package Model;

import Protocol.Reputacio;

import java.util.LinkedList;

public class Report {
    private int nodeID;
    private boolean sender;
    private String paquetID;
    private long timestamp_inicial;
    private int status_inicial;
    private LinkedList<Reputacio> reputacions_inicials;
    private int intents;
    private int status_final;
    private LinkedList<Reputacio> reputacions_finals;
    private long timestamp_final;
    private float probabilitat_error;

    public Report () {
        reputacions_inicials = new LinkedList<Reputacio>();
        reputacions_finals = new LinkedList<Reputacio>();
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public boolean isSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }

    public String getPaquetID() {
        return paquetID;
    }

    public void setPaquetID(String paquetID) {
        this.paquetID = paquetID;
    }

    public long getTimestamp_inicial() {
        return timestamp_inicial;
    }

    public void setTimestamp_inicial(long timestamp_inicial) {
        this.timestamp_inicial = timestamp_inicial;
    }


    public int getStatus_inicial() {
        return status_inicial;
    }

    public void setStatus_inicial(int status_inicial) {
        this.status_inicial = status_inicial;
    }


    public LinkedList<Reputacio> getReputacions_inicials() {
        return reputacions_inicials;
    }

    public void setReputacions_inicials(LinkedList<Reputacio> reputacions_inicials) {
        this.reputacions_inicials = reputacions_inicials;
    }

    public LinkedList<Reputacio> getReputacions_finals() {
        return reputacions_finals;
    }

    public void setReputacions_finals(LinkedList<Reputacio> reputacions_finals) {
        this.reputacions_finals = reputacions_finals;
    }

    public int getIntents() {
        return intents;
    }

    public void setIntents(int intents) {
        this.intents = intents;
    }

    public int getStatus_final() {
        return status_final;
    }

    public void setStatus_final(int status_final) {
        this.status_final = status_final;
    }

    public long getTimestamp_final() {
        return timestamp_final;
    }

    public void setTimestamp_final(long timestamp_final) {
        this.timestamp_final = timestamp_final;
    }

    public float getProbabilitat_error() {
        return probabilitat_error;
    }

    public void setProbabilitat_error(float probabilitat_error) {
        this.probabilitat_error = probabilitat_error;
    }
}
