package hu.soi.movies;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "movies")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Movies")
@XmlSeeAlso(Movie.class)
public class Movies {

    @XmlElement(name = "movie")
    private List<Movie> movies;

    public Movies() {
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
}
