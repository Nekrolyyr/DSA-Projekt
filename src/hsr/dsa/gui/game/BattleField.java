package hsr.dsa.gui.game;

import hasr.dsa.test.GameTests;
import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static hsr.dsa.gui.UiConfiguration.*;

public class BattleField {


    private JFrame battleField;


    private JPanel framePanel;
    private JPanel fieldPanel;
    private JPanel yourField;
    private JPanel enemyField;
    private JPanel shipPanel;

    private FieldButton[][] fields;

    private GameChoreographer gameChoreographer;

    public BattleField() {

        gameChoreographer = new GameChoreographer(GameChoreographer.Type.ACTIVE,
                () -> System.out.println("Time roun out!"),
                remainingSecond -> System.out.println("You have "+remainingSecond+" seconds to make a move!"),
                () -> System.out.println("Game Has ended!"));
        GameTests.testSetup(gameChoreographer);
        try {
            gameChoreographer.start();
        }catch (GameNotSetupException e){
            System.out.println("Game was not Setup Correctly");
        }

        JPanel namePanel = createNamePanel(); // On top of the Battlefield, to show which field is yours

        fieldPanel = new JPanel(new GridLayout(1, 2));
        fieldPanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, FIELD_PANEL_HEIGHT));
        yourField = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        yourField.setPreferredSize(new Dimension((int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        yourField.setBorder(FIELD_BORDER);
        enemyField = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        enemyField.setPreferredSize(new Dimension((int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        enemyField.setBorder(FIELD_BORDER);


        shipPanel = new JPanel(new GridLayout(1, NUMBER_OF_SHIPS));
        shipPanel.setPreferredSize(new Dimension((int)BATTLEFIELD_WINDOW_SIZE.getWidth(), (int)(0.15 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        shipPanel.setBackground(Color.RED);
        framePanel = new JPanel(new BorderLayout());

        GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener = shot -> {

        };
        fields = new FieldButton[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        generateFields(yourField, remotePlayerMoveAnswerListener);
        generateFields(enemyField, remotePlayerMoveAnswerListener);

        fieldPanel.add(yourField);
        fieldPanel.add(enemyField);
        framePanel.add(namePanel, BorderLayout.NORTH);
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

    private JPanel createNamePanel() {
        JLabel yourLabel = new JLabel("Your field");
        JLabel enemyLabel = new JLabel("Enemys field");
        yourLabel.setFont(BATTLEFIELD_FONT);
        yourLabel.setHorizontalAlignment(JLabel.CENTER);
        yourLabel.setVerticalAlignment(JLabel.CENTER);
        yourLabel.setBorder(TOP_MARGIN);
        enemyLabel.setFont(BATTLEFIELD_FONT);
        enemyLabel.setHorizontalAlignment(JLabel.CENTER);
        enemyLabel.setVerticalAlignment(JLabel.CENTER);
        enemyLabel.setBorder(TOP_MARGIN);

        JPanel namePanel = new JPanel(new GridLayout(1, 2));
        namePanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, (int) (0.1 * FIELD_PANEL_HEIGHT)));
        namePanel.add(yourLabel);
        namePanel.add(enemyLabel);
        return namePanel;
    }

    private void generateFields(JPanel field, GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener) {
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                fields[y][x] = new FieldButton(x,y);
                fields[y][x].setFieldButtonClickListener((xPos,yPos)->{
                    try{
                        gameChoreographer.localPlayermove(xPos,yPos, remotePlayerMoveAnswerListener);
                    }catch(IllegalMoveException e){
                        System.out.println("This was a Illegal Move!");
                    }});
                field.add(fields[y][x]);
            }
        }
    }


}
