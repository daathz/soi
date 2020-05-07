package ticketing;

import java.util.ArrayList;
import java.util.List;

public class SoldTickets {
    private List<Ticket> tickets = new ArrayList<>();


    public void sell(int id, int count) {
        for (Ticket ticket: tickets) {
            if (ticket.getMovieId() == id) {
                ticket.addCount(count);
                return;
            }
        }

        Ticket ticket = new Ticket();
        ticket.setMovieId(id);
        ticket.setCount(count);
        tickets.add(ticket);
    }

    public Ticketing.GetTicketsResponse getProto() {
        List<Ticketing.Ticket> list = new ArrayList<Ticketing.Ticket>();
        for (Ticket ticket: tickets) {
            list.add(ticket.getProto());
        }
        return Ticketing.GetTicketsResponse.newBuilder().addAllTicket(list).build();
    }
}