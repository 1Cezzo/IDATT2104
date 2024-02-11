import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadHandler implements Runnable {

  private Socket connection;

  public ClientThreadHandler(Socket connection) {
      this.connection = connection;
  }

  @Override
  public void run() {
      try {
          InputStreamReader inputConnection = new InputStreamReader(connection.getInputStream());
          BufferedReader readerConnection = new BufferedReader(inputConnection);
          PrintWriter writerConnection = new PrintWriter(connection.getOutputStream(), true);

          writerConnection.println("Hello, you are connected to the server side!");
          writerConnection.println("Enter two numbers separated by a space, followed by 'add' or 'sub'. End with a new line.");

          String line = readerConnection.readLine();
          while (line != null) { 
              String[] parts = line.split(" ");
              if (parts.length == 3) {
                  try {
                      double num1 = Double.parseDouble(parts[0]);
                      double num2 = Double.parseDouble(parts[1]);
                      String operation = parts[2];
                      double result;
                      if (operation.equalsIgnoreCase("add")) {
                          result = num1 + num2;
                          writerConnection.println("Result: " + result);
                      } else if (operation.equalsIgnoreCase("sub")) {
                          result = num1 - num2;
                          writerConnection.println("Result: " + result);
                      } else {
                          writerConnection.println("Invalid operation. Please enter 'add' or 'sub'.");
                      }
                  } catch (NumberFormatException e) {
                      writerConnection.println("Invalid input format. Please enter two numbers followed by 'add' or 'sub'. For example: 5 3 add");
                  }
              } else {
                  writerConnection.println("Invalid input format. Please enter two numbers followed by 'add' or 'sub'. For example: 5 3 add");
              }
              line = readerConnection.readLine();
          }

          readerConnection.close();
          writerConnection.close();
          connection.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}