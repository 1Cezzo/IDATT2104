import java.io.*;
import java.net.*;

class SocketServer {
  public static void main(String[] args) throws IOException {
    final int PORTNR = 1250;

    ServerSocket server = new ServerSocket(PORTNR);
    System.out.println("Server log. Waiting for connections...");
    Socket connection = server.accept();  // waits until someone connects

    InputStreamReader inputConnection = new InputStreamReader(connection.getInputStream());
    BufferedReader readerConnection = new BufferedReader(inputConnection);
    PrintWriter writerConnection = new PrintWriter(connection.getOutputStream(), true);

    writerConnection.println("Hello, you are connected to the server side!");
    writerConnection.println("Write what you want, and I'll repeat it. End with a new line.");

    String line = readerConnection.readLine();
    while (line != null) {  // connection on client side is closed
      System.out.println("A client wrote: " + line);
      writerConnection.println("You wrote: " + line);  // sends response to the client
      line = readerConnection.readLine();
    }

    readerConnection.close();
    writerConnection.close();
    connection.close();
  }
}
