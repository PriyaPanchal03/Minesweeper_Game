import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MinesweeperFX extends Application {

    private MinesweeperGame game;

    private BorderPane root = new BorderPane();
    private Text titleText = new Text("💣 Minesweeper 💣");
    private Text timerText = new Text("⏱ Time: 0s");

    @Override
    public void start(Stage stage) {
        try {
            game = new MinesweeperGame(8, 8, 10, root, titleText, timerText);
            game.setupUI();

            Scene scene = new Scene(root, 450, 500);
            stage.setScene(scene);
            stage.setTitle("Minesweeper JavaFX Project");
            stage.show();

            game.startTimer();
        } catch (Exception e) {
            game.showError("Error initializing game: " + e.getMessage());
        }
    }
}
