import java.io.*;
import java.net.*;
import java.util.Scanner;

class SocketClient {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;
        Scanner reader = new Scanner(System.in);
        Socket connection = null;

        try {
            System.out.print("Enter the name of the server machine: ");
            String serverMachine = reader.nextLine();

            connection = new Socket(serverMachine, PORTNR);
            System.out.println("Connection established.");

            BufferedReader readerConnection = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter writerConnection = new PrintWriter(connection.getOutputStream(), true);

            // Read server's introduction messages
            for (int i = 0; i < 2; i++) {
                System.out.println(readerConnection.readLine());
            }

            String line = "";
            while (true) {
                // Input two numbers and operation
                System.out.print("Enter two numbers separated by space, followed by 'add' or 'sub', or press Enter to exit: ");
                line = reader.nextLine();
                if (line.isEmpty()) break;

                writerConnection.println(line); // Send input to server
                String response = readerConnection.readLine(); // Receive response from server
                System.out.println("From the server: " + response);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
