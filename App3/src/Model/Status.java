package Model;

public class Status {
    private String paquetID;
    private int status;
    private byte[] paquet;
    private int concens = 0;
    private String hashEsperat;

    public String getPaquetID() {
        return paquetID;
    }

    public void setPaquetID(String paquetID) {
        this.paquetID = paquetID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getPaquet() {
        return paquet;
    }

    public void setPaquet(byte[] paquet) {
        this.paquet = paquet;
    }

    public int getConcens() {
        return concens;
    }

    public void setConcens(int concens) {
        this.concens = concens;
    }

    public String getHashEsperat() {
        return hashEsperat;
    }

    public void setHashEsperat(String hashEsperat) {
        this.hashEsperat = hashEsperat;
    }
}
