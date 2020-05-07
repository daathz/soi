package movies;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "movie")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Movie {
    private static int Sid = 1;

    @XmlTransient
    private int id;
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "year")
    private int year;
    @XmlElement(name = "director")
    private String director;
    @XmlElement(name = "actor")
    private String[] actors;

    public Movie() {
        super();
    }

    public Movie(String title, int year, String director, String[] actors) {
        id = Sid++;
        this.title = title;
        this.year = year;
        this.director = director;
        this.actors = new String[actors.length];
        for (int i = 0; i < actors.length; ++i) {
            this.actors[i] = actors[i];
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String[] getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        this.actors = actors;
    }

    public Movies.Movie getProto() {
        Movies.Movie.Builder movieBuilder = Movies.Movie.newBuilder();
        movieBuilder.setTitle(this.getTitle()).setYear(this.getYear()).setDirector(this.getDirector());
        for (String actor: this.actors) {
            movieBuilder.addActor(actor);
        }
        return movieBuilder.build();
    }
}
