import java.io.*;
import java.net.*;

class SocketServer {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;
        ServerSocket server = null;
        
        try {
            server = new ServerSocket(PORTNR);
            System.out.println("Server log. Waiting for connections...");
            
            while (true) {
                Socket connection = server.accept();
                System.out.println("New connection accepted.");

                Thread clientThread = new Thread(new ClientThreadHandler(connection));
                clientThread.start();
            }
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }
}
