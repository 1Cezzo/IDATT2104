package exercise_4;

import java.io.*;
import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        final int PORTNR = 1250;
        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(PORTNR);

            byte[] receiveData = new byte[1024];

            System.out.println("Server log. Waiting for connections...");

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received from client: " + receivedData);

                String[] parts = receivedData.split(" ");
                if (parts.length == 3) {
                    try {
                        double num1 = Double.parseDouble(parts[0]);
                        double num2 = Double.parseDouble(parts[1]);
                        String operation = parts[2];
                        double result;
                        if (operation.equalsIgnoreCase("add")) {
                            result = num1 + num2;
                        } else if (operation.equalsIgnoreCase("sub")) {
                            result = num1 - num2;
                        } else {
                            result = Double.NaN;
                        }

                        String response = "Result: " + result;
                        byte[] sendData = response.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                        serverSocket.send(sendPacket);
                    } catch (NumberFormatException e) {
                        String response = "Invalid input format. Please enter two numbers followed by 'add' or 'sub'.";
                        byte[] sendData = response.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                        serverSocket.send(sendPacket);
                    }
                } else {
                    String response = "Invalid input format. Please enter two numbers followed by 'add' or 'sub'.";
                    byte[] sendData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    serverSocket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}
