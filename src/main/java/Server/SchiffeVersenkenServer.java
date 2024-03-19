package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SchiffeVersenkenServer {

    public SchiffeVersenkenServer() {
        final int PORT = 12345;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server gestartet. Warte auf Verbindungen...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Neue Verbindung von " + clientSocket.getInetAddress().getHostName());
                SpielerHandler spielerHandler = new SpielerHandler(clientSocket);
                new Thread(spielerHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SpielerHandler extends Thread {
        private Socket clientSocket;
        public SpielerHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Willkommen! Sie sind mit dem Server verbunden.");
                out.println("Geben Sie 'bye' ein, um die Verbindung zu trennen.");
                while (!(this.isInterrupted())) {
                    String message = null;
                    while ((message = in.readLine()) !=null){
                        System.out.println("Server: " + message);
                        if (message.equalsIgnoreCase("bye")) {
                            System.out.println("Client hat die Verbindung beendet!");
                            out.println("Auf Wiedersehen!");
                            this.interrupt();
                        } else {
                            out.println("Echo: " + message);
                        }
                    }

                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
