
import Controller.Controller;
import Model.Model;
import Network.ClientConnection;
import Network.ConfiguracioClient;
import Network.Servidor;
import View.View;


import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Model model = new Model();
        View view = new View();

        Controller controller = new Controller(model,view);
        Servidor servidor = new Servidor(model,controller);
        servidor.start();
        view.registraControlador(controller);
        view.setVisible(true);




    }
}