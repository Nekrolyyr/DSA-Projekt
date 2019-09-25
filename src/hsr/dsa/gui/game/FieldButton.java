package hsr.dsa.gui.game;

import javax.swing.*;
import java.awt.*;

public class FieldButton extends JButton {
    public interface FieldButtonClickListener{void onClick(int x, int y);}
    private int xPos,yPos;
    private FieldButtonClickListener fieldButtonClickListener;

    public FieldButton(int xPos, int yPos) {
        this.xPos=xPos;
        this.yPos=yPos;
        this.setBackground(Color.lightGray);
        this.addActionListener(actionEvent -> fieldClicked());
        this.addActionListener(actionEvent -> {if(fieldButtonClickListener!=null)fieldButtonClickListener.onClick(xPos,yPos);});
    }

    public void fieldClicked() {
        this.setBackground(Color.blue);
        this.setEnabled(false);
    }

    public void setFieldButtonClickListener(FieldButtonClickListener fieldButtonClickListener){
        this.fieldButtonClickListener=fieldButtonClickListener;
    }

}
