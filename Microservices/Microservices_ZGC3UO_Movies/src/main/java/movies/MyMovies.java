package movies;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "movies")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Movies")
@XmlSeeAlso(Movie.class)
public class MyMovies {

    @XmlElement(name = "movie")
    private List<Movie> movies;

    public MyMovies() {
            movies = new ArrayList<>();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void add(Movie movie) {
        movies.add(movie);
    }

    public void remove(Movie movie) {
        movies.remove(movie);
    }

    public Movies.MovieList getProto() {
        Movies.MovieList.Builder movieListBuilder = Movies.MovieList.newBuilder();

        for (Movie movie: this.movies) {
            Movies.Movie m = movie.getProto();
            movieListBuilder.addMovie(m);
        }

        return movieListBuilder.build();
    }
}
