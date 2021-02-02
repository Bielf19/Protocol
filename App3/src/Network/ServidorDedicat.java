package Network;
import ChaosMonkey.ChaosMonkey;
import Controller.Controller;
import Model.Model;
import Model.Status;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.LinkedList;

public class ServidorDedicat extends Thread{

    private static final int ownId = 1;

    private Model model;
    private Socket s;
    private Controller controller;
    private ChaosMonkey cm;


    public ServidorDedicat (Socket s, Model model, Controller controller) {
        this.model = model;
        this.s = s;
        this.controller = controller;
        cm = new ChaosMonkey(model);
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

            int opcio = Integer.parseInt(ois.readObject().toString());

            switch (opcio) {
                case 1:
                    byte[] paquet = (byte[]) cm.readObject(ois);

                    md5origen = (String) cm.readObject(ois);
                    md5 = hash.getMd5(paquet);

                    String paquetID = (String) ois.readObject();
                    controller.appendText("O1: paquetID rebut: " + paquetID);
                    Timestamp t = (Timestamp) ois.readObject();
                    model.setReportInici(paquetID,t,false, cm.getProb());

                    oos.writeObject("ACK");

                    if (new Timestamp(System.currentTimeMillis()).getTime() - t.getTime() < 1000) {
                        Status s = new Status();
                        s.setPaquetID(paquetID);
                        int rep = model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio();
                        if (md5.equals(md5origen)) {
                            s.setStatus(1);
                            controller.appendText("O1: * Paquet " + paquetID + " rebut correctament");
                            System.out.println("1- Puja reputacio a " + getIdNode(paquetID) + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio() + " - nova rep: " + (rep + 1) + " - node: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getNode());
                            model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).setReputacio(rep + 1);
                            if(model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio() > 1000) {
                                model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).setReputacio(1000);
                            }
                            model.setReportStatusInicial(paquetID,1);
                            //Enviem nova reputació a l'altre node
                            oos.writeObject(model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio());
                            s.setPaquet(paquet);
                            //Actualitzem llista concens
                            model.addHashConcens(paquetID,md5);


                        } else {
                            s.setStatus(0);
                            model.setReportStatusInicial(paquetID,0);
                            controller.appendText("O1: # Paquet " + paquetID + " rebut incorrectament");
                            System.out.println("1- Baixa reputacio a " + getIdNode(paquetID) + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio() + " - nova rep: " + (rep - 10) + " - node: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getNode());
                            model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).setReputacio(rep - 10);
                            if(model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio() < 0) {
                                model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).setReputacio(0);
                            }
                            //Enviem nova reputació a l'altre node
                            oos.writeObject(model.getReputacions().get(model.getIndexReputacions(getIdNode(paquetID))).getReputacio());
                        }
                        model.addPaquet(s);
                        //Actualitzem llista concens
                        model.addHashConcens(paquetID,md5origen);


                        while (new Timestamp(System.currentTimeMillis()).getTime() - t.getTime() < 1000) {

                        }
                        controller.difussio(paquetID);
                    } else {
                        model.setReportStatusInicial(paquetID,2);
                        controller.appendText("O1: + El paquet " + paquetID + " ha arribat tard");

                    }
                    //controller.appendText("FI CONNEXIO SERVER - OPCIO 1 - AMB PORT " + s.getPort());
                    break;


                case 2:
                    //Rebo un paquetID, un timestamp i un md5 per a comprovar si tinc aquest paquet i si coincideix amb el de l'altre node
                    String pID = (String) ois.readObject();
                    Timestamp timestamp = (Timestamp) ois.readObject();
                    String md5Origen = (String) ois.readObject();

