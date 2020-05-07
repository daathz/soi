package ticketing;

public class Ticket {

    private int movieId;
    private int count;

    public Ticket() { }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int id) {
        movieId = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int plus) {
        count += plus;
    }

    public Ticketing.Ticket getProto() {
        return Ticketing.Ticket.newBuilder().setMovieId(this.getMovieId()).setCount(this.getCount()).build();
    }
}