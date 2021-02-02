package View;

import Controller.Controller;
import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class View extends JFrame {

    private JPanel jpNorth;
    private JPanel jpCenter;
    private JLabel jlImgS;
    private JLabel jlImgR;
    private JPanel jpSouth;
    private JButton jbConnect;
    private JTextField jtfNumber;
    private JButton jbSend;
    private JButton jbListStatus;
    private JButton jbshowRep;
    private JScrollPane jsp;
    private JTextArea jta;

    public View() {
        setTitle("Station 2");
        setSize(400, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /*VIEW DE LES IMATGES
        //NORTH
        jpNorth = new JPanel(new GridLayout(1,2));
        JLabel text1 = new JLabel("Last image sent");
        text1.setVerticalAlignment(SwingConstants.CENTER);
        text1.setHorizontalAlignment(SwingConstants.CENTER);
        jpNorth.add(text1);
        JLabel text2 = new JLabel("Last image recieved");
        text2.setVerticalAlignment(SwingConstants.CENTER);
        text2.setHorizontalAlignment(SwingConstants.CENTER);
        jpNorth.add(text2);
        getContentPane().add(jpNorth,BorderLayout.NORTH);

        //CENTER
        jpCenter = new JPanel(new GridLayout(1,2));
        jlImgS = new JLabel("No images yet");
        jlImgS.setHorizontalAlignment(SwingConstants.CENTER);
        jlImgR = new JLabel("No images yet");
        jlImgR.setHorizontalAlignment(SwingConstants.CENTER);
        jpCenter.add(jlImgS);
        jpCenter.add(jlImgR);
        getContentPane().add(jpCenter, BorderLayout.CENTER);

        //SOUTH
        jpSouth = new JPanel(new GridLayout(1,3));
        jpSouth.add(new JLabel(""));
        jpSouth.add(new JLabel(""));
        jbConnect = new JButton("Connect");
        jpSouth.add(jbConnect);
        getContentPane().add(jpSouth,BorderLayout.SOUTH);
    */
        //VIEW PROVA CONNEXIO
        jpNorth = new JPanel(new FlowLayout());
        jbListStatus = new JButton("List Status");
        jpNorth.add(jbListStatus);
        jbshowRep = new JButton("Show Reputations");
        jpNorth.add(jbshowRep);
        jbSend = new JButton("Send");
        jpNorth.add(jbSend);
        getContentPane().add(jpNorth, BorderLayout.NORTH);


        jta = new JTextArea();
        jsp = new JScrollPane(jta);
        jsp.setPreferredSize(new Dimension(375,675));
        jpCenter = new JPanel(new FlowLayout());
        jpCenter.add(jsp);
        getContentPane().add(jpCenter, BorderLayout.CENTER);



    }

    public void registraControlador(Controller controller) {
//        jbConnect.addActionListener(controller);
  //      jbConnect.setActionCommand("CON");

        jbSend.addActionListener(controller);
        jbSend.setActionCommand("SEND");

        jbListStatus.addActionListener(controller);
        jbListStatus.setActionCommand("LIST");

        jbshowRep.addActionListener(controller);
        jbshowRep.setActionCommand("SHOW");
    }


    public void appendText(String s) {
        jta.append("\n" + s);
    }
}
