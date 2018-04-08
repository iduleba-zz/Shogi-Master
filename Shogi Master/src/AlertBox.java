
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;

import java.io.IOException;

public class AlertBox {

    public static void display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Parent rootLayout;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/alertLayout.fxml"));
            rootLayout = loader.load();
        } catch (IOException ex) {
            ex.getMessage();
            return;
        }

        Scene scene = new Scene(rootLayout);
        window.setScene(scene);
        window.setResizable(false);
        window.show();

        Button btn = (Button) scene.lookup("#OkBtn");
        btn.setOnAction(e -> window.close());

        Label label = (Label) scene.lookup("#lbl");
        label.setText(message);

    }
}
