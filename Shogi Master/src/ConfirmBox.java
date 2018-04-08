
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;

import java.io.IOException;

public class ConfirmBox {

    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Parent rootLayout;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/confirmLayout.fxml"));
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        boolean decision[] = new boolean[1];

        Scene scene = new Scene(rootLayout);
        window.setScene(scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            decision[0] = false;
        });        

        Button yesBtn = (Button) scene.lookup("#yesBtn");
        yesBtn.setOnAction(e -> {
            decision[0] = true;
            window.close();
        });

        Button noBtn = (Button) scene.lookup("#noBtn");
        noBtn.setOnAction(e -> {
            decision[0] = false;
            window.close();
        });

        Label label = (Label) scene.lookup("#lbl");
        label.setText(message);

        window.showAndWait();
        return decision[0];
    }
}
