package cinema;

public class Seat {

    private int row;
    private int column;
    private SeatStatus status;
    private String lockId;

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
        status = SeatStatus.FREE;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
}
