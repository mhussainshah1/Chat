package chat.client;

import static chat.client.CommonSettings.BUTTON_BACKGROUND;
import static chat.client.CommonSettings.BUTTON_FOREGROUND;
import java.awt.Button;
import java.awt.Frame;

class CustomButton extends Button {

    private Frame chatclient;
    public CustomButton(Frame parent, String label) {
        chatclient = parent;
        setLabel(label);
        setBackground(BUTTON_BACKGROUND);
        setForeground(BUTTON_FOREGROUND);
    }
}
