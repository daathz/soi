package movies;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Result {

    @XmlElement(name = "id")
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movies.MovieId getProto() {
        return Movies.MovieId.newBuilder().setId(this.getId()).build();
    }
}