                    //Controlem que no hagi passat el timeout
                    if (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime() < 1000) {
                        LinkedList<Status> l = model.getStatusPaquets();
                        boolean trobat = false;

                        //Busquem el identificador del paquet a la nostre llista de paquets
                        for (int i = 0; i < l.size(); i++) {
                            //Identificador trobat
                            if (l.get(i).getPaquetID().equals(pID)) {
                                trobat = true;
                                controller.appendText("O2: Paquet trobat, paquetID: " + l.get(i).getPaquetID() + ", status: " + l.get(i).getStatus());

                                //Comparem els md5
                                if (md5Origen != null && model.getStatusPaquet(pID).getStatus() == 1) {
                                    //Guardem md5 a la llista de concens
                                    model.addHashConcens(pID,md5Origen);
                                    //Actaulitzem concens
                                    md5 = hash.getMd5(model.getStatusPaquet(pID).getPaquet());
                                    System.out.println("md5Origen: " + md5Origen + " - md5: " + md5 + " - pID: " + pID);
                                    if (md5Origen.equals(md5)) {
                                        model.getStatusPaquet(pID).setConcens(model.getStatusPaquet(pID).getConcens() + 1);
                                    } else {
                                        model.getStatusPaquet(pID).setConcens(model.getStatusPaquet(pID).getConcens() -1);
                                    }
                                    System.out.println("Concens: " + model.getStatusPaquet(pID).getConcens() + " - pID: " + pID);
                                }
                            }
                        }
                        //Identificador no trobat
                        if (!trobat) {
                            controller.appendText("O2: Paquet no trobat, paquetID: " + pID);
                            Status s = new Status();
                            s.setPaquetID(pID);
                            s.setStatus(0);
                            model.addPaquet(s);
                            controller.appendText("O2: Nou paquetID: " + pID + ", status: 0");

                            //Actualitzem reputació
                            int rep = model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getReputacio();
                            System.out.println("2- Baixa reputacio a " + getIdNode(pID) + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getReputacio() + " - nova rep: " + (rep - 10) + " - node: " + model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getNode());
                            model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).setReputacio(rep - 10);
                            if(model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getReputacio() < 0) {
                                model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).setReputacio(0);
                            }
                            controller.canviaReputacio(pID,getIdNode(pID), model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getReputacio());
                            //controller.buscaPaquet(pID);
                        }

                    }
                    //oos.writeObject(model.getReputacions().get(model.getIndexReputacions(getIdNode(pID))).getReputacio());

                    //controller.buscaPaquet(pID);
                    //controller.appendText("FI CONNEXIO SERVER - OPCIO 2 - AMB PORT " + s.getPort());
                    break;

                case 3:
                    //controller.appendText("INICI CONNEXIO SERVER - OPCIO 3 - AMB PORT " + s.getPort());
                    String paquet_id = (String) ois.readObject();
                    Status p = model.getStatusPaquet(paquet_id);
                    oos.writeObject(p.getStatus());
                    if (p.getStatus() == 1) {
                        controller.appendText("O3: Intenta enviar " + paquet_id);
                        cm.writeObject(p.getPaquet(),oos);
                        cm.writeObject(hash.getMd5(p.getPaquet()),oos);
                        int satisfactori = (int) ois.readObject();
                    } else {
                        controller.appendText("O3: No puc enviar " + paquet_id);
                    }
                    int otherId = (int) ois.readObject();
                    int nova_reputacio = (int) ois.readObject();
                    System.out.println("3- Canvia reputacio a " + otherId + " - reputacio actual: " + model.getReputacions().get(model.getIndexReputacions(otherId)).getReputacio() + " - nova rep: " + nova_reputacio + " - node: " + model.getReputacions().get(model.getIndexReputacions(otherId)).getNode());
                    model.getReputacions().get(model.getIndexReputacions(otherId)).setReputacio(nova_reputacio);
                    //controller.appendText("FI CONNEXIO SERVER - OPCIO 3 - AMB PORT " + s.getPort());
                    break;

                case 4:
                    int id_node = (int) ois.readObject();
                    nova_reputacio = (int) ois.readObject();
                    model.getReputacions().get(model.getIndexReputacions(id_node)).setReputacio(nova_reputacio);


                case 100:

                    break;

            }


            ois.close();
            oos.close();
            s.close();
            //interrupt();






               /* //String o = ois.readObject().toString();
                Object o = cm.readObject(ois);
                cm.decoderBase64(o.toString(), model.getNewPath());

                controller.changeReceivedImage(model.getNewPath());
                model.incrementaNewFileIndex();
                cm.writeObject("Image recieved by station 2", oos);

                /*String s = ois.readObject().toString();
                System.out.println(s);
                oos.writeObject("El servidor 2 ha rebut la petició");*/



        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Socket Exception");
        }
    }

    private int getIdNode(String pID) {
        int i = 0;
        String n = "";
        while (pID.charAt(i) != '-') {
            n = n + pID.charAt(i);
            i++;
        }
        return Integer.parseInt(n);
    }
}