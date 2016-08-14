package kuik.matthijs;

import notify.MessageType;
import notify.Notify;
import notify.macosxcenter.MacOsXNotifier;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Server {

    private ServerSocket serverSocket;
    private static final MacOsXNotifier notifier = new MacOsXNotifier();
    private List<Counter> counters = new ArrayList<>();

    final int portNumber = 4500;

    public Server(Counter counter) {

        try {
            serverSocket = new ServerSocket(portNumber);
            addMessage("Server is gereed op poort " + serverSocket.getLocalPort());
        } catch (IOException e) {
            addMessage("Poort " + portNumber + " is al in gebruik (" + e.toString() + ")");
        }

        counters.add(counter);

        //keep receiving new incomming requests from clients
        while (serverSocket != null) {
            try {
                Socket socket = serverSocket.accept();
                new ClientCommunicator(socket, counters); //object that will run its own thread to reply to each client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addMessage(String message) {
        notifier.notify(MessageType.NONE, "Server based counting", message);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println(strDate + "   " + message);
    }

    public void add(Counter counter) {
        counters.add(counter);
    }

    public static void main(String args[]) {
        Counter counter = new Counter();
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-c1":
                    ++i;
                    counter.setPrimaryColor(args[i]);
                    break;
                case "-c2":
                    ++i;
                    counter.setSecondayColor(args[i]);
                    break;
                case "-icon":
                    ++i;
                    counter.setIconPath(args[i]);
                    break;
                case "-password":
                    ++i;
                    counter.setPassword(args[i]);
                    break;
                case "-max":
                    ++i;
                    counter.setMax(Integer.valueOf(args[i]));
                    break;
                case "-catena":
                    counter = new Counter(0, 400, "#1a3669", "#ffffff", "icon.png", "");
                    break;
                case "-h":
                case "--help":
                default:
                    System.out.println(Server.usage());
                    System.exit(0);
                    break;
            }
        }
        new Server(counter);
    }

    private static String usage() {
        return String.format(Locale.ENGLISH, "Usage: java -jar server.jar [-catena][-c1 <#rgb>][-c2 <#rgb>][-icon <path>][-password <string>][-max <integer>]");
    }
}
