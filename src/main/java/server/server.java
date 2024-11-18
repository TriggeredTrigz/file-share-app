package server;

import misc.*;

import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class server {

    public static int serverPort = 1234;
    public static String serverAddress = "";
    public static InetAddress serverInetAddress;
    public static ServerSocket serverSocket;
    
    public static Socket clientSocket;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;

    public static void startServer() {

        try{

            // finds inetAddress to initiate server on
            Enumeration<NetworkInterface> netInts = NetworkInterface.getNetworkInterfaces(); 
            for (NetworkInterface netInt : Collections.list(netInts)) {
                if (!( 
                    ( 
                        netInt.getDisplayName().contains("Intel") || 
                        netInt.getDisplayName().contains("Realtek") || 
                        netInt.getDisplayName().contains("MediaTek") ||
                        netInt.getDisplayName().contains("Asus") ||
                        netInt.getDisplayName().contains("Qualcomm") ||
                        netInt.getDisplayName().contains("TP-Link") ||
                        netInt.getDisplayName().contains("Broadcom") ||
                        netInt.getDisplayName().contains("D-Link") ||
                        netInt.getDisplayName().contains("Netgear") ||
                        netInt.getDisplayName().contains("Edimax")
                    ) 
                    && ( 
                        netInt.getDisplayName().contains("Ethernet") || 
                        netInt.getDisplayName().contains("Wi-Fi") 
                    )
                    )) continue;
                Enumeration<InetAddress> inetAdds = netInt.getInetAddresses();
                for (InetAddress inetAdd : Collections.list(inetAdds)) {
                    if (!inetAdd.isLinkLocalAddress()) {
                        serverInetAddress = inetAdd;
                    }
                }
            }

            // initates serverSocket
            serverSocket = new ServerSocket(serverPort, 0, serverInetAddress);
            System.out.println("Initialized server at " + serverSocket.getLocalSocketAddress());

            // listen for clients
            while (!serverSocket.isClosed()) {

                // accept client socket connection
                clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort() + " at port " + clientSocket.getPort());

                // declare buffer variables to send and read outgoing and incoming messages
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                break;
            }
        } catch (Exception e) { e.printStackTrace(); }
    
    }
    
    public static void sendFile() {

        File file;

        try {

            // gets file from user/server-admin
            while (true) {
                System.out.println("Enter file with path: ");
                String fileName = misc.input.nextLine();
                file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    break;
                } else {
                    System.out.println("Enter valid file with its path.");
                }
            }

            // sends client file name to confirm
            broadcast(file.getName());
            String confirm = listen();

            // client confirms
            if (confirm.equals("y")) {

                // declares Stream variables for file transfer
                // File file = new File("C:/hello.txt");
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
    
                OutputStream outputStream = clientSocket.getOutputStream();

                System.out.println("Awaiting client confirmation to initiate file transfer...");
                listen();
    
                // starts file transfer
                System.out.println("Starting file transfer:");
                long start = System.nanoTime();

                byte[] contents;
                long fileLength = file.length();
                long current = 0;
                while ( current!=fileLength ) {
                    int size = 10000;
                    if ( fileLength - current >= size ) {
                        current += size;
                    } else {
                        size = (int)(fileLength - current) ;
                        current = fileLength;
                    }
                    contents = new byte[size];
                    bis.read(contents, 0, size);
                    outputStream.write(contents);
                    System.out.println("Progress: " + (current*100)/fileLength + "%");
                }
                outputStream.flush();
                outputStream.close();
                long stop = System.nanoTime();
                System.out.println("File sent in " + (stop-start) + "ns.");
                bis.close();
                fis.close();

                System.exit(0);

            } else { 
                // client denies
                System.out.println("Client denied file transfer for the file: " + file.getName());
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    // listens for message from client
    private static String listen() {

        String clientMessage = null;
        try{
            clientMessage = bufferedReader.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        return clientMessage;

    }

    // sends message to client
    private static void broadcast(String message) {

        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) { e.printStackTrace(); }

    }
    
}
