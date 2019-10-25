package hsr.dsa.gui.game;

import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;

import javax.swing.*;

import static hsr.dsa.gui.UiConfiguration.*;

public class FieldButton extends JButton {

    GameChoreographer gameChoreographer;
    private boolean hasPartOfShip = false;
    private int xPos, yPos;
    private FieldButtonClickListener fieldButtonClickListener;

    public FieldButton(int xPos, int yPos, GameChoreographer gameChoreographer) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameChoreographer = gameChoreographer;
        this.setBackground(BUTTON_FOG_OF_WAR);
        this.addActionListener(actionEvent -> fieldClicked());
        // Needed for ShipPlacer
        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
    }

    public void hasPartOfShip() {
        hasPartOfShip = true;
    }

    public boolean isPartOfShip() {
        return hasPartOfShip;
    }

    public void setPartOfShip() {
        hasPartOfShip = true;
    }

    public void fieldClicked() {
        if (gameChoreographer.setupComplete()) {
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
    }

    public void setFieldButtonClickListener(FieldButtonClickListener fieldButtonClickListener) {
        this.fieldButtonClickListener = fieldButtonClickListener;
    }

    public void setShipPlacedColor() {
        this.setEnabled(false); // Needed only here because this fields are never clicked
        this.setBackground(BUTTON_SHIP_PLACED);
    }

    public void setShipHitColor() {
        this.setBackground(BUTTON_SHIP_HIT);
    }

    public void setMissedShotColor() {
        this.setBackground(BUTTON_MISSED_SHOT);
    }

    public interface FieldButtonClickListener {
        void onClick(int x, int y);
    }
}
