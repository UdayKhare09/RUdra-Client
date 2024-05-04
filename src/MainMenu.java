import ChatApp.LoginGUI;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainMenu {

    public static void createMenu() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.minHeightProperty().set(600);
        stage.minWidthProperty().set(1000);

        VBox vBox = new VBox(15);

        // Set the Background Image
        Image image = new Image("Resources/MainMenuBack.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        vBox.setBackground(background);

        // Create a Button for each option
        MenuButton btn1 = new MenuButton("RUdra Chat");
        MenuButton btn4 = new MenuButton("Exit");

        // Set an onAction event handler for the "RUdra Chat" button to launch LoginGUI
        btn1.setOnAction(event -> {
            new LoginGUI();
            stage.close();
        });

        btn4.setOnAction(event -> {
            stage.close();
            System.exit(0);
        });

        // Add the Buttons to the VBox
        vBox.getChildren().addAll(btn1, btn4);
        vBox.alignmentProperty().set(Pos.CENTER);

        // Create a Scene and add the VBox to it
        Scene scene = new Scene(vBox, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        Rectangle clip = new Rectangle(1000, 600);
        clip.setArcWidth(30.0);
        clip.setArcHeight(30.0);
        vBox.setClip(clip);

        // Create a Stage and set the Scene to it
        stage.setScene(scene);
        stage.setTitle("Main Menu");

        // Show the Stage
        stage.show();
        stage.toFront();
    }

}

class MenuButton extends Button {
    public MenuButton(String text) {
        super(text);
        setPrefSize(250, 65.5);
        setShape(new Circle(50));
        setCursor(javafx.scene.Cursor.HAND);


        FadeTransition ft = new FadeTransition(Duration.millis(5000), this);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        ScaleTransition stEnter = new ScaleTransition(Duration.millis(200), this);
        stEnter.setToX(1.2);
        stEnter.setToY(1.2);

        ScaleTransition stExit = new ScaleTransition(Duration.millis(200), this);
        stExit.setToX(1.0);
        stExit.setToY(1.0);

        // Create a DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.RED); // Set the color of the glow
        dropShadow.setRadius(10.0); // Set the radius of the glow
        dropShadow.setSpread(0.5); // Set the spread of the glow

        setOnMouseEntered(event -> {
            stEnter.playFromStart();
            setEffect(dropShadow);
        });
        setOnMouseExited(event -> {
            stExit.playFromStart();
            setEffect(null);
        });
    }
}