import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.io.IOException;
import javafx.application.Platform;

public class Main extends Application {
    public Connection connection = null;
    static boolean exit = false;
    public static void exit(){
        Main.exit = true;
    }
    
    public static void main(String[] args) {
        launch(args); //launch the program as a javaFX application
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Start Login window
        String players[] = loginScene();
        
        if(exit) return;
        int FIRST = Character.getNumericValue(players[1].charAt(0));
        System.out.println("FIRST "+FIRST);
        players[1] = players[1].substring(1);
        
        //verify if socket is connected
        primaryStage.setMaximized(true);
        
        primaryStage.setOnCloseRequest(e -> {
            try {
                if(connection!=null){
                    System.out.println("close");
                    connection.send("Quit");
                    connection.close_connection();
                }
            } catch (IOException ex) {
                
            }
            Platform.exit();
        });
        
        BorderPane bPane = new BorderPane();
        Pane centerPane = new Pane();
        Scene scene = new Scene(bPane);   
        centerPane.setMaxSize(0.5 * scene.getWidth(), 0.8 * scene.getHeight());
        bPane.setCenter(centerPane);
        scene.setFill(Color.rgb(240, 240, 240));
        primaryStage.setScene(scene);
        //---------------------------------------------------------------------
        Player p1 = null;
        Player p2 = null;
        if(FIRST==1){
            p1 = new Player(players[0], Player.BLACK, Player.LOCAL);
            p2 = new Player(players[1], Player.WHITE, Player.REMOTE); 
        }
        else if(FIRST==2){
            p1 = new Player(players[0], Player.WHITE, Player.LOCAL);
            p2 = new Player(players[1], Player.BLACK, Player.REMOTE); 
        }
        else{
            AlertBox.display("Error" + FIRST, "Server did not decide the first player!");
            Platform.exit();
        }
        
        Game game = new Game(p1, p2, connection, centerPane);
        game.init();
        //---------------------------------------------------------------------
        
        primaryStage.show();
    }
    
    public String[] loginScene() {
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
            Main.exit();
            if (connection!=null) try {
                connection.send("Quit");
                connection.close_connection();
            } catch (IOException ex) {}
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
                LoginControl login = new LoginControl(name, host, players, window);
                connection = login.validate();
            }
        });
        
        window.showAndWait();
        return players;
    }
}