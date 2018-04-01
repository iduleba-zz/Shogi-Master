
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class Connection extends Thread{
    public static final int PORT = 4444; 
    public static final String keepAlive = "Keep Alive";
    private Socket socket;
    private OutputStream outputStream;
    public final ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<>();
    
    public Connection(Socket socket, OutputStream outputStream){
        this.socket = socket;
        this.outputStream = outputStream;
    }
    
    public Connection(String server) throws IOException{
        this.socket = new Socket(server, PORT);
        this.outputStream = socket.getOutputStream();
    }
    
    private static final String CRLF = "\r\n"; // newline

    /**
     * Send a line of text
     */
    public void send(String text) {
        try {
            outputStream.write((text + CRLF).getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            if (ex.getMessage().contains("Broken pipe")) {
                System.out.println(ex.getMessage());
                //notifyObservers("Closed Connection.");
            } else {
                System.out.println(ex.getMessage());
                //notifyObservers(ex);
            }
        }
    }

    /**
     * Close the socket
     */
    public void close_connection() throws IOException{
        socket.close();
    }
    
    @Override  
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(!line.equals("") && !line.equals(keepAlive)) buffer.add(line);
                //notifyObservers(line);
            }
        } catch (IOException ex) {
            //notifyObservers(ex);
        }
    }
}
