package hsr.dsa;

import hsr.dsa.core.matchmaking.Matchmaker;
import hsr.dsa.gui.UIController;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {

        System.out.println("Hoi DSA");

        //chatRoom = new ChatRoom();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Matchmaker match = new Matchmaker();
            }
        });
    }
}
