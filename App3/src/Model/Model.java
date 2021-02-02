package Model;

import Network.ConfiguracioClient;
import Protocol.Concens;
import Protocol.ConcensCops;
import Protocol.Reputacio;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class Model {

    private static final int ownId = 3;

    private LinkedList<Reputacio> reputacions;
    private ConfiguracioClient[] nodes;
    private String paquetID;
    private File toSent;
    private LinkedList<Status> statusPaquets;
    private LinkedList<Report> report;
    private LinkedList<Concens> concens;


    public Model() {

        ConfiguracioClient config = new ConfiguracioClient();
        try {
            nodes = config.llegeixJsonClient();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reputacions = new LinkedList<Reputacio>();
        report = new LinkedList<Report>();
        for (int i = 0;  i < nodes.length; i++) {
            Reputacio r = new Reputacio();
            r.setReputacio(500);
            r.setNode(nodes[i].getId());
            reputacions.add(r);
        }
        statusPaquets = new LinkedList<Status>();
        concens = new LinkedList<Concens>();

    }

    public int getOwnId() {
        return ownId;
    }


    public void setPaquetID(String s) {
        paquetID = s;
    }

    public String getPaquetID() {
        return paquetID;
    }

    public void toSent(File f) {
        toSent = f;
    }

    public File getToSent() {
        return toSent;
    }

    public String getWrongPath() {
        Random random = new Random();
        int n = random.nextInt(7);
        return new File("").getAbsolutePath() + "/images/img" + n + ".jpg";
    }

    public LinkedList<Status> getStatusPaquets() {
        return statusPaquets;
    }

    public void addPaquet(Status s) {
        statusPaquets.add(s);
    }

    public void deletePaquet(String paquetID) {
        for (int i = 0; i < statusPaquets.size(); i++) {
            if (statusPaquets.get(i).getPaquetID().equals(paquetID)) {
                statusPaquets.remove(i);
            }
        }
    }

    public LinkedList<Reputacio> getReputacions() {
        return reputacions;
    }

    public int getIndexReputacions(int id) {
        for (int i = 0; i < reputacions.size(); i++) {
            if (reputacions.get(i).getNode() == id) {
                return i;
            }
        }
        return -1;
    }

    public LinkedList<Reputacio> getReputacioOrdenada() {
        reputacions.sort(new ReputationComparator());
        return reputacions;
    }


    public Status getStatusPaquet(String paquet_id) {
        for (int i = 0; i < statusPaquets.size(); i++) {
            if (statusPaquets.get(i).getPaquetID().equals(paquet_id)) {
                return statusPaquets.get(i);
            }
        }
        return null;
    }

    public void guardaPaquet(byte[] paquet, String pID) {
        for (int i = 0; i < statusPaquets.size(); i++) {
            if (statusPaquets.get(i).getPaquetID().equals(pID)) {
                statusPaquets.get(i).setPaquet(paquet);
                statusPaquets.get(i).setStatus(1);
            }
        }
    }

    public synchronized void addPaquetInicial(Status sp) {
        //Afegim el paquet inicial a la llista i comprovem que ho faci només una vegada ja que si no s'afegirà tantes vegades com threads hi hagi
        boolean trobat = false;
        for (int i = 0; i < statusPaquets.size(); i++) {
            if (statusPaquets.get(i).getPaquetID().equals(sp.getPaquetID())) {
                trobat = true;
            }
        }
        if (!trobat) {
            statusPaquets.add(sp);
        }
    }

    //REPORT
    public void setReportInici(String pID, Timestamp timestamp, boolean sender, float prob) {
        Report r = new Report();
        r.setNodeID(getOwnId());
        r.setPaquetID(pID);
        r.setSender(sender);
        r.setTimestamp_inicial(timestamp.getTime());
        r.setIntents(0);
        r.setStatus_final(0);
        r.setProbabilitat_error(prob);
        Reputacio rep = new Reputacio();
        for (int i = 0; i < reputacions.size(); i++) {
            rep.setNode(reputacions.get(i).getNode());
            rep.setReputacio(reputacions.get(i).getReputacio());
            r.getReputacions_inicials().add(rep);
        }
        report.add(r);
    }


    public void setReportStatusInicial(String pID, int status) {
        for (int i = 0; i < report.size(); i++) {
            if (report.get(i).getPaquetID().equals(pID)) {
                report.get(i).setStatus_inicial(status);
                if (status == 1) {
                    report.get(i).setTimestamp_final(new Timestamp(System.currentTimeMillis()).getTime());
                    report.get(i).setStatus_final(1);
                    Reputacio rep = new Reputacio();
                    for (int x = 0; x < reputacions.size(); x++) {
                        rep.setNode(reputacions.get(x).getNode());
                        rep.setReputacio(reputacions.get(x).getReputacio());
                        report.get(i).getReputacions_finals().add(rep);
                    }
                }
            }
        }
    }

    public void setReportAugmentaIntents(String pID) {
        for (int i = 0; i < report.size(); i++) {
            if (report.get(i).getPaquetID().equals(pID)) {
                report.get(i).setIntents(report.get(i).getIntents() + 1);
            }
        }
    }

    public void setReportFinal(String pID, int status) {
        for (int i = 0; i < report.size(); i++) {
            if (report.get(i).getPaquetID().equals(pID)) {
                report.get(i).setStatus_final(status);
                Reputacio rep = new Reputacio();
                for (int x = 0; x < reputacions.size(); x++) {
                    rep.setNode(reputacions.get(x).getNode());
                    rep.setReputacio(reputacions.get(x).getReputacio());
                    report.get(i).getReputacions_finals().add(rep);
                }
                report.get(i).setTimestamp_final(new Timestamp(System.currentTimeMillis()).getTime());
            }
        }
    }

    public LinkedList<Report> getReport() {
        return report;
    }

    public void addHashConcens(String pID, String hash) {
        boolean trobat1 = false;
        boolean trobat2 = false;
        int index = 0;
        //Busca que el paquetID estigui a la llista de concens
        if (concens.size() != 0) {
            for (int i = 0; i < concens.size(); i++) {
                if (concens.get(i).getPaquetID().equals(pID)) {
                    trobat1 = true;
                    index = i;
                    //Busca que el md5 estigui a la llista de hash
                    for (int x = 0; x < concens.get(i).getHash().size(); x++) {
                        if (concens.get(i).getHash().get(x).getHash().equals(hash)) {
                            trobat2 = true;
                            concens.get(i).getHash().get(x).addCop();
                        }
                    }
                }
            }
        }
        //Afegeix un paquetID a la llista de concens
        if (!trobat1) {
            Concens c = new Concens();
            ConcensCops cc = new ConcensCops();
            cc.setHash(hash);
            c.setPaquetID(pID);
            c.getHash().add(cc);
            concens.add(c);
            trobat2 = true;
        }
        //Afegeix un nou md5 a la llista de hash
        if (!trobat2) {
            ConcensCops cc = new ConcensCops();
            cc.setHash(hash);
            concens.get(index).getHash().add(cc);
        }
    }

    public LinkedList<Concens> getConcens() {
        return concens;
    }
}


class ReputationComparator implements Comparator<Reputacio>  {
    @Override
    public int compare(Reputacio r1, Reputacio r2)
    {
        if(r1.getReputacio()==r2.getReputacio())
            return 0;
        else if(r1.getReputacio()>r2.getReputacio())
            return 1;
        else
            return -1;
    }
}
