package gui.game;

import javax.swing.*;
import java.awt.*;

public class Field extends JButton {


    public Field() {
        this.setBackground(Color.lightGray);
        this.addActionListener(actionEvent -> fieldClicked());
    }

    public void fieldClicked() {
        this.setBackground(Color.blue);
        this.setEnabled(false);
    }

}
