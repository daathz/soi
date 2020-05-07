package ticketing;

import banking.Banking;
import banking.CreditCard;
import movies.MovieService;
import movies.Movies;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/ticketing")
public class TicketService {

    private static SoldTickets soldTickets = new SoldTickets();
    private static String MoviesAddress;
    private static String BankingAddress;

    private static String getMoviesAddress() {
        if (MoviesAddress == null) {
            MoviesAddress = System.getProperty("microservices.movies.url");
        }
        return MoviesAddress;
    }

    private static String getBankingAddress() {
        if (BankingAddress == null) {
            BankingAddress = System.getProperty("microservices.banking.url");
        }
        return BankingAddress;
    }

    private MovieService getMoviesBackend() {
        String backendAddress = TicketService.getMoviesAddress();
        if (backendAddress == null)
            return null;
        try {
            ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
            ResteasyClient client = new ResteasyClientBuilder().providerFactory(instance).build();
            ResteasyWebTarget target = client.target(backendAddress);
            MovieService backend = target.proxy(MovieService.class);
            return backend;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private CreditCard getBankingBackend() {
        String backendAddress = TicketService.getBankingAddress();
        if (backendAddress == null)
            return null;
        try {
            ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
            ResteasyClient client = new ResteasyClientBuilder().providerFactory(instance).build();
            ResteasyWebTarget target = client.target(backendAddress);
            CreditCard backend = target.proxy(CreditCard.class);
            return backend;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Path("GetMovies")
    public Ticketing.GetMoviesResponse getMovies(Ticketing.GetMoviesRequest request) {
        MovieService moviesBackend = this.getMoviesBackend();
        if (moviesBackend == null) return null;

        Movies.MovieIdList movieIdList = moviesBackend.findMoviesByFields(request.getYear(), "Title")
                .readEntity(Movies.MovieIdList.class);
        List<Integer> idList = movieIdList.getIdList();
        List<Ticketing.Movie> result = new ArrayList<>();

        for (Integer item : idList) {
            Movies.Movie movie = moviesBackend.getMovieById(item).readEntity(Movies.Movie.class);
            result.add(Ticketing.Movie.newBuilder().setId(item).setTitle(movie.getTitle()).build());
        }

        return Ticketing.GetMoviesResponse.newBuilder().addAllMovie(result).build();
    }

    @POST
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Path("BuyTickets")
    public Ticketing.BuyTicketsResponse buyTickets(Ticketing.BuyTicketsRequest request) {
        MovieService moviesBackend = this.getMoviesBackend();
        CreditCard bankingBackend = this.getBankingBackend();
        if (moviesBackend == null || bankingBackend == null) {
            return null;
        }

        int price = request.getCount() * 10;
        Banking.ChargeCardRequest chargeCardRequest = Banking.ChargeCardRequest.newBuilder()
                .setCardNumber(request.getCardNumber())
                .setAmount(price)
                .build();
        Banking.ChargeCardResponse chargeCardResponse = bankingBackend.charge(chargeCardRequest);

        if (chargeCardResponse.getSuccess()) {
            soldTickets.sell(request.getMovieId(), request.getCount());
        }
        return Ticketing.BuyTicketsResponse.newBuilder().setSuccess(chargeCardResponse.getSuccess()).build();
    }

    @POST
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Path("GetTickets")
    public Ticketing.GetTicketsResponse getTickets(Ticketing.GetTicketsRequest request) {
        return soldTickets.getProto();
    }
}
