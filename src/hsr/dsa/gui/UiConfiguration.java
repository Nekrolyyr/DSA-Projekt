package hsr.dsa.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static hsr.dsa.core.game.GameConfiguration.FIELD_SIZE;

public class UiConfiguration {

    public final static int INFO_SCREEN_DURATION = 2000;

    private static final float SCREEN_RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();

    public final static int BORDER_GAP = (int) (SCREEN_RESOLUTION / 20);

    private static float screenResolutionFactor = SCREEN_RESOLUTION / 200;
    public static Dimension BATTLEFIELD_WINDOW_SIZE = new Dimension((int) (2500 * screenResolutionFactor), (int) (0.7 * (int) (2000 * screenResolutionFactor)));
    public static Dimension CHAT_ROOM_WINDOW_SIZE = new Dimension((int) (2000 * screenResolutionFactor), (int) (0.7 * (int) (2000 * screenResolutionFactor)));
    public static Dimension INFO_SCREEN_SIZE = new Dimension((int) (2000 * screenResolutionFactor), (int) (0.7 * (int) (1000 * screenResolutionFactor)));
    public static Dimension GAMBLING_WINDOW_SIZE = new Dimension((int) (1200 * screenResolutionFactor), (int) (450 * screenResolutionFactor));

    public static Dimension BUTTON_SIZE = new Dimension((int)(300 * screenResolutionFactor), (int)(50 * screenResolutionFactor));

    public static final int FIELD_PANEL_HEIGHT = (int) (0.85 * BATTLEFIELD_WINDOW_SIZE.getHeight());
    public static final int FIELD_PANEL_WIDTH = (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getWidth());


    public static final Font CHAT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (50 * screenResolutionFactor));
    public static final Font WRITE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int) (30 * screenResolutionFactor));
    public static final Font BATTLEFIELD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int) (60 * screenResolutionFactor));
    public static final Font INFO_SCREEN_FONT = new Font(Font.SERIF, Font.PLAIN, (int)(100 * screenResolutionFactor));
    public static final Font USER_WINDOW_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int)(20 * screenResolutionFactor));

    public static final Border FIELD_BORDER = BorderFactory.createEmptyBorder(0, 50, 50, 50);
    public static final Border TOP_MARGIN = BorderFactory.createEmptyBorder(20, 0, 20, 0);

    public static final Color BUTTON_SHIP_PLACED = Color.BLACK;
    public static final Color BUTTON_FOG_OF_WAR = Color.LIGHT_GRAY;
    public static final Color BUTTON_MISSED_SHOT = Color.BLACK;
    public static final Color BUTTON_SHIP_HIT = Color.RED;
    public static final Color GENERAL_BUTTON_COLOR = Color.white;

}
