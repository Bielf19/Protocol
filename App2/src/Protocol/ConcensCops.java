package Protocol;

public class ConcensCops {
    private String hash;
    private int cops;

    public ConcensCops() {
        cops = 1;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getCops() {
        return cops;
    }

    public void setCops(int cops) {
        this.cops = cops;
    }

    public void addCop() {
        this.cops ++;
    }
}
