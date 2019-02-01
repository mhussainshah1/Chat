package chat.server;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatCommunication implements Runnable, CommonSettings {

    /**
     * ******** Global Variable Declarations **************
     */
    Thread thread;
    Socket socket;
    //DataInputStream in;
    String RFC;
    ChatServer server;
    BufferedReader in;

    /**
     * ****** Initialize the Socket to the Client **********
     */
    ChatCommunication(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
            //in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        thread = new Thread(this);
        thread.start();
    }

    private void quitConnection() {
        thread.interrupt();//stop();
        thread = null;
        try {
            socket.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        socket = null;
    }

    /**
     * ****** Implement the Thread Interface ********
     */
    @Override
    public void run() {
        while (thread != null) {
            try {
                RFC = in.readLine();
                /**
                 * ***** RFC Checking *************
                 */
                if (RFC.startsWith("HELO")) {
                    server.addUser(socket, RFC.substring(5));
                }
                if (RFC.startsWith("QUIT")) {
                    server.removeUser(RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1),
                            REMOVE_USER);
                    quitConnection();
                }
                if (RFC.startsWith("KICK")) {
                    server.removeUser(RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1),
                            KICK_USER);
                    quitConnection();
                }
                if (RFC.startsWith("CHRO")) {
                    server.changeRoom(socket, RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("MESS")) {
                    server.sendGeneralMessage(socket, RFC.substring(RFC.indexOf(":") + 1),
                            RFC.substring(RFC.indexOf("~") + 1, RFC.indexOf(":")), RFC.substring(5, RFC.indexOf("~")));
                }
                if (RFC.startsWith("PRIV")) {
                    server.sendPrivateMessage(RFC.substring(RFC.indexOf("~") + 1), RFC.substring(5, RFC.indexOf("~")));
                }
                if (RFC.startsWith("ROCO")) {
                    server.getUserCount(socket, RFC.substring(5));
                }
                if (RFC.startsWith("CALL")) {
                    server.requestForVoiceChat(socket, RFC.substring(5, RFC.indexOf("~")),
                            RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("ACCE")) {
                    server.sendUserIP(socket, RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("CANC")) {
                    server.rejectCall(RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("QVCT")) {
                    server.quitVoiceChat(RFC.substring(5, RFC.indexOf("~")), RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("REIP")) {
                    server.getRemoteUserAddress(socket, RFC.substring(5, RFC.indexOf("~")),
                            RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("AEIP")) {
                    server.sendRemoteUserAddress(socket, RFC.substring(5, RFC.indexOf("~")),
                            RFC.substring(RFC.indexOf("~") + 1));
                }
                if (RFC.startsWith("QUVC")) {
                    server.quitVideoChat(RFC.substring(5));
                }
            } catch (IOException e) {
                server.removeUserWhenException(socket);
                quitConnection();
                e.printStackTrace();
            }
        }
    }
}