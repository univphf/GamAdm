package gamadm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author maj
 */
public class GamAdm extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLGAM.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);  
        stage.setTitle("Gam Admission des patients - Tondeur Hervé - UPHF - TD");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
