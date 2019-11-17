package hsr.dsa.gui.game;

import hsr.dsa.core.game.schiffe_versenken.Field;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;

import javax.swing.*;

import static hsr.dsa.gui.UiConfiguration.*;

public class FieldButton extends JButton {

    private int xPos, yPos;
    private FieldButtonClickListener fieldButtonClickListener;

    public FieldButton(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.setBackground(BUTTON_FOG_OF_WAR);
        this.addActionListener(actionEvent -> {
            if (fieldButtonClickListener != null) fieldButtonClickListener.onClick(xPos, yPos);
        });
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

    public void setShotColoring(Field.Shot shot){
        if (shot!=null) {
            if (shot.equals(Field.Shot.HIT)) setShipHitColor();
            if (shot.equals(Field.Shot.MISS)) setMissedShotColor();
        }
    }

    public interface FieldButtonClickListener {
        void onClick(int x, int y);
    }
}
