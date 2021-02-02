package Network;

import ChaosMonkey.ChaosMonkey;
import Controller.Controller;
import Model.Model;
import Model.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;

public class ClientDedicat extends Thread {

    private static final int ownId = 1;
    private Controller controller;
    private Model model;
    private ChaosMonkey cm;
    //private ObjectInputStream ois;
    //private ObjectOutputStream oos;
    private Socket s;
    private int id;
    private Socket[] sockets;
    private int opcio;
    private int id_comp;
    private Timestamp timestamp;
    private String pID;
    private int reputation;

    public ClientDedicat(Controller c, Model m, ChaosMonkey cm, Socket s, int id, int opcio, Timestamp timestamp, String pID, int reputacio) {
        controller = c;
        model = m;
        this.cm = cm;
        this.s = s;
        this.id = id;
        this.opcio = opcio;
        this.timestamp = timestamp;
        this.pID = pID;
        reputation = reputacio;


    }

    @Override
    public void run() {

        try {
            //Creo els streams
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            Hash hash = new Hash();
            String md5;
            String md5origen;
            Status statusPaquet = new Status();

            int id_origen = 0;
            if (pID != null) {
                id_origen = getIdOrigen(pID);
            }

            //if(opcio == 2 && id != id_origen || opcio != 2) {
            oos.writeObject(opcio);
            /*} else {
                oos.writeObject(100);
                opcio = 100;
            }*/

            switch (opcio) {
                case 1:
                    //Agafa la imatge des d'un fitxer
                    byte[] imageData = cm.getByteArrayFromFile(model.getToSent().toString(), true);
                    //Calcul del hash de la imatge
                    md5 = hash.getMd5(imageData);
                    //Guardem la imatge a Status
                    statusPaquet.setPaquet(imageData);

                    //Enviem la imatge i el md5 i el paquetID
                    cm.writeObject(imageData,oos);
                    cm.writeObject(md5,oos);
                    oos.writeObject(model.getPaquetID());

                    //Guardem paquetID i status a Status i l'afegim a la llista de paquets
                    statusPaquet.setPaquetID(model.getPaquetID());
                    statusPaquet.setStatus(1);
                    ////////POTSER NO ES EL CORRECTE, PODRIA SER QUE EL PRIMER PAQUET QUE ENVIA NO SIGUI EL MATEIX QUE EL QUE ENVIA A LA RESTA
                    model.addPaquetInicial(statusPaquet);

                    //Enviem el timestamp en que s'inicia la connexio per a calcular el timeout
                    oos.writeObject(timestamp);


                    ois.readObject();

                    //Actualitzem la reputació
                    int nova_reputacio = (int) ois.readObject();
                    System.out.println("1- Canvia reputacio a " + id + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(id)).getReputacio() + " - nova rep: " + nova_reputacio + " - node: " + model.getReputacions().get(model.getIndexReputacions(id)).getNode());
                    model.getReputacions().get(model.getIndexReputacions(id)).setReputacio(nova_reputacio);


                    break;

                case 2:

                    //Enviem el paquetID, un timestamp per al timeout i el md5 del paquet en cas que haguem rebut el paquet correctament
                    oos.writeObject(pID);
                    oos.writeObject(timestamp);
                    if (model.getStatusPaquet(pID).getStatus() == 1) {
                        md5 = hash.getMd5(model.getStatusPaquet(pID).getPaquet());
                        oos.writeObject(md5);
                        System.out.println("Envio md5 a node" + id);
                    } else {
                        System.out.println("Envio null a node" + id);
                        oos.writeObject(null);
                    }

                    break;

                case 3:
                    //controller.appendText("INICI CONNEXIO CLIENT - OPCIO 3 - AMB NODE " + id);
                    oos.writeObject(pID);
                    int status = (int) ois.readObject();
                    if (status == 1) {
                        byte[] paquet = (byte[]) cm.readObject(ois);
                        md5origen = (String) cm.readObject(ois);
                        controller.appendText("O3: Node " + id + " - md5 origen: " + md5origen);
                        md5 = hash.getMd5(paquet);
                        controller.appendText("O3: md5 destí: " + md5);
                        int rep = model.getReputacions().get(model.getIndexReputacions(id)).getReputacio();
                        if (md5origen.equals(md5)) {
                            oos.writeObject(1);
                            model.guardaPaquet(paquet,pID);
                            controller.appendText("O3: Ja tinc el paquet " + pID);
                            System.out.println("3- Puja reputacio a " + id + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(id)).getReputacio() + " - nova rep: " + (rep + 1) + " - node: " + model.getReputacions().get(model.getIndexReputacions(id)).getNode());
                            model.getReputacions().get(model.getIndexReputacions(id)).setReputacio(rep + 1);
                            if(model.getReputacions().get(model.getIndexReputacions(id)).getReputacio() > 1000) {
                                model.getReputacions().get(model.getIndexReputacions(id)).setReputacio(1000);
                            }
                            model.setReportFinal(pID,1);

                        } else {
                            oos.writeObject(0);
                            controller.appendText("O3: Demano paquet " + pID + " al següent node");
                            System.out.println("3- Baixa reputacio a " + id);
                            model.getReputacions().get(model.getIndexReputacions(id)).setReputacio(rep - 10);
                            if(model.getReputacions().get(model.getIndexReputacions(id)).getReputacio() < 0) {
                                model.getReputacions().get(model.getIndexReputacions(id)).setReputacio(0);
                            }
                        }
                    }
                    oos.writeObject(model.getOwnId());
                    oos.writeObject(model.getReputacions().get(model.getIndexReputacions(id)).getReputacio());
                    controller.appendText("CANVIA NEXT");
                    controller.setNext(pID);
                    //controller.appendText("FI CONNEXIO CLIENT - OPCIO 3 - AMB NODE " + id);

                    break;

                case 4:
                    oos.writeObject(model.getOwnId());
                    oos.writeObject(reputation);


                    break;

                case 100:
                    break;



            }



                /*
                cm.writeObject(cm.encoderBase64(controller.getPath()), oos);
                controller.changeSentImage();
                System.out.println(cm.readObject(ois).toString());*/

            ois.close();
            oos.close();
            s.close();
            //interrupt();

            // }
            //disconnect();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private int getIdOrigen(String pID) {
        int i = 0;
        String n = "";
        while (pID.charAt(i) != '-') {
            n = n + pID.charAt(i);
            i++;
        }
        return Integer.parseInt(n);
    }




}
