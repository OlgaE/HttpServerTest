package com.demo;

import java.net.ServerSocket;
import java.net.Socket;

public class Launch {
    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        String dirNameSrc = args[1];

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Waiting ...");
            System.out.println();
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new NewThread(socket, dirNameSrc)).start();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
