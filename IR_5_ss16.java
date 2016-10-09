package ir_5_ss16;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Fabian Kopp, Maximilian Mühle
 */
public class IR_5_ss16 extends Application {

    static Dokumentensammlung sammlung = new Dokumentensammlung();

    @Override
    public void start(Stage primaryStage) throws IOException {
        File data = new File("./data/");
        File coll = new File(data, "sammlung.txt");
        if (!data.exists()) {
            data.mkdir();
        }
        if (!coll.exists()) {
            coll.createNewFile();
        }
        sammlung = new Dokumentensammlung(coll.getPath());

        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("IR Gruppe 5");
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
