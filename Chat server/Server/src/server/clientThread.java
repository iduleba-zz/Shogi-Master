/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */

public class clientThread extends Thread{
    private String clientName = null;
    private BufferedReader is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final ConcurrentLinkedQueue<clientThread> threads;
    private final ConcurrentLinkedQueue<clientThread> queue;
    private clientThread opponent;
    private final String date;
    public boolean wasInterrupted = false;

    public clientThread(Socket clientSocket, ConcurrentLinkedQueue<clientThread> threads, ConcurrentLinkedQueue<clientThread> queue, String date) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.queue = queue;
        clientName = "Anonymous";
        opponent = null;
        this.date = date;
    }

    private void instructions(PrintStream os, String name, String op){
        os.println("Every message is required to have a prefix:");
        os.println("|" + name + "|" + op + "|Your message");
        os.println("If you wish to quit the game, begin your message with /quit");
        os.println("|" + name + "|" + op + "|/quit");
    }
    private boolean findOpponent() throws Exception{
        if(opponent != null){
            //opponent has been assigned by another thread
            this.os.println("Opponent Found: " + this.opponent.clientName);
            instructions(os, this.clientName, this.opponent.clientName);
            instructions(this.opponent.os, this.opponent.clientName, this.clientName);
            return true;
        }
        
        Iterator<clientThread> itr = this.queue.iterator();
        while (itr.hasNext()){
            clientThread challenger = itr.next();
            if(challenger != null && !challenger.equals(this)){
                if (challenger.opponent != null) {
                    throw new Exception("Assignement Inconsistency! Player: " + challenger.getName() + " already has an opponent: " + challenger.getopponent());
                }
                queue.remove(challenger);
                queue.remove(this);
                this.opponent = challenger;
                challenger.opponent = this;
                //this.os.println("Opponent: " + this.opponent.clientName);
                //instructions();

                return true;
            }
        }
        
        //no clients left
        return false;
    }
    
    public String getopponent(){
        if (opponent==null) return null;
        return opponent.getclientName();
    }
    
    public String getclientSocket(){
        if (clientSocket==null) return null;
        return clientSocket.toString();
    }
    
    public String getclientName(){
        return clientName;
    }
    
    public String getdate(){
        return date;
    }
    
    public Socket getSocket(){
        return clientSocket;
    }
    
    /* If the message is valid
     * that is, if it has the following format: |NameSender(this)|NameReceiver(opponent)|move(or /quit)
     */
    private String[] interpretMessage(String input){
        //debug
        System.out.println(input);
        
        if (input.startsWith("|")) {
            String[] msg = input.split("\\|");
            if (msg.length == 4 && msg[1] != null && msg[2] != null && msg[3] != null) {
                msg[1] = msg[1].trim();
                msg[2] = msg[2].trim();
                msg[3] = msg[3].trim();
                if (!msg[1].isEmpty() && !msg[2].isEmpty() && !msg[3].isEmpty()){
                    return msg;
                } else{return null;}
            } else{return null;}
        } else{return null;}
    }
    
    //kill player
    private synchronized void RageQuit(){
        opponent.os.println("*** The user " + clientName + " is leaving! ***");
        os.println("*** Bye " + clientName + ". GG Easy ***");
        threads.remove(opponent);
        opponent.opponent = null;
        opponent.wasInterrupted = true;
        opponent = null;
        threads.remove(this);
        wasInterrupted = true;
    }
    
    public void goodBye(){
        // interrupted by other thread
        os.println("*** Server closed ***");
    }
    
    private boolean validateMove(String input){
        return true;
    }
    
    boolean shouldStop() {
        if(!wasInterrupted)
            wasInterrupted = isInterrupted();
        return wasInterrupted;
    }
    
    /**
     *
     */
    @Override
    public void run() {
        try {
            //Create input and output streams for this client
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            
            //Read name of the client
            String name = "";
            while (!shouldStop() && !clientSocket.isClosed()) {
                os.println("Enter your name.");
                name = is.readLine().trim();
                if (name.indexOf('|') == -1) {
                    break;
                }
                else {
                    os.println("The name should not contain '|' character.");
                }
            }
            
            if(name!=null){
                clientName = name;
                os.println("Welcome " + clientName + "!");
                os.println("Finding a challenger...");
            }
            
            //Let's find a challenger (the first one avaible to play, i.e., the one that has been waiting the longest)
            if(this.opponent == null){
                while(findOpponent()==false && !shouldStop() && !clientSocket.isClosed()) {sleep(5000);} //wait 5 seconds (lessen the burden on the server)
            }
            else{//opponent has been assigned by another thread
                this.os.println("Opponent: " + this.opponent.clientName);
                this.opponent.os.println("Opponent: " + this.clientName);
                instructions(os, this.clientName, this.opponent.clientName);
                instructions(this.opponent.os, this.opponent.clientName, this.clientName);
            }
            
            /* Start the game (exchange of messages) */
            while (!shouldStop() && !clientSocket.isClosed()) {
                String input = is.readLine(); if(shouldStop()){break;}
                String[] msg = interpretMessage(input);
                if(msg != null && clientName.equals(msg[1]) && this.opponent.clientName.equals(msg[2]) ){
                    if (msg[3].startsWith("/quit")){
                        this.RageQuit();
                        break;
                    }
                    if (validateMove(msg[3])){
                        opponent.os.println(">" + clientName + "< " + msg[3]);
                        this.os.println("<" + clientName + "> " + msg[3]);
                        
                    } else{ System.out.println("Invalid message: " + input); }
                    
                } else{ System.out.println("Invalid message: " + input); }
            }
            
            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            if (!clientSocket.isClosed()){
                clientSocket.close();
            }
        }
        catch (InterruptedException ie){}
        catch (SocketException exception) {}
        catch (Exception e) {JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);}
    }
}
