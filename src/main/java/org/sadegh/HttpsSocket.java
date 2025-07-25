package org.sadegh;

import java.io.*;
import java.net.Socket;

public class HttpsSocket implements Runnable {

    public Socket clientSocket;

    public HttpsSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try{
            System.out.println("Accepted new connection from " + clientSocket.getInetAddress());

            InputStream inputStream =  clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            this.handleHttpsConnection(line, inputStream, outputStream);
            clientSocket.close();

        }catch (Exception e){
            System.out.println("Error in HttpsSocket run method: " + e.getMessage());
        }

    }

    private void handleHttpsConnection(String uriLine, InputStream clientIn, OutputStream clientOut){
        // get host and port from uri;
        String[] uri = uriLine.split(" ")[1].split(":");
        String host = uri[0];
        String port = uri[1];

        System.out.println("Handling CONNECT method for host: " + host + " on port: " + port);

        try(Socket serverSocket = new Socket(host, Integer.parseInt(port))){
            clientOut.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
            clientOut.flush();

            InputStream serverIn = serverSocket.getInputStream();
            OutputStream serverOut = serverSocket.getOutputStream();

            Thread clInServOut = new Thread(() -> streamData(clientIn, serverOut));
            Thread servInClOut = new Thread(() -> streamData(serverIn, clientOut));
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
