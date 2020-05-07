package webservice.cinema;

import seatreservation.SeatStatus;

public class MySeat {
    private String row;
    private String column;
    private SeatStatus status = SeatStatus.FREE;
    private int reservationId = 0;

    public MySeat(int row, int column) {
        this.row = Character.toString((char)(row + 'A'));
        this.column = Integer.toString(column + 1);
    }

    public String getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
}
