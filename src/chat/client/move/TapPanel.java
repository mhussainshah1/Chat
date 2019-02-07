package chat.client.move;

import java.awt.*;
import java.awt.event.*;

public class TapPanel extends Panel implements CommonSettings, ActionListener {

    ChatClient chatclient;
    protected TextField txtUserCount;
    ScrollView ImageScrollView, UserScrollView, RoomScrollView;
    protected ImageCanvas imageCanvas;
    protected ListViewCanvas userCanvas, roomCanvas;
    Button cmdChangeRoom, cmdIgnoreUser, cmdSendDirect;

    TapPanel(ChatClient parent) {
        /**
         * *********Initialize the Components**********
         */
        chatclient = parent;

        Panel Tappanel = new Panel(new BorderLayout());
        CardLayout cardlayout = new CardLayout();
        Panel MainPanel = new Panel(cardlayout);

        /**
         * *****User Panel Coding Starts**********
         */
        Panel UserPanel = new Panel(new BorderLayout());
        userCanvas = new ListViewCanvas(chatclient, USER_CANVAS);

        UserScrollView = new ScrollView(userCanvas, true, true, TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        userCanvas.scrollView = UserScrollView;
        UserPanel.add(BorderLayout.CENTER, UserScrollView);

        Panel UserButtonPanel = new Panel(new BorderLayout());
        cmdSendDirect = new CustomButton(chatclient, "Send Direct Message");
        cmdSendDirect.addActionListener(this);
        UserButtonPanel.add(BorderLayout.NORTH, cmdSendDirect);
        cmdIgnoreUser = new CustomButton(chatclient, "Ignore User");
        cmdIgnoreUser.addActionListener(this);
        UserButtonPanel.add(BorderLayout.CENTER, cmdIgnoreUser);
        UserPanel.add(BorderLayout.SOUTH, UserButtonPanel);

        /**
         * ******Room Panel Coding Starts**********
         */
        Panel RoomPanel = new Panel(new BorderLayout());
        roomCanvas = new ListViewCanvas(chatclient, ROOM_CANVAS);

        RoomScrollView = new ScrollView(roomCanvas, true, true, TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        roomCanvas.scrollView = RoomScrollView;
        RoomPanel.add(BorderLayout.CENTER, RoomScrollView);

        Panel RoomButtonPanel = new Panel(new BorderLayout());
        Panel RoomCountPanel = new Panel(new BorderLayout());
        Label LblCaption = new Label("ROOM COUNT", 1);
        RoomCountPanel.add(BorderLayout.NORTH, LblCaption);
        txtUserCount = new TextField();
        txtUserCount.setEditable(false);
        RoomCountPanel.add(BorderLayout.CENTER, txtUserCount);
        RoomButtonPanel.add(BorderLayout.CENTER, RoomCountPanel);

        cmdChangeRoom = new CustomButton(chatclient, "Change Room");
        cmdChangeRoom.addActionListener(this);
        RoomButtonPanel.add(BorderLayout.SOUTH, cmdChangeRoom);

        RoomPanel.add(BorderLayout.SOUTH, RoomButtonPanel);

        /**
         * ******Image Panel Coding Starts**********
         */
        Panel ImagePanel = new Panel(new BorderLayout());

        imageCanvas = new ImageCanvas(chatclient);
        ImageScrollView = new ScrollView(imageCanvas, true, true, TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        imageCanvas.scrollview = ImageScrollView;
        /**
         * ********Add Icons into MessageObject ********
         */
        imageCanvas.AddIconsToMessageObject();
        ImagePanel.add(BorderLayout.CENTER, ImageScrollView);

        /**
         * *******Add All the Panel in to Main Panel********
         */
        MainPanel.add("UserPanel", UserPanel);
        MainPanel.add("RoomPanel", RoomPanel);
        MainPanel.add("ImagePanel", ImagePanel);
        cardlayout.show(MainPanel, "UserPanel");
        BorderPanel borderpanel = new BorderPanel(this, chatclient, cardlayout, MainPanel, TAPPANEL_WIDTH, TAPPANEL_HEIGHT);

        borderpanel.addTab("USERS", "UserPanel");
        borderpanel.addTab("ROOMS", "RoomPanel");
        borderpanel.addTab("EMOTICONS", "ImagePanel");

        Tappanel.add(borderpanel);
        add("Center", Tappanel);

        /**
         * ******Common Things**********
         */
    }

    /**
     * *********Action Listener coding *********
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cmdChangeRoom)) {
            /**
             * ****** Change Room Coding ********
             */
            chatclient.changeRoom();
        }

        if (evt.getSource().equals(cmdIgnoreUser)) {
            if (evt.getActionCommand().equals("Ignore User")) {
                userCanvas.ignoreUser(true);
            } else {
                userCanvas.ignoreUser(false);
            }
        }

        if (evt.getSource().equals(cmdSendDirect)) {
            userCanvas.SendDirectMessage();
        }
    }
}
