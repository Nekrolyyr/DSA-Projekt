package hsr.dsa.gui.game;

import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.core.game.schiffe_versenken.Ship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FieldButton extends JButton {

    private boolean hasPartOfShip;

    public interface FieldButtonClickListener {
        void onClick(int x, int y);
    }

    private int xPos, yPos;
    private FieldButtonClickListener fieldButtonClickListener;


    public FieldButton(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.setBackground(Color.lightGray);
        this.addActionListener(actionEvent -> fieldClicked());
        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
    }

    public void fieldClicked() {
        this.setBackground(Color.blue);
        this.setEnabled(false);

        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
    }

    public void setFieldButtonClickListener(FieldButtonClickListener fieldButtonClickListener) {
        this.fieldButtonClickListener = fieldButtonClickListener;
    }

    public void setShipPlacedColor () {
        this.setBackground(Color.BLACK);
    }
}
