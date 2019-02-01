package chat.server;

import java.net.Socket;

public class ClientObject {

    private Socket clientSocket;
    private String clientUserName, clientRoomName;

    public ClientObject(Socket clientSocket, String clientUserName, String clientRoomName) {
        this.clientSocket = clientSocket;
        this.clientUserName = clientUserName;
        this.clientRoomName = clientRoomName;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public String getClientRoomName() {
        return clientRoomName;
    }

    public void setClientRoomName(String clientRoomName) {
        this.clientRoomName = clientRoomName;
    }
}
