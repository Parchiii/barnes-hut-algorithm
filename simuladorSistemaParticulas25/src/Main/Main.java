package Main;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Vista/MainWindows.fxml"));
        stage.setTitle("sistema de particulas");
        stage.setScene(new Scene (root));
        stage.show();
    }
    
    void main(){
    launch();
    }
}