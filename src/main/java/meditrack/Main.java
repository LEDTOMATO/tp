package meditrack;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import meditrack.model.MediTrack;
import meditrack.model.ReadOnlyMediTrack;
import meditrack.storage.StorageManager;
import meditrack.ui.LoginScreen;
import meditrack.ui.MainAppScreen;

/**
 * JavaFX entry point: wires login and the main window.
 */
public class Main extends Application {

    private Stage primaryStage;
    private final StorageManager storageManager = new StorageManager();

    /** Loaded once per session from disk (or empty if no save yet). */
    private MediTrack mediTrack;

    /** Starts the app: load data, then go straight to login. */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MediTrack");
        primaryStage.setWidth(900);
        primaryStage.setHeight(620);

        Optional<ReadOnlyMediTrack> loaded = storageManager.readMediTrackData();
        mediTrack = loaded.isPresent()
                ? (MediTrack) loaded.get()
                : new MediTrack();

        // Boot straight into the Login Screen!
        showLoginScreen();

        primaryStage.show();
    }

    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this::showMainAppScreen);
        primaryStage.setScene(new Scene(loginScreen));
    }

    private void showMainAppScreen() {
        MainAppScreen mainApp = new MainAppScreen(mediTrack, storageManager, this::showLoginScreen);
        primaryStage.setScene(new Scene(mainApp, 900, 620));
    }

    /**
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}