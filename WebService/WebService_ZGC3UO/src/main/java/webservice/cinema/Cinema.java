package webservice.cinema;

import javax.jws.WebService;

import seatreservation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@WebService(
        name = "CinemaService",
        portName = "ICinema_HttpSoap11_Port",
        targetNamespace = "http://www.iit.bme.hu/soi/hw/SeatReservation",
        endpointInterface = "seatreservation.ICinema",
        wsdlLocation = "WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema {
    private static List<MySeat> seats;
    private static int columnNumber;
    private static int lockId = 1;
    private static ArrayList<String> reservationList = new ArrayList<>();

    @Override
    public void init(int rows, int columns) throws ICinemaInitCinemaException {
        if ((rows >= 1) && (rows <= 26) && (columns >= 1) && (columns <= 100)) {
            columnNumber = columns;
            seats = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    seats.add(new MySeat(i, j));
                }
            }
        } else {
            throw new ICinemaInitCinemaException("Rows or columns out of range",
                    new CinemaException());
        }
    }

    @Override
    public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
        ArrayOfSeat arrayOfSeat = new ArrayOfSeat();
        for (MySeat mySeat : seats) {
            seatreservation.Seat seat = new seatreservation.Seat();
            seat.setRow(mySeat.getRow());
            seat.setColumn(mySeat.getColumn());
            arrayOfSeat.getSeat().add(seat);
        }
        return arrayOfSeat;
    }

    @Override
    public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
        for (MySeat mySeat : seats) {
            if (seat.getRow().equals(mySeat.getRow()) && seat.getColumn().equals(mySeat.getColumn())) {
                return mySeat.getStatus();
            }
        }
        throw new ICinemaGetSeatStatusCinemaException("There is no such seat", new CinemaException());
    }

    @Override
    public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
        if (count <= 0) throw new ICinemaLockCinemaException("Invalid Number", new CinemaException());
        if ((Integer.parseInt(seat.getColumn()) + count - 1) > columnNumber)
            throw new ICinemaLockCinemaException("The number of the seats are not enough in the row",
                    new CinemaException());
        for (MySeat mySeat : seats) {
            if (seat.getRow().equals(mySeat.getRow()) && seat.getColumn().equals(mySeat.getColumn())) {
                for (MySeat mySeat1 : seats) {
                    if (mySeat.getRow().equals(mySeat1.getRow()) &&
                            Integer.parseInt(mySeat.getColumn()) <= Integer.parseInt(mySeat1.getColumn())) {
                        if (mySeat1.getStatus() != SeatStatus.FREE) {
                            throw new ICinemaLockCinemaException("There is a lock in the requested seats",
                                    new CinemaException());
                        }
                    }
                }
                for (MySeat mySeat1 : seats) {
                    if (mySeat.getRow().equals(mySeat1.getRow()) &&
                            Integer.parseInt(mySeat.getColumn()) <= Integer.parseInt(mySeat1.getColumn())) {
                        mySeat1.setStatus(SeatStatus.LOCKED);
                        mySeat1.setReservationId(lockId);
                    }
                }
                String lock = Integer.toString(lockId);
                reservationList.add(lock);
                lockId++;
                return lock;
            }
        }
        throw new ICinemaLockCinemaException("There is no seat", new CinemaException());
    }

    @Override
    public void unlock(String lockId) throws ICinemaUnlockCinemaException {
        if (reservationList.contains(lockId)) {
            for (MySeat mySeat : seats) {
                if (Integer.toString(mySeat.getReservationId()).equals(lockId) &&
                        mySeat.getStatus() == SeatStatus.LOCKED) {
                    mySeat.setReservationId(0);
                    mySeat.setStatus(SeatStatus.FREE);
                }
            }
            reservationList.remove(lockId);
        } else throw new ICinemaUnlockCinemaException("There is no such lock ID", new CinemaException());
    }

    @Override
    public void reserve(String lockId) throws ICinemaReserveCinemaException {
        if (reservationList.contains(lockId)) {
            for (MySeat mySeat : seats) {
                if (Integer.toString(mySeat.getReservationId()).equals(lockId) &&
                        mySeat.getStatus() == SeatStatus.LOCKED) {
                    mySeat.setStatus(SeatStatus.RESERVED);
                }
            }
        } else throw new ICinemaReserveCinemaException("There is no such lock ID", new CinemaException());
    }

    @Override
    public void buy(String lockId) throws ICinemaBuyCinemaException {
        if (reservationList.contains(lockId)) {
            for (MySeat mySeat : seats) {
                if (Integer.toString(mySeat.getReservationId()).equals(lockId) &&
                        (mySeat.getStatus() == SeatStatus.LOCKED ||
                                mySeat.getStatus() == SeatStatus.RESERVED)) {
                    mySeat.setStatus(SeatStatus.SOLD);
                }
            }
            reservationList.remove(lockId);
        } else throw new ICinemaBuyCinemaException("The is no such lock ID", new CinemaException());
    }
}
