package hu.soi.movies;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("MovieDatabase")
public class MovieService {
    private static Movies movies = new Movies();

    @GET
    @Path("movies")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Movies getAllMovies() {
        return movies;
    }

    @GET
    @Path("movies/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Result postMovie(Movie movie) {
        Movie item = new Movie(movie.getTitle(), movie.getYear(), movie.getDirector(), movie.getActors());
        movies.add(item);
        Result result = new Result();
        result.setId(item.getId());
        return result;
    }

    @PUT
    @Path("movies/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void insertOrUpdateMovie(@PathParam("id") int id, Movie movie) {
        for (Movie item : movies.getMovies()) {
            if (id == item.getId()) {
                movies.remove(item);
            }
        }
        movie.setId(id);
        movies.add(movie);
    }

    @DELETE
    @Path("movies/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void deleteMovie(@PathParam("id") int id) {
        for (Movie movie : movies.getMovies()) {
            if (movie.getId() == id) {
                movies.remove(movie);
            }
        }
    }

    @GET
    @Path("movies/find")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
