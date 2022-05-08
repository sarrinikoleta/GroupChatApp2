import java.io.*;
import java.net.*;
import java.util.Scanner;


//kathe fora pairnw ena connection kai to dinw se ena thread
//
public class Consumer extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    BufferedWriter writer;  //And a writer to send messages.
    String profileName;
    Socket connection;
    String topic;
    BufferedReader keyboard;
    String historyPath;




    public Consumer(Socket connection , String topic ,String profileName ,int port) {
        try {

            this.profileName = profileName;
            this.connection = connection;
            this.topic = topic;
            System.out.println("Got a connection Consumer - Broker ...Opening streams....");


            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());



        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void run() {
        try {


            // Dinoume to Connection Type ston broker
            out.writeObject(new SocketMessage("CONSUMER_CONNECTION",new SocketMessageContent(topic)));
            out.flush();

            SocketMessage reply = (SocketMessage) in.readObject();



            // History Reading First Time Only
            if (reply.getType().equals("USER_TOPIC_FULL_HISTORY")) {

                while (!reply.getContent().getMessage().isEmpty()){
                    if (reply.getType().equals("USER_TOPIC_CHUNK")) {
                        reply = (SocketMessage) in.readObject();
                        System.out.println(reply.getContent().getMessage());
                    }
                }

            }



            while(true) {
                // Listen for broker messages.

                if (reply.getType().equals("USER_MULTIMEDIA_CHUNK")) {
                    // Get info from the message.
                    //Broker send the path of the file
                    //Notify User
                    System.out.println("File sending " + reply.getContent().getMessage());
                }

                if(reply.getType().equals("USER_MESSAGE")){
                    System.out.println(reply.getContent().getMessage());
                    //edw tha pairnw apo ton broker ta mnmt pou stelnei o publisher ston broker
                }


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


}