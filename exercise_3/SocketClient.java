import java.io.*;
import java.net.*;
import java.util.Scanner;

class SocketClient {
  public static void main(String[] args) throws IOException {
    final int PORTNR = 1250;

    Scanner reader = new Scanner(System.in);
    System.out.print("Enter the name of the server machine: ");
    String serverMachine = reader.nextLine();

    Socket connection = new Socket(serverMachine, PORTNR);
    System.out.println("Connection established.");

    InputStreamReader inputConnection = new InputStreamReader(connection.getInputStream());
    BufferedReader readerConnection = new BufferedReader(inputConnection);
    PrintWriter writerConnection = new PrintWriter(connection.getOutputStream(), true);

    String intro1 = readerConnection.readLine();
    String intro2 = readerConnection.readLine();
    System.out.println(intro1 + "\n" + intro2);

    String line = reader.nextLine();
    while (!line.equals("")) {
      writerConnection.println(line);
      String response = readerConnection.readLine();
      System.out.println("From the server: " + response);
      line = reader.nextLine();
    }

    readerConnection.close();
    writerConnection.close();
    connection.close();
  }
}
