package Protocol;

import java.util.LinkedList;

public class Concens {
    private String paquetID;
    private LinkedList<ConcensCops> hash;

    public Concens() {
        hash = new LinkedList<ConcensCops>();
    }


    public String getPaquetID() {
        return paquetID;
    }

    public void setPaquetID(String paquetID) {
        this.paquetID = paquetID;
    }

    public LinkedList<ConcensCops> getHash() {
        return hash;
    }

    public void setHash(LinkedList<ConcensCops> hash) {
        this.hash = hash;
    }
}




