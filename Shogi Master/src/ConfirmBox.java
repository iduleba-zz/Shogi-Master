import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.stage.Modality;

import java.io.IOException;

public class ConfirmBox {

	public static void display(String title, String message, boolean[] decision) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		
		Parent rootLayout;
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/confirmLayout.fxml"));
			rootLayout = loader.load();
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}

		Scene scene = new Scene(rootLayout);
		window.setScene(scene);
		window.setResizable(false);
		window.show();

		Button yesBtn = (Button) scene.lookup("#yesBtn");
		yesBtn.setOnAction(e -> {
			decision[0] = true;
			window.close();
		});

		Button noBtn = (Button) scene.lookup("#noBtn");
		noBtn.setOnAction(e -> {
			decision[0] = true;
			window.close();
		});

		Label label = (Label) scene.lookup("#lbl");
		label.setText(message);

	}
}