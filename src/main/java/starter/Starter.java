package starter;

import Server.SchiffeVersenkenServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Starter {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1"; // IP-Adresse des Servers
        final int SERVER_PORT = 12345; // Port, auf dem der Server lauscht

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Verbunden mit Server " + SERVER_IP + ":" + SERVER_PORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Thread receiverThread = new Thread(() -> {
                try {
                    String message = null;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Server: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            receiverThread.start();

            Scanner userInputScanner = new Scanner(System.in);
            out.println("REGISTER T");
            out.flush();
            Thread.sleep(1000);
            System.out.println("Warte auf eingabe");
            String message = userInputScanner.nextLine();
            System.out.println("Warte auf eingabe");
            out.println("RPS " + message);
            out.flush();
            String[] placementCommands = {
                    "SHIP_ADD SUBMARINE A1 RIGHT",
                    "SHIP_ADD CRUISER F0 DOWN",
                    "SHIP_ADD DESTROYER H0 RIGHT",
                    "SHIP_ADD DESTROYER C3 RIGHT",
                    "SHIP_ADD SUBMARINE I4 RIGHT",
                    "SHIP_ADD SUBMARINE E5 DOWN",
                    "SHIP_ADD DESTROYER A7 DOWN",
                    "SHIP_ADD BATTLESHIP C7 RIGHT",
                    "SHIP_ADD SUBMARINE J8 DOWN",
                    "SHIP_ADD CRUISER E9 RIGHT"
            };
            for (String command : placementCommands) {
                out.println(command);
                out.flush();
                Thread.sleep(1000);
            }

            while (true) {
                message = userInputScanner.nextLine();
                out.println(message);
                out.flush();
                if (message.equalsIgnoreCase("bye")) {
                    break;
                }

            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
