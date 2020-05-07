package hu.soi.movies;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "movies")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class IdList {

    @XmlElement(name = "id")
    private List<Integer> ids;

    public IdList() {
        ids = new ArrayList<Integer>();
    }

    public void add(int id) {
        ids.add(id);
    }

    public List<Integer> getIdList() {
        return ids;
    }
}
