package Network;



import ChaosMonkey.ChaosMonkey;
import Controller.Controller;
import Model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;

public class ClientConnection extends Thread {

    private Socket[] socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean conectat;
    private Controller controller;
    private ChaosMonkey cm;
    private int[] id;

    public ClientConnection() throws IOException {
        ConfiguracioClient config = new ConfiguracioClient();
        ConfiguracioClient[] array;
        array = config.llegeixJsonClient();


        socket = new Socket[array.length];
        id = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            socket[i] = new Socket(array[i].getIP(), array[i].getPortServer());
            id[i] = array[i].getId();
        }

        conectat = true;

    }

    public Timestamp IniciaConnexioMultiCast(Controller controller, Model model, int opcio, String paquetID) {
        this.controller = controller;
        cm = new ChaosMonkey(model);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        /*if (opcio == 1) {
            model.setReportInici(paquetID,timestamp,true, cm.getProb());
        }*/
        if (conectat) {
            try {
                for (int i = 0; i < socket.length; i++) {
                    ClientDedicat cd = new ClientDedicat(controller,model,cm,socket[i],id[i],opcio,timestamp,paquetID,-1);
                    cd.start();
                }
                start();
            } catch (java.lang.IllegalThreadStateException e) {

            }
        }
        return timestamp;

    }

    public void IniciaConnexioUniCast(Controller controller, Model model, int opcio, String paquetID, int node, int reputacio) {
        this.controller = controller;
        cm = new ChaosMonkey(model);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            int index = buscaIndex(node);
            ClientDedicat cd = new ClientDedicat(controller,model,cm,socket[index],node,opcio,timestamp,paquetID,reputacio);
            cd.start();
        } catch (java.lang.IllegalThreadStateException e) {
            System.out.println("Connexió unicast fallida");
        }
    }

    private int buscaIndex(int node) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] == node) {
                return i;
            }
        }
        return -1;
    }

    /*@Override
    public void run() {

        while (conectat) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cm.writeObject(cm.encoderBase64(controller.getPath()), oos);
            controller.changeSentImage();
            System.out.println(cm.readObject(ois).toString());



        }
        //disconnect();

    }*/


   /* private void disconnect() {
        try {

            ois.close();
            oos.close();
            socket.close();
            conectat = false;

        } catch (IOException e) {

        }

        interrupt();
    }*/


}
