package kuik.matthijs;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server extends JFrame implements Runnable, WindowListener {
    private JTextArea messageArea;
    private JTextArea color1Area;
    private JTextArea color2Area;
    private Thread thread;
    private ServerSocket serverSocket;
    private JPanel colorBar;
    private JPanel colorPanel;
    private JPanel headerPanel;

    final int messageAreaBufferSize = 2000;
    final int portNumber = 4500;
    final int bordersize = 10;

    public Server() {
        Border border = BorderFactory.createEmptyBorder(
                bordersize, bordersize, bordersize, bordersize);
        this.messageArea = new JTextArea();
        this.messageArea.setLineWrap(true);
        this.messageArea.setBackground(Color.BLACK);
        this.messageArea.setForeground(Color.WHITE);
        this.messageArea.setBorder(border);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        this.messageArea.setFont(font);

        font = new Font(Font.MONOSPACED, Font.BOLD, 18);
        this.color1Area = new JTextArea();
        this.color2Area = new JTextArea();

        this.colorBar = new JPanel();
        this.colorBar.setBackground(Color.decode(Data.getPrimaryColor()));
        color1Area.setFont(font);
        color1Area.setBorder(border);
        color1Area.setText(Data.getPrimaryColor());
        color1Area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String hex = color1Area.getText();
                if (hex.matches("#-?[0-9a-fA-F]+") && hex.length() == 7) {
                    try {
                        Color color = Color.decode(hex);
                        colorBar.setBackground(color);
                        Data.setPrimaryColor(hex);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        color2Area.setFont(font);
        color2Area.setBorder(border);
        color2Area.setText(Data.getSecondayColor());
        color2Area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String hex = color2Area.getText();
                if (hex.matches("#-?[0-9a-fA-F]+") && hex.length() == 7) {
                    try {
                        Color color = Color.decode(hex);
                        colorBar.setBackground(color);
                        Data.setSecondayColor(hex);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        setLayout(new BorderLayout());
        add(this.messageArea, BorderLayout.CENTER);
        colorPanel = new JPanel();
        headerPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.LINE_AXIS));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.PAGE_AXIS));
        colorPanel.add(color1Area);
        colorPanel.add(color2Area);
        headerPanel.add(colorPanel);
        headerPanel.add(colorBar);
        add(headerPanel, BorderLayout.NORTH);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(100, 50, 600, 500);
        this.setVisible(true);

        //start de server loop
        this.thread = new Thread(this);
        this.thread.start();
    }


    public void run() {
        serverSocket = null;

        try {
            serverSocket = new ServerSocket(portNumber);
            this.addMessage("Server is gereed op poort " + serverSocket.getLocalPort());
        } catch (IOException e) {
            this.addMessage("Poort " + portNumber + " is al in gebruik (" + e.toString() + ")");
        }


        //keep receiving new incomming requests from clients
        while (serverSocket != null) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
                new ClientCommunicator(socket, this); //object that will run its own thread to reply to each client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        String history = messageArea.getText();
        if (history.length() > messageAreaBufferSize) { history = history.substring(0, messageAreaBufferSize) + "..."; }
        messageArea.setText(strDate + "   " + message + "\n" + history);
    }


    public static void main(String args[]) {
        new Server();
    }


    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }


    //bij het sluiten van de applicatie moet de thread stoppen!
    @Override
    public void windowClosed(WindowEvent e) {
        serverSocket = null;
    }


    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }
}
