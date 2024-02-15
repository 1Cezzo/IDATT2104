package exercise_4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        final int PORTNR = 1250;
        Scanner reader = new Scanner(System.in);
        DatagramSocket clientSocket = null;

        try {
            clientSocket = new DatagramSocket();

            System.out.print("Enter the name of the server machine: ");
            String serverMachine = reader.nextLine();
            InetAddress serverAddress = InetAddress.getByName(serverMachine);

            // Input numbers and operation from the user
            System.out.print("Enter two numbers separated by space, followed by 'add' or 'sub', or press Enter to exit: ");
            String line = reader.nextLine();

            while (!line.isEmpty()) {
                byte[] sendData = line.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORTNR);
                clientSocket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server response: " + response);

                System.out.print("Enter two numbers separated by space, followed by 'add' or 'sub', or press Enter to exit: ");
                line = reader.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }
}
