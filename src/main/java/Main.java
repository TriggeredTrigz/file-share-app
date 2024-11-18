import server.*;
import client.*;
import misc.*;

public class Main {

    public static void main(String[] args) {

        while(true) {
            System.out.println("Client(c)/Server(s)/Exit(e)?");
            String appType = misc.input.nextLine();
            // if (appType.equals("c")){
            //     clientStuff();
            //     break;
            // } else if (appType.equals("s")) {
            //     serverStuff();
            //     break;
            // } else if (appType.equals("e")) {
            //     System.exit(0);
            // } else {
            //     System.out.println("what");
            // }
            switch (appType) {
                case "C":
                case "c": {
                    clientStuff();
                    break;
                }
                case "S":
                case "s": {
                    serverStuff();
                    break;
                }
                case "E":
                case "e": {
                    System.exit(0);
                }
                default:
                    System.out.println("what");
            }
        }
    }
    
    private static void serverStuff() {
        server.startServer();
        while (true) {
            System.out.println("Send file? (y/n)");
            String confirm = misc.input.nextLine();
            if (confirm.equals("y")) break;
            else System.exit(0);
        }
        server.sendFile();
    }
    
    private static void clientStuff() {
        client.startClient();
        while (true) {
            System.out.println("Receive file? (y/n)");
            String confirm = misc.input.nextLine();
            if (confirm.equals("y")) break;
            else System.exit(0);
        }
        client.receiveFile();
    }
    
}
