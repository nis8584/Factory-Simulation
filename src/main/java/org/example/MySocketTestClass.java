package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MySocketTestClass {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(null));
        socket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(socket.getInputStream().read() != -1){
            String incoming = in.readLine();
            System.out.println("received Message: " + incoming);
        }

    }
    public void stop() throws IOException {
        in.close();
        serverSocket.close();
        socket.close();
    }
    public static void main(String[] args){
        MySocketTestClass server = new MySocketTestClass();
        try {
            server.start(6666);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        System.out.println("Connection is closed");
    }
}
