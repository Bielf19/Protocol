
import Controller.Controller;
import Model.Model;
import Network.Servidor;
import View.View;


import javax.swing.*;

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