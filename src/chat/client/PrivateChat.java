package chat.client;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class PrivateChat extends javax.swing.JFrame implements CommonSettings {

    PrivateChat(ChatClient parent, String toUserName) {
        chatClient = parent;
        userName = toUserName;
        setTitle("Private Chat With " + userName);
        System.out.println(userName);
        Image IconImage = Toolkit.getDefaultToolkit().getImage("images/logo.gif");
        setIconImage(IconImage);
        setBackground(BACKGROUND);
        setFont(chatClient.getFont());
        emotionFlag = false;
        initComponents();
        emotionPane.setVisible(false);
        addIconsToMessage();
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdClear = new javax.swing.JButton();
        cmdIgnore = new javax.swing.JButton();
        cmdClose = new javax.swing.JButton();
        cmdEmoticons = new javax.swing.JButton();
        cmdSend = new javax.swing.JButton();
        txtMessage = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        messagePane = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        emotionPane = new javax.swing.JTextPane();
        lblConversation = new javax.swing.JLabel();

        setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.gif"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cmdClear.setText("Clear");
        cmdClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearActionPerformed(evt);
            }
        });

        cmdIgnore.setText("Ignore User");
        cmdIgnore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdIgnoreActionPerformed(evt);
            }
        });

        cmdClose.setText("Close");
        cmdClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCloseActionPerformed(evt);
            }
        });

        cmdEmoticons.setText("Emotions");
        cmdEmoticons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEmoticonsActionPerformed(evt);
            }
        });

        cmdSend.setText("Send");
        cmdSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSendActionPerformed(evt);
            }
        });

        txtMessage.setName(""); // NOI18N
        txtMessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMessageKeyPressed(evt);
            }
        });

        messagePane.setContentType("text/html"); // NOI18N
        jScrollPane1.setViewportView(messagePane);

        jScrollPane2.setViewportView(emotionPane);

        lblConversation.setText("Conversation with " + userName);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdSend, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdClear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdIgnore, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmdClose, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdEmoticons, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(lblConversation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblConversation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdClear)
                    .addComponent(cmdIgnore)
                    .addComponent(cmdClose)
                    .addComponent(cmdEmoticons))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitPrivateWindow();
    }//GEN-LAST:event_formWindowClosing

    private void cmdClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdClearActionPerformed
        messagePane.setText("");
    }//GEN-LAST:event_cmdClearActionPerformed

    private void cmdCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCloseActionPerformed
        exitPrivateWindow();
    }//GEN-LAST:event_cmdCloseActionPerformed

    private void cmdSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSendActionPerformed
        if (!(txtMessage.getText().trim().equals(""))) {
            sendMessage();
        }
    }//GEN-LAST:event_cmdSendActionPerformed

    private void cmdIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdIgnoreActionPerformed
        if (evt.getActionCommand().equals("Ignore User")) {
            chatClient.ignoreUser(true, userName);
            appendToPane(messagePane, "<span>" + userName + " has been ignored!" + "</span>");
            cmdIgnore.setText("Allow User");
        } else {
            appendToPane(messagePane, "<span>" + userName + " has been removed from ignored list!" + "</span>");
            chatClient.ignoreUser(false, userName);
            cmdIgnore.setText("Ignore User");
        }
    }//GEN-LAST:event_cmdIgnoreActionPerformed

    private void txtMessageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMessageKeyPressed
        if ((evt.getKeyCode() == 10) && (!(txtMessage.getText().trim().equals("")))) {
            sendMessage();
        }
    }//GEN-LAST:event_txtMessageKeyPressed

    private void cmdEmoticonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEmoticonsActionPerformed
        // TODO add your handling code here:
        if (emotionFlag) {
            emotionFlag = false;
            emotionPane.setVisible(false);
            setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT);
        } else {
            emotionFlag = true;
            emotionPane.setVisible(true);
            setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT + EMOTION_CANVAS_HEIGHT);
        }
    }//GEN-LAST:event_cmdEmoticonsActionPerformed

    private ChatClient chatClient;
    private String userName;
    private boolean emotionFlag;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdClear;
    private javax.swing.JButton cmdClose;
    private javax.swing.JButton cmdEmoticons;
    private javax.swing.JButton cmdIgnore;
    private javax.swing.JButton cmdSend;
    private javax.swing.JTextPane emotionPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblConversation;
    private javax.swing.JTextPane messagePane;
    private javax.swing.JTextField txtMessage;
    // End of variables declaration//GEN-END:variables

    private void sendMessage() {
        appendToPane(messagePane,
                "<span>" + chatClient.getUserName() + ": " + txtMessage.getText() + "</span>");
        chatClient.sentPrivateMessageToServer(txtMessage.getText(), userName);
        txtMessage.setText("");
        txtMessage.requestFocus();
    }

    private void addIconsToMessage() {
        StyledDocument doc = emotionPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        try {
            for (int i = 1; i < 22; i++) {
                Style s = doc.addStyle("icon" + (i - 1), def);
                ImageIcon icon = createImageIcon("icons/photo" + (i - 1) + ".gif", String.valueOf(i - 1));
                if (icon != null) {
                    StyleConstants.setIcon(s, icon);
                }
                if (i % 6 == 0) {
                    doc.insertString(doc.getLength(), "  \n", doc.getStyle("icon" + (i - 1)));
                } else {
                    doc.insertString(doc.getLength(), " ", doc.getStyle("icon" + (i - 1)));
                }
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Function to Set the Image Name into Text Field
     *
     * @param imageName
     */
    private void addImageToTextField(String imageName) {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            txtMessage.setText("~~" + imageName + " ");
        } else {
            txtMessage.setText(txtMessage.getText() + " " + "~~" + imageName + " ");
        }
    }

    /**
     * Function to Add a Message To Message Pane
     * @param message
     */
    protected void addMessageToMessagePane(String message) {
        appendToPane(messagePane, "<span>" + message + "</span>");
    }

    // send html to pane
    private void appendToPane(JTextPane tp, String message) {
        var doc = (HTMLDocument) tp.getDocument();
        var editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void disableAll() {
        txtMessage.setEnabled(false);
        cmdSend.setEnabled(false);
    }

    public void enableAll() {
        txtMessage.setEnabled(true);
        cmdSend.setEnabled(true);
    }

    /**
     * Exit from Private Chat
     */
    private synchronized void exitPrivateWindow() {
        chatClient.removePrivateWindow(userName);
        setVisible(false);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(String path, String description) {
        URL imgURL = PrivateChat.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public String getUserName() {
        return userName;
    }
    
}
