package org.sadegh;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpsSocket {

    public static void main(String[] args) throws IOException {
        // This is a placeholder for the main method.
        // The actual implementation would go here.
        System.out.println("HttpsSocket is running...");
        ServerSocket serverSocket = new ServerSocket(1212);
        serverSocket.setReuseAddress(true);
        while (true) {
            try {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();

                System.out.println("Accepted new connection from " + clientSocket.getInetAddress());

                InputStream inputStream =  clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                System.out.println("Received request: " + line);
                handleConnectMethod(line, inputStream, outputStream);
                clientSocket.close();
                // Here you would handle the input stream, e.g., read data from the client

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    private static void handleConnectMethod(String uriLine, InputStream clientIn, OutputStream clientOut){
        // get host and port from uri;
        String host = uriLine.split(" ")[1].split(":")[0];
        String port = uriLine.split(" ")[1].split(":")[1];

        System.out.println("Handling CONNECT method for host: " + host + " on port: " + port);


        try(Socket serverSocket = new Socket(host, Integer.parseInt(port))){
            clientOut.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
            clientOut.flush();

            InputStream serverIn = serverSocket.getInputStream();
            OutputStream serverOut = serverSocket.getOutputStream();

            Thread clInServOut = new Thread(() -> streamData(clientIn, serverOut) );
            Thread servInClOut = new Thread(() -> streamData(serverIn, clientOut) );
            clInServOut.start();
            servInClOut.start();
            clInServOut.join();
            servInClOut.join();



        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            try {
                clientOut.write(("HTTP/1.1 502 Bad Gateway\r\n\r\n").getBytes());
                clientOut.flush();
            } catch (IOException ioException) {
                System.out.println("Error sending response: " + ioException.getMessage());
            }
        }

    }

    private static void streamData(InputStream in, OutputStream out){
        try{
            byte[] buffer = new byte[4096];
            int len;
            while((len = in.read(buffer)) != -1){
                out.write(buffer, 0, len);
                out.flush();
            }
        }
        catch (Exception e){
            System.out.println("Error in streamData: " + e.getMessage());
        }


    }
}
