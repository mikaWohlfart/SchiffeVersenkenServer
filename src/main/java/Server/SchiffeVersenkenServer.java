package Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Enum.ServerCommands;

public class SchiffeVersenkenServer {
    private int PORT;
    int currenplayernumber;
    List<PlayerHandler> playerHandlers = new ArrayList<>();
    boolean running;

    public SchiffeVersenkenServer() {
        running = true;
        setPORT();
        host();
        int playerCount = 1;
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server gestartet. Warte auf Verbindungen...");
            while (playerCount <= 2) {
                System.out.println("Playercount: " + playerCount);
                Socket clientSocket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(this, clientSocket, playerCount - 1, new Board());
                player.start();
                playerHandlers.add(player);
                playerCount++;
            }

            waitingForRegistration();
            System.out.println("REGISTRATION FINISHED");
            waitingForRPS();
            System.out.println("RPS FINISHED");
            waitingForShipToPlaces();
            System.out.println("SHIPS_PLACED FINISHED");

            for(PlayerHandler player : playerHandlers) {
                player.setDefenderBoard(player.ships);
            }

            //Inital Send
            for (PlayerHandler player : playerHandlers) {
                if(player.getRpsWon()){
                    player.setPlayerStatus(ServerCommands.ATTACKER.name());
                    player.sendMessageToUser(ServerCommands.ATTACKER.name() + " " + player.getAttackerBoard().castToString());
                }else {
                    player.setPlayerStatus(ServerCommands.DEFENDER.name());
                    player.sendMessageToUser(ServerCommands.DEFENDER.name() + " " + player.getDefenderBoard().castToString());
                }
            }

            System.out.println("Waiting for attacker move...");
            while(running){
                PlayerHandler playerAttacker = null;
                PlayerHandler playerDefender = null;
                //Waiting for Attackermove
                for(PlayerHandler player : playerHandlers) {
                    if(player.getPlayerStatus().equals(ServerCommands.ATTACKER.name())){
                        playerAttacker = player;
                        while(!player.playerAlreadyAttacked()){}
                    }
                }

                for(PlayerHandler player : playerHandlers) {
                    if(player.getPlayerStatus().equals(ServerCommands.DEFENDER.name())){
                        playerDefender = player;
                    }
                }
                checkIfSomeoneWons(playerDefender, playerAttacker);

                changeRoles();
            }


            //TODO
        /*
            Game plan:
            1. Start screen
                a. name
                b. start or connect
            2. Rock Paper Scissors
            3. Ship placing
            4. Defending / Attacking
            5. End screen
                a. score
                b. play again
                c. exit
         */

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkIfSomeoneWons(PlayerHandler playerDefender, PlayerHandler playerAttacker) {
        int counter = 0;
        for(Ship ship: playerDefender.ships){
            if (ship.isShipDestroyed()){
                counter++;
            }
        }
        if(counter == playerDefender.ships.size()){
            playerDefender.sendMessageToUser("LOST");
            playerAttacker.sendMessageToUser("WON");
        }
    }

    private void changeRoles() {
        for(PlayerHandler player : playerHandlers) {
            if(player.getPlayerStatus().equals(ServerCommands.DEFENDER.name())){
                player.setPlayerStatus(ServerCommands.ATTACKER.name());
            }else {
                player.setPlayerStatus(ServerCommands.DEFENDER.name());
            }
        }
    }

    private void waitingForShipToPlaces() {
        int counter = 0;
        System.out.println("Waiting for ship to places...");

        for (PlayerHandler player : playerHandlers) {
            System.out.println(player.ships.size());
            if (player.ships.size() == 10) {
                counter += 1;
            }else {
                counter = 0;
            }
        }
        if (!(counter == 2)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            waitingForShipToPlaces();
        }
    }

    private void waitingForRPS() throws InterruptedException {
        int counter = 0;
        while (counter < 2) {
            counter = 0;
            Thread.sleep(5000);
            for (PlayerHandler p : playerHandlers) {
                if (p.getRps() != null) {
                    counter++;
                }
            }
        }
        if (playerHandlers.get(0).getRps().ordinal() == playerHandlers.get(1).getRps().ordinal()) {
            for (PlayerHandler p : playerHandlers) {
                p.sendMessageToUser(ServerCommands.TIE.toString());
                p.restartRPS();
            }
            waitingForRPS();
        } else if (playerHandlers.get(0).getRps().ordinal() == 0 && playerHandlers.get(1).getRps().ordinal() == 1) {
            playerHandlers.get(0).sendMessageToUser(ServerCommands.LOST.toString());
            playerHandlers.get(0).setRpsWon(false);
            playerHandlers.get(1).sendMessageToUser(ServerCommands.WON.toString());
            playerHandlers.get(1).setRpsWon(true);
            currenplayernumber = 1;

        } else {
            playerHandlers.get(1).sendMessageToUser(ServerCommands.LOST.toString());
            playerHandlers.get(1).setRpsWon(false);
            playerHandlers.get(0).sendMessageToUser(ServerCommands.WON.toString());
            playerHandlers.get(0).setRpsWon(true);
            currenplayernumber = 0;
        }


    }

    private void waitingForRegistration() throws InterruptedException {
        System.out.println("WAITING FOR TWO PLAYERS TO REGISTER");
        int counter = 0;
        while (counter < 2) {
            counter = 0;
            Thread.sleep(5000);
            for (PlayerHandler p : playerHandlers) {
                if (p.getIsRegsitered()) {
                    counter += 1;
                    System.out.println("WAITING FOR " + (2 - counter) + " PLAYERS TO REGISTER");
                }
            }
        }
        System.out.println("TWO PLAYERS ARE REGISTERED");
    }


    private void setPORT() {
        System.out.println("Bitte gebe den Port an, unter welchem du erreichbar sein möchtest:");
        Scanner sc = new Scanner(System.in);
        PORT = sc.nextInt();
    }


    public void host() {
        try {
            System.out.println("Deine IP-Adresse: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
