package gui;

import java.awt.*;

public class UiConfiguration {

    public final static int NUMBER_OF_ROWS = 10;
    public final static int NUMBER_OF_COLUMNS = 10;
    public final static int NUMBER_OF_SHIPS = 4;


    private static final float SCREEN_RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();

    public final static int BORDER_GAP = (int)(SCREEN_RESOLUTION / 20);

    private static float screenResolutionFactor = SCREEN_RESOLUTION / 200;
    public static Dimension BATTLEFIELD_WINDOW_SIZE = new Dimension((int)(2000 * screenResolutionFactor), (int)(0.7 * (int)(2000 * screenResolutionFactor)));
    public static Dimension CHAT_ROOM_WINDOW_SIZE = new Dimension((int)(2000 * screenResolutionFactor), (int)(0.7 * (int)(2000 * screenResolutionFactor)));



    public static final int FIELD_PANEL_HEIGHT = (int) (0.85 * BATTLEFIELD_WINDOW_SIZE.getHeight());
    public static final int FIELD_PANEL_WIDTH = (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getWidth());


    public static final Font CHAT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, (int)(50 * screenResolutionFactor));
    public static final Font WRITE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, (int)(30 * screenResolutionFactor));





}
