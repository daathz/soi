package cinema;

import javax.xml.ws.BindingProvider;

import seatreservation.*;

public class Program {
    public static void main(String[] args) {
        CinemaService cinemaService = new CinemaService();
        ICinema cinema = cinemaService.getICinemaHttpSoap11Port();
        BindingProvider bp = (BindingProvider)cinema;
        bp.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                //"http://localhost:8080/WebService_ZGC3UO/Cinema");
                args[0]);

        if (args.length != 4) {
            System.out.println("There is not enough parameter.");
            System.exit(0);
        }

        Seat seat = new Seat();
        seat.setRow(args[1]);
        seat.setColumn(args[2]);

        switch (args[3]) {
            case "Lock":
                try {
                    cinema.lock(seat, 1);
                } catch (ICinemaLockCinemaException iclce) {
                    iclce.printStackTrace();
                }
                break;
            case "Reserve":
                try {
                    String lockId = cinema.lock(seat, 1);
                    cinema.reserve(lockId);
                } catch (ICinemaLockCinemaException | ICinemaReserveCinemaException e) {
                    e.printStackTrace();
                }
                break;
            case "Buy":
                try {
                    String lockId = cinema.lock(seat, 1);
                    cinema.buy(lockId);
                } catch (ICinemaLockCinemaException | ICinemaBuyCinemaException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Task is not valid");
                break;
        }
    }
}
