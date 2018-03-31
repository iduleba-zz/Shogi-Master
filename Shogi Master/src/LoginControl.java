import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.stage.Modality;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginControl {
    Connection connection = null;
    String[] players;
    String name;
    String host;
    Stage window;

    public LoginControl(String name, String host, String[] players, Stage window) {
        this.name = name;
        this.host = host;
        this.players = players;
        this.window = window;
    }

    public Connection validate() {
        //send Name to the server
        if (name == null || name.equals("")) {
            name = "Anonymous";
        }
        players[0] = name;
        
        //start connection
        try {
            connection = new Connection(host);
        } catch (IOException ex) {
            AlertBox.display("Fail", "Connection Failed!");
            return null;
        }

        //start listening
        connection.start();

        //Creating window message ---------------------------------------------------
        Stage wMsg = new Stage();
        wMsg.initModality(Modality.APPLICATION_MODAL);
        wMsg.setTitle("Waiting...");
        wMsg.setOnCloseRequest(e -> {
            if(connection!=null) try {
                connection.send("Quit");
                connection.close_connection();
            } catch (IOException ex) {System.out.println("Exit not successfull");}
        });
        
        Parent rootLayout;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/messageBoxLayout.fxml"));
            rootLayout = loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            return connection;
        }
        Scene scene = new Scene(rootLayout);
        Label label = (Label) scene.lookup("#lbl");
        label.setText("Looking for players...");
        wMsg.setScene(scene);
        wMsg.setResizable(false);
        wMsg.show();
        //--------------------------------------------------------------------------

        connection.send(name);

        //Set Opponent
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() {
                        try {
                            String line;
                            while ((line = connection.buffer.poll()) == null ||( 
                                    !line.startsWith("Opponent Found: ") &&
                                    !line.startsWith("*** Server closed ***") &&
                                    !line.startsWith("No opponents"))) {
                                Thread.sleep(250);
                            }
                            return line;
                        } catch (InterruptedException ex) {
                            return null;
                            //Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                    @Override
                    protected void succeeded() {
                        String ret = getValue();
                        if(ret.equals("*** Server closed ***")){
                            if(connection!=null) try {
                                connection.close_connection();
                            } catch (IOException ex) {}
                            AlertBox.display("Server Shutdown", "*** Server closed ***");
                            wMsg.close();
                        }
                        else if(ret.equals("No opponents")){
                            if(connection!=null) try {
                                connection.close_connection();
                            } catch (IOException ex) {}
                            AlertBox.display("=(", "No other player connected. Try again later.");
                            wMsg.close();
                        }
                        else{
                            players[1] = getValue().replace("Opponent Found: ","");
                            window.close();
                            wMsg.close();
                        }
                    }
                };
            }
        };
        service.start();
        return connection;
    }
}
