package client;

import misc.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class client {

    public static Socket serverSocket;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;

    public static void startClient() {
        try{

            System.out.println("Enter server address:");
            String serverAddress = misc.input.nextLine();
            System.out.println("Enter server port:");
            int serverPort = misc.input.nextInt();
            misc.input.nextLine();

            serverSocket = new Socket(serverAddress, serverPort);

            bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void receiveFile() {

        System.out.println("Asking server for file name:");
        String fileName = listen();
        while (true) {
            System.out.println("Do you want to save file: " + fileName + "? (y/n) ");
            String confirm = misc.input.nextLine();
            broadcast(confirm);
            if(confirm.equals("y")) {
                String filePath;
                while (true) {
                    System.out.println("Enter directory to save to: ");
                    filePath = misc.input.nextLine();
                    File file = new File(filePath);
                    if ( file.exists() && file.isDirectory() ) {
                        break;
                    } else {
                        System.out.println("Enter valid directory.");
                    }
                }
        
                System.out.println("Starting file transfer...");
                long start = System.nanoTime();
        
                broadcast("start");
        
                try {
        
                    FileOutputStream fos = new FileOutputStream(filePath + fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    InputStream inputStream = serverSocket.getInputStream();

                    byte[] contents = new byte[10000];
                    int bytesRead = 0;
                    while ( (bytesRead=inputStream.read(contents)) > 0 ) {
                        bos.write(contents, 0, bytesRead);
                        bos.flush();
                    }
                    bos.flush();
                    bos.close();
        
                    long stop = System.nanoTime();
                    System.out.println("File received in " + (stop-start) + "ns.");
                    
                    break;
    
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    System.exit(0); 
                }
            } else if (confirm.equals("n")) {
                System.exit(0);
            } else {
                System.out.println("Invalid answer.");
            }
        }

    }

    // listens for message from client
    private static String listen() {

        String serverMessage = null;
        try{
            serverMessage = bufferedReader.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        return serverMessage;

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
