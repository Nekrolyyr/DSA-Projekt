package hsr.dsa;

import hsr.dsa.core.matchmaking.Matchmaker;
import hsr.dsa.gui.UIController;
import hsr.dsa.gui.chatRoom.GamblingWindow;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {

        System.out.println("Hoi DSA");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Matchmaker match = new Matchmaker();
            }
        });
    }
}
