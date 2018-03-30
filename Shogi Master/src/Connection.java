
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ian
 */

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Observable{
    private Socket socket;
    private OutputStream outputStream;
    public final ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<>();
    
    public Connection(Socket socket, OutputStream outputStream){
        this.socket = socket;
        this.outputStream = outputStream;
    }
    
    public Connection(String server, int port) throws IOException{
        this.socket = new Socket(server, port);
        this.outputStream = socket.getOutputStream();
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
    
    public void open_connection(){
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.add(line);
                //notifyObservers(line);
            }
        } catch (IOException ex) {
            //notifyObservers(ex);
        }
    }
    
    public void close_connection(){
        try {
            outputStream.close();
            socket.close();
        } catch (IOException ex) {
            //notifyObservers(ex);
        }
    }
    
    /*String clientName = nameField.getCharacters().toString();
    String server = hostField.getCharacters().toString();
    if(clientName == null) clientName = "Anonymous";
    
    try {
    Connection connection = new Connection(server, PORT);
    } catch (IOException ex) {
    AlertBox.display("Fail", "Connection Failed!");
    }
    
    //start listening
    connection.start();
    
    //troca a tela
    
    Thread receivingThread = new Thread() {
    @Override
    public void run() {
    try {
    while(!connection.buffer.poll().equals("OpponentFound!")){sleep(1000);}
    
    } catch (InterruptedException ex) {
    Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    };
    receivingThread.start();*/
}
