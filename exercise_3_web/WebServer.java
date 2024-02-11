import java.io.*;
import java.net.*;

public class WebServer {
    public static void main(String[] args) throws IOException {
        final int PORT = 80;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                Thread clientThread = new Thread(new WebServerThread(clientSocket));
                clientThread.start();
            }
        }
    }
}

class WebServerThread implements Runnable {
    private Socket clientSocket;

    public WebServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            StringBuilder requestHeaders = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestHeaders.append("<LI>").append(line).append("</LI>");
            }

            writer.println("HTTP/1.0 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println();
            writer.println("<HTML><BODY>");
            writer.println("<H1> Hilsen. Du har koblet deg opp til min enkle web-tjener </h1>");
            writer.println("Header fra klient er:");
            writer.println("<UL>");
            writer.println(requestHeaders);
            writer.println("</UL>");
            writer.println("</BODY></HTML>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
