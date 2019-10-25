package hsr.dsa.gui.game;

import javax.swing.*;
import java.awt.*;

import static hsr.dsa.gui.UiConfiguration.*;

public class FieldButton extends JButton {

    public interface FieldButtonClickListener {
        void onClick(int x, int y);
    }

    private boolean hasPartOfShip = false;

    private int xPos, yPos;
    private FieldButtonClickListener fieldButtonClickListener;

    public void setPartOfShip() {
        hasPartOfShip = true;
    }

    public FieldButton(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.setBackground(BUTTON_FOG_OF_WAR);
        this.addActionListener(actionEvent -> fieldClicked());
        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
    }

    public void fieldClicked() {
        this.setEnabled(false);

        if (hasPartOfShip) {
            setShipHitColor();
        } else {
            setMissedShotColor();
        }

        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
    }

    public void setFieldButtonClickListener(FieldButtonClickListener fieldButtonClickListener) {
        this.fieldButtonClickListener = fieldButtonClickListener;
    }

    public void setShipPlacedColor () {
        this.setEnabled(false); // Needed only here because this fields are never clicked
        this.setBackground(BUTTON_SHIP_PLACED);
    }

    public void setShipHitColor() {
        this.setBackground(BUTTON_SHIP_HIT);
    }
    public void setMissedShotColor() {
        this.setBackground(BUTTON_MISSED_SHOT);
    }
}
