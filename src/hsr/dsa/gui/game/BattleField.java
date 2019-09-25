package hsr.dsa.gui.game;

import javax.swing.*;
import java.awt.*;

import static hsr.dsa.gui.UiConfiguration.*;

public class BattleField {


    private JFrame battleField;

    private JPanel framePanel;
    private JPanel fieldPanel;
    private JPanel field;
    private JPanel shipPanel;

    private Field[][] fields;

    public BattleField() {



        fieldPanel = new JPanel(new FlowLayout());
        fieldPanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, FIELD_PANEL_HEIGHT));
        field = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        field.setPreferredSize(new Dimension((int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));



        shipPanel = new JPanel(new GridLayout(1, NUMBER_OF_SHIPS));
        shipPanel.setPreferredSize(new Dimension((int)BATTLEFIELD_WINDOW_SIZE.getWidth(), (int)(0.15 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        shipPanel.setBackground(Color.RED);
        framePanel = new JPanel(new BorderLayout());


        fields = new Field[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                fields[y][x] = new Field();
                field.add(fields[y][x]);
            }
        }
        fieldPanel.add(field);
        framePanel.add(fieldPanel, BorderLayout.CENTER);
        framePanel.add(shipPanel, BorderLayout.SOUTH);

        battleField = new JFrame("Battleships");
        battleField.add(framePanel);
        battleField.setSize(BATTLEFIELD_WINDOW_SIZE);
        battleField.setResizable(false);
        battleField.setLocationRelativeTo(null);
        battleField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        battleField.setVisible(true);
    }


}
