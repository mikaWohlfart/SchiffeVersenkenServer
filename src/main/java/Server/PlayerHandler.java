package Server;

import Interfaces.IPlayerHandler;
import Interfaces.ISchiffeVersenkenServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements IPlayerHandler {
    private ISchiffeVersenkenServer schiffeVersenkenServer;
    private String playername;
    private Socket clientSocket;
    private PrintWriter out;
    private SocketListener socketListener;

    public PlayerHandler(SchiffeVersenkenServer schiffeVersenkenServer, Socket socket, String playername) {
        this.clientSocket = socket;
        this.playername = playername;
        this.schiffeVersenkenServer = schiffeVersenkenServer;
        this.socketListener = new SocketListener();
        this.socketListener.start();
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class SocketListener extends Thread{
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Willkommen! Sie sind mit dem Server verbunden.");
                out.println("Platzieren sie ihre Schiffe!");
                while (!(this.isInterrupted())) {
                    String message = null;
                    while ((message = in.readLine()) !=null){
                        handlePlayerInput(message);
                    }
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void handlePlayerInput(String message){
        schiffeVersenkenServer.messageHandler(message, playername);
    }

    @Override
    public void sendMessageToUser(String message) {
        out.println(message);
    }

    @Override
    public String getPlayername() {
        return playername;
    }
}
