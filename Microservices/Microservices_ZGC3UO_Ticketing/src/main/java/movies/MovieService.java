package movies;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface MovieService {

    @GET
    @Path("movies")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Movies.MovieList getAllMovies();

    @GET
    @Path("movies/{id}")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Response getMovieById(@PathParam("id") int id);

    @POST
    @Path("movies")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Movies.MovieId postMovie(Movies.Movie movie);

    @PUT
    @Path("movies/{id}")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public void insertOrUpdateMovie(@PathParam("id") int id, Movies.Movie movie);

    @DELETE
    @Path("movies/{id}")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public void deleteMovie(@PathParam("id") int id);

    @GET
    @Path("movies/find")
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON})
    public Response findMoviesByFields(@QueryParam("year") int year, @QueryParam("orderby") String field);
}
