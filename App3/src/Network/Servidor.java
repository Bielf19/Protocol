package Network;

import Controller.Controller;
import Model.Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Thread {

    private Model model;
    private Controller controller;

    public Servidor (Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
    }

    @Override
    public void run () {
        try {
            ConfiguracioServer config = new ConfiguracioServer();
            config = config.llegeixJsonServidor();
            //Creo server socket que escoltara clients
            ServerSocket sServer = new ServerSocket(config.getPortClient());
            //ServerSocket sServer = new ServerSocket(3000);


            while (true) {
                //Espero a que un client es connecti
                Socket s = sServer.accept();
                ServidorDedicat sd = new ServidorDedicat(s,model,controller);
                sd.start();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}