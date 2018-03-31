import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.net.Socket;

import java.io.IOException;
import javafx.application.Platform;

public class Main extends Application {
    static boolean exit = false;
    
    public static void main(String[] args) {
        launch(args); //launch the program as a javaFX application
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Start Login window
        Socket socket = null;
        String players[];
        players = loginScene(socket);

        //verify if socket is connected
        primaryStage.setMaximized(true);
        
        BorderPane bPane = new BorderPane();
        Scene scene = new Scene(bPane);
        primaryStage.setScene(scene);
        
        Pane centerPane = new Pane();
        centerPane.setMaxSize(0.5 * scene.getWidth(), 0.8 * scene.getHeight());

        //---------------------------------------------------------------------
        Board board = new Board(centerPane, -250, -255, 500, 510, true); //must be created before the pieces
        Player p1 = new Player("player1", Player.BLACK, Player.LOCAL);
        Player p2 = new Player("player2", Player.WHITE, Player.REMOTE);        
        p1.initPieces(board);
        p2.initPieces(board);

        //---------------------------------------------------------------------
        bPane.setCenter(centerPane);
        
        scene.setFill(Color.rgb(240, 240, 240));
        primaryStage.setScene(scene);
        if(!exit)
            primaryStage.show();
    }
    
    public String[] loginScene(Socket socket) {
        Stage window = new Stage();
        window.setTitle("Shogi");
        Parent rootLayout;
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/loginLayout.fxml"));
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        Scene scene = new Scene(rootLayout);
        window.setScene(scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            this.exit = true;
            Platform.exit();
        });
        
        Button startBtn = (Button) scene.lookup("#BtnStart");
        String[] players = new String[2];
        
        startBtn.setOnAction(event -> {
            TextField nameField = (TextField) scene.lookup("#NameField");
            TextField hostField = (TextField) scene.lookup("#HostField");
            String name = nameField.getCharacters().toString();
            String host = hostField.getCharacters().toString();
            if(name.length() > 20){
                AlertBox.display("Oops", "Name too long!");
            }
            else{
                LoginControl login = new LoginControl(name, host, socket, players, window);
                login.validate();
            }
        });
        
        window.showAndWait();
        return players;
    }
}