package chat.client.move;

import java.awt.Color;

public interface CommonSettings {

    int MAX_COLOR = 8;
    int TOP_PANEL_START_POS = 10;
    int DEFAULT_ICON_WIDTH = 30;
    int DEFAULT_ICON_HEIGHT = 30;
    int TAPPANEL_WIDTH = 225;
    int TAPPANEL_HEIGHT = 350;
    int TAPPANEL_CANVAS_WIDTH = 180;
    int TAPPANEL_CANVAS_HEIGHT = 260;
    int TAP_COUNT = 3;
    int IMAGE_CANVAS_START_POSITION = 10;
    String ICON_NAME = "PHOTO";
    int DEFAULT_IMAGE_CANVAS_SPACE = 35;
    int DEFAULT_LIST_CANVAS_POSITION = 0;
    int DEFAULT_LIST_CANVAS_INCREMENT = 20;
    int DEFAULT_LIST_CANVAS_HEIGHT = 15;
    int SCROLL_BAR_SIZE = 15;
    int USER_CANVAS = 0;
    int ROOM_CANVAS = 1;
    int USER_CANVAS_NORMAL_ICON = 11;
    int USER_CANVAS_IGNORE_ICON = 10;
    int ROOM_CANVAS_ICON = 13;
    int DEFAULT_MESSAGE_CANVAS_POSITION = 25;
    int DEFAULT_SCROLLING_HEIGHT = 20;

    int MESSAGE_TYPE_DEFAULT = 0;
    int MESSAGE_TYPE_JOIN = 1;
    int MESSAGE_TYPE_LEAVE = 2;
    int MESSAGE_TYPE_ADMIN = 3;

    int QUIT_TYPE_DEFAULT = 0;
    int QUIT_TYPE_KICK = 1;
    int QUIT_TYPE_NULL = 2;

    int PRIVATE_WINDOW_WIDTH = 415;
    int PRIVATE_WINDOW_HEIGHT = 350;

    int EMOTION_CANVAS_WIDTH = 400;
    int EMOTION_CANVAS_HEIGHT = 270;

    int MAX_PRIVATE_WINDOW = 40;
    String PRODUCT_NAME = "Turtle Chat v1.0";
    String COMPANY_NAME = "Private Production..";

    Color BACKGROUND = new Color(224, 236, 254);                    //0
    Color INFORMATION_PANEL_BACKGROUND = new Color(255, 153, 0);    //1
    Color BUTTON_FOREGROUND = Color.BLACK;                          //2
    Color BUTTON_BACKGROUND = new Color(224, 236, 254);             //3
    Color SSTABBUTTON = new Color(255, 153, 0);                     //4
    Color MESSAGE_CANVAS = Color.BLACK;                             //5
    Color TOP_PANEL_BACKGROUND = Color.YELLOW;                      //6
    Color LABEL_TEXT_COLOR = Color.WHITE;                           //7

    int PORT_NUMBER = 1436;
}
