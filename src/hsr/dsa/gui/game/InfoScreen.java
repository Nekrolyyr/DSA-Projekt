package hsr.dsa.gui.game;

import javax.swing.*;
import java.awt.*;

import static hsr.dsa.gui.UiConfiguration.MESSAGE_FONT;
import static hsr.dsa.gui.UiConfiguration.MESSAGE_WINDOW_SIZE;

public class InfoScreen extends JWindow {

    JLabel placeShips;

    JWindow shipWindow;

    public InfoScreen(String text) {
        placeShips = new JLabel(text);
        placeShips.setFont(MESSAGE_FONT);
        placeShips.setHorizontalAlignment(SwingConstants.CENTER);
        placeShips.setVerticalAlignment(SwingConstants.CENTER);
        placeShips.setForeground(Color.RED);
        placeShips.setVisible(true);

        ///this = new JWindow();
        this.setSize(MESSAGE_WINDOW_SIZE);
        this.setLocationRelativeTo(null);
        this.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));

        this.add(placeShips);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void showInfoScreen() {
        this.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setVisible(false);
    }




}
