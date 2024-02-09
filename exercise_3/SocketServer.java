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
        writerConnection.println("Enter two numbers separated by a space, followed by 'add' or 'sub'. End with a new line.");

        String line = readerConnection.readLine();
        while (line != null) {  // connection on client side is closed
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
                    writerConnection.println("Invalid input format. Please enter two numbers followed by 'add' or 'sub'.");
                }
            } else {
                writerConnection.println("Invalid input format. Please enter two numbers followed by 'add' or 'sub'.");
            }
            line = readerConnection.readLine();
        }

        readerConnection.close();
        writerConnection.close();
        connection.close();
    }
}
