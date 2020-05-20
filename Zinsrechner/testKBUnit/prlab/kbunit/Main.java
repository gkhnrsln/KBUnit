package prlab.kbunit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prlab.kbunit.gui.windowMainFrame.MainFrameController;

import java.io.IOException;


/**
 * Start von KBUnit Entwickler<br>
 *  
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * 
 * @author Patrick Pete
 *
 */

public class Main extends Application {

	private Stage primaryStage;

	public static void main(String[] args){
		launch(args);
	}
	
	/**
	 * Start der JavaFX Anwendung
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("KBUnit-Entwickler");
		this.primaryStage.getIcons().add(new Image(getClass()
			.getResourceAsStream("/prlab/kbunit/resources/images/kbunit.png")));
		showMainScene();
	}

	public void showMainScene() {
		try {
			// Laden der Hauptszene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(
				"/prlab/kbunit/resources/view/MainFrameScene.fxml"));
			Parent root =  loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(
				"/prlab/kbunit/resources/css/MainFrameScene.css").toExternalForm());
			primaryStage.setScene(scene);
			MainFrameController controller = loader.getController();
		    controller.setHostServices(getHostServices());
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Laden der Oberflächen-Resourcen.");
		}
	}
}