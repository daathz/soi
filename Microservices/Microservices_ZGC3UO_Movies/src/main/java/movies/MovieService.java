package movies;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class MovieService {
    private static MyMovies movies = new MyMovies();

    @GET
    @Path("movies")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Movies.MovieList getAllMovies() {
        return movies.getProto();
    }

    @GET
    @Path("movies/{id}")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Response getMovieById(@PathParam("id") int id) {
        for (Movie movie : movies.getMovies()) {
            if (id == movie.getId()) {
                return Response.ok(movie).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("movies")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Movies.MovieId postMovie(Movies.Movie movie) {
        String[] actors = new String[movie.getActorCount()];
        for (int i = 0; i < movie.getActorCount(); ++i) {
            actors[0] = movie.getActor(0);
        }
        Movie item = new Movie(movie.getTitle(), movie.getYear(), movie.getDirector(), actors);
        movies.add(item);
        Result result = new Result();
        result.setId(item.getId());
        return result.getProto();
    }

    @PUT
    @Path("movies/{id}")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public void insertOrUpdateMovie(@PathParam("id") int id, Movies.Movie movie) {
        for (Movie item : movies.getMovies()) {
            if (id == item.getId()) {
                movies.remove(item);
            }
        }
        Movie m = new Movie();
        m.setId(id);
        m.setTitle(movie.getTitle());
        m.setYear(movie.getYear());
        m.setDirector(movie.getDirector());
        String[] actors = new String[movie.getActorCount()];
        for (int i = 0; i < movie.getActorCount(); ++i) {
            actors[0] = movie.getActor(0);
        }
        m.setActors(actors);
        movies.add(m);
    }

    @DELETE
    @Path("movies/{id}")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public void deleteMovie(@PathParam("id") int id) {
        for (Movie movie : movies.getMovies()) {
            if (movie.getId() == id) {
                movies.remove(movie);
            }
        }
    }

    @GET
    @Path("movies/find")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Response findMoviesByFields(@QueryParam("year") int year, @QueryParam("orderby") String field) {
        IdList idList = new IdList();
        List<Movie> foundMovies = new ArrayList<>();
        for (Movie movie : movies.getMovies()) {
            if (movie.getYear() == year) {
                foundMovies.add(movie);
            }
        }
        if (field.equals("Title")) {
            foundMovies.sort(new TitleComparator());
        } else if (field.equals("Director")) {
            foundMovies.sort(new DirectorComparator());
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        for (Movie movie : foundMovies) {
            idList.add(movie.getId());
        }
        return Response.ok(idList).build();
    }
}
