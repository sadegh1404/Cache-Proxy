package org.sadegh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


// TODO: Add cli arguments to specify port and other configurations
// TODO: Add logging instead of System.out.println
// TODO: Add Tests
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        int port = 1212;
        try(ServerSocket proxyServerSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while(true){
                try{
                    Socket clientSocket = proxyServerSocket.accept();
                    new Thread(new HttpsSocket(clientSocket)).start();

                }catch (Exception e) {
                    System.out.println("Error in main loop: " + e.getMessage());
                }
            }
        }catch (IOException e) {
            System.out.println("Error starting server on port " + port + ": " + e.getMessage());
            throw new IOException("Failed to start server on port " + port, e);
        }


    }

}