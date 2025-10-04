import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.input.MouseButton;

public class MineTile extends Button {
    int r, c;
    MinesweeperGame game;

    public MineTile(int r, int c, MinesweeperGame game) {
        this.r = r;
        this.c = c;
        this.game = game;

        setPrefSize(50, 50);
        setFont(Font.font(18));

        setOnMouseClicked(e -> {
            if (game.gameOver) return;

            if (e.getButton() == MouseButton.PRIMARY) { // Left click
                if (game.mineList.contains(this)) game.revealMines();
                else game.checkMine(r, c);
            } else if (e.getButton() == MouseButton.SECONDARY) { // Right click
                setText(getText().equals("🚩") ? "" : "🚩");
            }
        });
    }
}
