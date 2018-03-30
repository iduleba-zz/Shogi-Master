import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.stage.Modality;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginControl implements EventHandler<ActionEvent> {
	String[] players;
	String name;
	String host;
	Socket socket;
	Stage window;

	public LoginControl(Scene scene, Socket socket, String[] players, Stage window) {
		TextField nameField = (TextField) scene.lookup("#NameField");
		TextField hostField = (TextField) scene.lookup("#HostField");
		this.name = nameField.getCharacters().toString();
		this.host = hostField.getCharacters().toString();
		this.players = players;
		this.socket = socket;
		this.window = window;
	}

	@Override
	public void handle(ActionEvent e) {
		//tries connection
		/*try{
			socket = new Socket("localhost", 4444);
		} catch(IOException ex) { 
			AlertBox.display("Connection Failed", ex.getMessage());
		}*/

		//Creating window message ---------------------------------------------------
		Stage wMsg = new Stage();
		wMsg.initModality(Modality.APPLICATION_MODAL);
		wMsg.setTitle("Waiting...");
		Parent rootLayout;
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/messageBoxLayout.fxml"));
			rootLayout = loader.load();
		} catch(IOException ex) {
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

		Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask(){
            	return new Task<String>() {
            		@Override
            		protected String call() {
            			try{
            				Thread.sleep(5000);
            			} catch(InterruptedException ex) {}
            			return "BLA";
            		}

            		@Override 
            		protected void succeeded() {
     					window.close();
     					wMsg.close();
    				}
            	};
            }
        };
        service.start();


	}
}