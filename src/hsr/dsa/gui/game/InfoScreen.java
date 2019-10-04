package hsr.dsa.gui.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static hsr.dsa.gui.UiConfiguration.*;

public class InfoScreen extends JWindow {

    JLabel placeShips;

    JWindow shipWindow;

    public InfoScreen(String text) {
        placeShips = new JLabel(text);
        placeShips.setFont(INFO_SCREEN_FONT);
        placeShips.setHorizontalAlignment(SwingConstants.CENTER);
        placeShips.setVerticalAlignment(SwingConstants.CENTER);
        placeShips.setForeground(Color.RED);
        placeShips.setVisible(true);

        shipWindow = new JWindow();
        shipWindow.setSize(INFO_SCREEN_SIZE);
        shipWindow.setLocationRelativeTo(null);
        shipWindow.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));

        shipWindow.add(placeShips);
        startTimer();

    }


    public void showInfoScreen() {
        shipWindow.setVisible(true);
        startTimer();
    }

    public void startTimer() {
        Timer timer = new Timer(INFO_SCREEN_DURATION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                shipWindow.setVisible(false);
                shipWindow.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }


}
