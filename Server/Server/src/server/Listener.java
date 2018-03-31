/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;
import java.util.Date;

/**
 *
 * @author user
 */
public class Listener extends Thread{
    final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    //this server can handle only MAX_CONNECTIONS clients (it should always be an even number!)
    final static int MAX_CONNECTIONS = 20;
    
    //Set the default port. 4444 is open for use (normally).
    final int portNumber = 4444;
    
    //private static final clientThread[] clientThreads = new clientThread[MAX_CONNECTIONS];
    public final ConcurrentLinkedQueue<clientThread> clientThreads = new ConcurrentLinkedQueue<>();
    
    //Players waiting for an opponent
    public final ConcurrentLinkedQueue<clientThread> queue = new ConcurrentLinkedQueue<>();
    
    // The server socket.
    public ServerSocket serverSocket = null;
    // The client socket.
    private Socket clientSocket = null;
    
    //stop server
    public void stopListening() throws IOException{
        //close all conections
        clientThread client;
        while((client=clientThreads.poll())!=null){
            client.goodBye();
            client.getSocket().close();
            client.interrupt();
        }
    }

    @Override
    public void run() {
        /*
         * Open a server socket on the portNumber (default 4444).
         */
        try{
            serverSocket = new ServerSocket(portNumber);
        }
        catch (IOException e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);
            try {throw e;} catch (IOException ex) {JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);}
        }
        /*
         * Create a client socket for each connection and pass it to a new client thread.
         */
        while (!this.isInterrupted()){
            try{
                clientSocket = serverSocket.accept();
                if(clientThreads.size() < MAX_CONNECTIONS){
                    clientThread newClient = new clientThread(clientSocket, clientThreads, queue, dateFormat.format(new Date())); 
                    clientThreads.add(newClient);
                    queue.add(newClient);
                    newClient.start();
                }
                else{
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("No connections available!");
                    os.close();
                    clientSocket.close();
                }
            }
            catch (SocketException exception) {
                try {
                    //JOptionPane.showMessageDialog(null, exception, "Error", JOptionPane.INFORMATION_MESSAGE);
                    stopListening();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            }
            
            catch (IOException e){
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            
        }
    }
    
}
