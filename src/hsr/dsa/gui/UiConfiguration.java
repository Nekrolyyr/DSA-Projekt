package hsr.dsa.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UiConfiguration {

    public final static int NUMBER_OF_ROWS = 10;
    public final static int NUMBER_OF_COLUMNS = 10;
    public final static int NUMBER_OF_SHIPS = 4;


    private static final float SCREEN_RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();

    public final static int BORDER_GAP = (int) (SCREEN_RESOLUTION / 20);

    private static float screenResolutionFactor = SCREEN_RESOLUTION / 200;
    public static Dimension BATTLEFIELD_WINDOW_SIZE = new Dimension((int) (2500 * screenResolutionFactor), (int) (0.7 * (int) (2000 * screenResolutionFactor)));
    public static Dimension CHAT_ROOM_WINDOW_SIZE = new Dimension((int) (2000 * screenResolutionFactor), (int) (0.7 * (int) (2000 * screenResolutionFactor)));
    public static Dimension MESSAGE_WINDOW_SIZE = new Dimension((int) (1000 * screenResolutionFactor), (int) (0.7 * (int) (1000 * screenResolutionFactor)));

    public static final int FIELD_PANEL_HEIGHT = (int) (0.85 * BATTLEFIELD_WINDOW_SIZE.getHeight());
    public static final int FIELD_PANEL_WIDTH = (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getWidth());


    public static final Font CHAT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (50 * screenResolutionFactor));
    public static final Font WRITE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int) (30 * screenResolutionFactor));
    public static final Font BATTLEFIELD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int) (60 * screenResolutionFactor));
    public static final Font MESSAGE_FONT = new Font(Font.SERIF, Font.PLAIN, (int)(100 * screenResolutionFactor));

    public static final Border FIELD_BORDER = BorderFactory.createEmptyBorder(0, 50, 50, 50);
    public static final Border TOP_MARGIN = BorderFactory.createEmptyBorder(20, 0, 20, 0);

}
