
import java.net.Socket;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.stage.Modality;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;
import javafx.application.Platform;

public class LoginControl {
    Connection connection;
    String[] players;
    String name;
    String host;
    Socket socket;
    Stage window;

    public LoginControl(String name, String host, Socket socket, String[] players, Stage window) {
        this.name = name;
        this.host = host;
        this.players = players;
        this.socket = socket;
        this.window = window;
        this.connection = null;
    }

    public void validate() {
        //send Name to the server
        if (name == null || name.equals("")) {
            name = "Anonymous";
        }
        players[0] = name;

        //start connection
        try {
            connection = new Connection(host, Connection.PORT);
        } catch (IOException ex) {
            AlertBox.display("Fail", "Connection Failed!");
            return;
        }

        //start listening
        connection.start();

        //Creating window message ---------------------------------------------------
        Stage wMsg = new Stage();
        wMsg.initModality(Modality.APPLICATION_MODAL);
        wMsg.setTitle("Waiting...");
        window.setOnCloseRequest(e -> {
            if(connection!=null) connection.close_connection();
            Platform.exit();
        });
        
        Parent rootLayout;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/messageBoxLayout.fxml"));
            rootLayout = loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
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
                            while ((line = connection.buffer.poll()) == null || !line.startsWith("Opponent Found: ")) {
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
                        players[1] = getValue();
                        System.out.println(players[0] + "!" + players[1]);
                        window.close();
                        wMsg.close();
                    }
                };
            }
        };
        service.start();

    }
}
