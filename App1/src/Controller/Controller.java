package Controller;

import Model.Model;
import Network.ClientConnection;
import Protocol.Concens;
import Protocol.Reputacio;
import View.View;
import Model.Status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;

public class Controller implements ActionListener {

    private Model model;
    private View view;
    private ClientConnection networkClient;
    private int idCounter;
    private String next;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        idCounter = 0;
        next = "";

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("CON")) {

            try {
                networkClient =  new ClientConnection();
            } catch (IOException p) {
                p.printStackTrace();
            }
        }

        if (actionCommand.equals("SEND")) {
            String s = "" + model.getOwnId() + "-" + idCounter;
            model.setPaquetID(s);
            idCounter++;
            File f = new File(new File("").getAbsolutePath() + "/images/img8.jpg");
            model.toSent(f);
            try {
                networkClient =  new ClientConnection();
            } catch (IOException p) {
                p.printStackTrace();
            }
            networkClient.IniciaConnexioMultiCast(this,model,1,null);
        }

        if (actionCommand.equals("LIST")) {
            for (int i = 0; i < model.getStatusPaquets().size(); i++) {
                view.appendText("PaquetID: " + model.getStatusPaquets().get(i).getPaquetID() + " - Status: " + model.getStatusPaquets().get(i).getStatus());
            }
        }

        if (actionCommand.equals("SHOW")) {
            for (int i = 0; i < model.getReputacioOrdenada().size(); i++) {
                view.appendText("Node: " + model.getReputacioOrdenada().get(i).getNode() + " - Reputació: " + model.getReputacioOrdenada().get(i).getReputacio());
            }
            for (int i = 0; i < model.getReport().size(); i++) {
                System.out.println(model.getReport().get(i).getNodeID() + "," +
                        model.getReport().get(i).getPaquetID() + "," +
                        model.getReport().get(i).getReputacions_inicials().getLast().getNode() + "," +
                        model.getReport().get(i).getReputacions_inicials().getLast().getReputacio() + "," +
                        model.getReport().get(i).getStatus_inicial() + "," +
                        model.getReport().get(i).getIntents() + "," +
                        model.getReport().get(i).getStatus_final() + "," +
                        (model.getReport().get(i).getTimestamp_final() - model.getReport().get(i).getTimestamp_inicial()) + "," +
//                        model.getReport().get(i).getReputacions_finals().getLast().getNode() + "," +
//                        model.getReport().get(i).getReputacions_finals().getLast().getReputacio() + "," +
                        model.getReport().get(i).getProbabilitat_error());
            }
        }
    }

    public void difussio(String paquetID) {
        try {
            networkClient =  new ClientConnection();
        } catch (IOException p) {
            p.printStackTrace();
        }
        Timestamp t = networkClient.IniciaConnexioMultiCast(this,model,2,paquetID);
        while (new Timestamp(System.currentTimeMillis()).getTime() - t.getTime() < 1000) {

        }
        LinkedList<Concens> c = model.getConcens();
        int maxCops = 0;
        int indexI = 0;
        int indexX = 0;
        for (int i = 0; i < c.size(); i++) {
            if (c.get(i).getPaquetID().equals(paquetID)) {
                indexI = i;
                for (int x = 0; x < c.get(i).getHash().size(); x++) {
                    System.out.println("Hash de " + paquetID + ": " + c.get(i).getHash().get(x).getHash() + " - cops: " + c.get(i).getHash().get(x).getCops());
                    if (c.get(i).getHash().get(x).getCops() > maxCops) {
                        maxCops = c.get(i).getHash().get(x).getCops();
                        indexX = x;
                    }
                }
            }
        }
        model.getStatusPaquet(paquetID).setHashEsperat(c.get(indexI).getHash().get(indexX).getHash());
        System.out.println("Hash esperat: " + model.getStatusPaquet(paquetID).getHashEsperat());

        if (model.getStatusPaquet(paquetID).getStatus() == 1 && model.getStatusPaquet(paquetID).getConcens() < 0) {
            model.getStatusPaquet(paquetID).setStatus(0);
            System.out.println("PaquetID " + paquetID + " canvia Status");
        }
        buscaPaquet(paquetID);
    }

    public synchronized void buscaPaquet(String pID) {
        LinkedList<Status> l = model.getStatusPaquets();
        for (int i = 0; i < l.size(); i++) {
            if (pID.equals(l.get(i).getPaquetID())) {
                if(l.get(i).getStatus() == 0) {
                    int buscant = 0;
                    view.appendText("Buscant " + pID + " ...");
                    LinkedList<Reputacio> r = model.getReputacioOrdenada();
                    /*for (int z = 0; z < r.size(); z++) {
                        view.appendText("Node: " + r.get(z).getNode() + ", reputacio: " + r.get(z).getReputacio());
                    }*/
                    int x = r.size() - 1;
                    while (model.getStatusPaquets().get(i).getStatus() == 0 && buscant <= 1) {
                        next = "";
                        try {
                            networkClient =  new ClientConnection();
                        } catch (IOException p) {
                            p.printStackTrace();
                        }
                        view.appendText("Demana paquet " + pID + " a node " + r.get(x).getNode() + " amb reputacio " + r.get(x).getReputacio());
                        networkClient.IniciaConnexioUniCast(this,model,3,l.get(i).getPaquetID(),r.get(x).getNode(),-1);
                        model.setReportAugmentaIntents(pID);
                        Long t = new Timestamp(System.currentTimeMillis()).getTime();
                        while (!next.equals(pID) && new Timestamp(System.currentTimeMillis()).getTime() - t < 100) {

                        }
                        view.appendText("PASSA NEXT");
                        if (x == 0) {
                            buscant++;
                            x = r.size() - 1;
                        } else {
                            x--;
                        }
                        view.appendText("Condicions sortida bucle: " + l.get(i).getStatus() + ", buscant: " + buscant);
                    }

                }
            }
        }
    }

    public void setNext(String n) {
        next = n;
    }

    public void appendText(String s) {
        view.appendText(s);
    }

    public void canviaReputacio(String pID, int node, int reputacio) {
        try {
            networkClient =  new ClientConnection();
        } catch (IOException p) {
            p.printStackTrace();
        }
        networkClient.IniciaConnexioUniCast(this,model,4,pID,node,reputacio);
    }



    /*public void comprovaValors(int id) {
        try {
            networkClient =  new ClientConnection();
        } catch (IOException p) {
            p.printStackTrace();
        }
        networkClient.IniciaConnexio(this,model,2,id);
    }*/
}
