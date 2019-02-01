package chat.client;

import static chat.client.CommonSettings.BUTTON_BACKGROUND;
import static chat.client.CommonSettings.BUTTON_FOREGROUND;
import java.awt.Button;

class CustomButton extends Button {

    public CustomButton(ChatClient Parent, String label) {
        chatclient = Parent;
        setLabel(label);
        setBackground(BUTTON_BACKGROUND);
        setForeground(BUTTON_FOREGROUND);
    }
    ChatClient chatclient;
}
