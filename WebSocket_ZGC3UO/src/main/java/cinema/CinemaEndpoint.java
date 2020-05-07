package cinema;

import cinema.messaging.Message;
import cinema.messaging.MessageDecoder;
import cinema.messaging.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint(value = "/cinema",
        encoders = {MessageEncoder.class},
        decoders = {MessageDecoder.class})
public class CinemaEndpoint {
    private static int rows = 0;
    private static int columns = 0;
    private static int lockId = 1;

    private static MessageEncoder messageEncoder;
    private static Set<Session> connections;

    private static List<Seat> seats;
    private static List<String> reservations;

    public CinemaEndpoint() {
        if (connections == null) {
            connections = Collections.synchronizedSet(new HashSet<Session>());
        }
        messageEncoder = new MessageEncoder();
    }

    @OnOpen
    public void open(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
        connections.add(session);
    }

    @OnClose
    public void close(Session session) {
        System.out.println("WebSocket closed: " + session.getId());
        connections.remove(session);
    }

    @OnError
    public void error(Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    @OnMessage
    public void message(Message message, Session session) {
        System.out.println("Websocket message: " + message);

        switch (message.getType()) {
            case "initRoom":
                initRoom(message, session);
                break;
            case "getRoomSize":
                getRoomSize(session);
                break;
            case "updateSeats":
                updateSeats(session);
                break;
            case "lockSeat":
                lockSeat(message, session);
                break;
            case "unlockSeat":
                unlockSeat(message, session);
                break;
            case "reserveSeat":
                reserveSeat(message, session);
                break;
            default:
                System.out.println("Wrong message: " + message.getType());
                break;
        }
    }

    private void sendMessage(Message message, Session session) {
        try {
            session.getBasicRemote().sendText(messageEncoder.encode(message));
        } catch (IOException e) {
            System.out.println("IOException at " +session.getId());
            e.printStackTrace();
        } catch (EncodeException e) {
            System.out.println("Encodin Exception");
            e.printStackTrace();
        }
    }

    private void sendError(String error, Session session) {
        Message errorMessage = new Message();
        errorMessage.setType("error");
        errorMessage.addProperty("message", error);
        sendMessage(errorMessage, session);
    }

    private void broadcastMessage(Message message) {
        synchronized (connections) {
            for (Session session : connections) {
                sendMessage(message, session);
            }
        }
    }

    private void initRoom(Message message, Session session) {
        int messageRows = Integer.parseInt(message.getProperties().get("rows").toString());
        int messageColumns = Integer.parseInt(message.getProperties().get("columns").toString());

        if ( messageRows < 1 || messageColumns < 1) {
            sendError("Invalid init numbers", session);
            return;
        }

        System.out.println("Initializing room.");

        rows = messageRows;
        columns = messageColumns;
        seats = new ArrayList<>();
        reservations = new ArrayList<>();

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                seats.add(new Seat(i, j));
            }
        }
    }

    private void getRoomSize(Session session) {
        System.out.println("getRoomSize");
        Message message = new Message();
        message.setType("roomSize");
        message.addProperty("rows", rows);
        message.addProperty("columns", columns);
        sendMessage(message, session);
    }

    private void updateSeats(Session session) {
        System.out.println("updateSeats");
        for (Seat seat : seats) {
            Message message = new Message();
            message.setType("seatStatus");
            message.addProperty("row", seat.getRow());
            message.addProperty("column", seat.getColumn());
            message.addProperty("status", seat.getStatus().toString().toLowerCase());
            sendMessage(message, session);
        }
    }

    private void lockSeat(Message message, Session session) {
        String id = null;
        if (rows < 1 || columns < 1) {
            sendError("Room is not initialized", session);
            return;
        }

        Map<String, Object> properties = message.getProperties();
        if (!(properties.containsKey("row") && properties.containsKey("column"))) {
            sendError("Invalid properties", session);
        } else {
            int row = Integer.parseInt(properties.get("row").toString());
            int column = Integer.parseInt(properties.get("column").toString());
            System.out.println("Lock seat at " + row + "," + column);

            if ((row <= 0 || row > rows + 1) || (column <= 0 || column > column + 1)) {
                sendError("Invalid row or column number", session);
                return;
            }

            for (Seat seat : seats) {
                if (seat.getRow() == row && seat.getColumn() == column) {
                    if (seat.getStatus() == SeatStatus.FREE) {
                        seat.setStatus(SeatStatus.LOCKED);
                        id = "lock" + lockId;
                        lockId++;
                        seat.setLockId(id);
                        reservations.add(id);
                    } else {
                        sendError("cinema.Seat is not free", session);
                        return;
                    }
                }
            }

            Message result = new Message();
            result.setType("lockResult");
            result.addProperty("lockId", id);
            sendMessage(result, session);
            broadcastMessage(result);
        }
    }

    private void unlockSeat(Message message, Session session) {
        String id = null;
        if (rows < 1 || columns < 1) {
            sendError("Room is not initialized", session);
            return;
        }

        Map<String, Object> properties = message.getProperties();
        if (properties.containsKey("lockId")) {
            id = properties.get("lockId").toString();
        } else {
            sendError("Invalid properties", session);
        }

        for (String lock : reservations) {
            if (lock.equals(id)) {
                for (Seat seat : seats) {
                    if (seat.getLockId().equals(id)) {
                        if(seat.getStatus() == SeatStatus.LOCKED) {
                            seat.setStatus(SeatStatus.FREE);
                            reservations.remove(id);
                            Message result = new Message();
                            result.setType("seatStatus");
                            result.addProperty("row", seat.getRow());
                            result.addProperty("column", seat.getColumn());
                            result.addProperty("status", "free");
                            broadcastMessage(result);
                        } else {
                            sendError("cinema.Seat is not locked", session);
                        }
                    }
                }
            } else {
                sendError("Lock doesn't exist", session);
            }
        }
    }

    private void reserveSeat(Message message, Session session) {
        String id = null;
        if (rows < 1 || columns < 1) {
            sendError("Room is not initialized", session);
            return;
        }

        Map<String, Object> properties = message.getProperties();
        if (properties.containsKey("lockId")) {
            id = properties.get("lockId").toString();
        } else {
            sendError("Invalid properties", session);
        }

        for (String lock : reservations) {
            if (lock.equals(id)) {
                for (Seat seat : seats) {
                    if (seat.getLockId().equals(id)) {
                        if(seat.getStatus() == SeatStatus.LOCKED) {
                            seat.setStatus(SeatStatus.RESERVED);
                            reservations.remove(id);
                            Message result = new Message();
                            result.setType("seatStatus");
                            result.addProperty("row", seat.getRow());
                            result.addProperty("column", seat.getColumn());
                            result.addProperty("status", "reserved");
                            broadcastMessage(result);
                        } else {
                            sendError("cinema.Seat is not locked", session);
                        }
                    }
                }
            } else {
                sendError("Lock doesn't exist", session);
            }
        }
    }
}
