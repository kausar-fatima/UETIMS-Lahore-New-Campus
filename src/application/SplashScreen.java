package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

//splash screen
public class SplashScreen extends Application {

    private ProgressBar progressBar;
    private Label progressLabel;
    private int progress = 0;
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Splash Screen
        showSplashScreen(primaryStage);
    }

    private void showSplashScreen(Stage primaryStage) {
        primaryStage.setTitle("UET Inventory Management System");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        // Create a StackPane with a blue background
        StackPane stackPane = new StackPane();
        stackPane.setBackground(new Background(new BackgroundFill(Color.rgb(34, 66, 122), CornerRadii.EMPTY, Insets.EMPTY)));

        // Add a gradient effect
        stackPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080);");

        // Create a GridPane to hold components
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);

        // UET logo image - replace "uet-logo.png" with your logo file path
        Image logoImage = new Image("uet.png");
        ImageView logoImageView = new ImageView(logoImage);
     // Set the size of the ImageView
        logoImageView.setFitWidth(140); // Set the desired width
        logoImageView.setFitHeight(140); // Set the desired height
        GridPane.setConstraints(logoImageView, 1, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(logoImageView);

        // Text component for "UET Inventory Management System"
        Label textLabel = new Label("UET INVENTORY MANAGEMENT SYSTEM");
        textLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");
        GridPane.setConstraints(textLabel, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(textLabel);

        // Progress bar for loading simulation
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400); // Increased width
        progressBar.setStyle("-fx-accent: #FFD700;"); // Yellow color
        GridPane.setConstraints(progressBar, 0, 2, 2, 1);
        gridPane.getChildren().add(progressBar);

        // Label to display the percentage text
        progressLabel = new Label("0%");
        progressLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");
        GridPane.setConstraints(progressLabel, 1, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(progressLabel);

        stackPane.getChildren().add(gridPane);

        // Create the scene
       // primaryStage.setMaximized(true);
        Scene scene = new Scene(stackPane, 600, 500); // Increased window size

        // Set up the stage
        primaryStage.setScene(scene);
        primaryStage.show();

        // Simulate loading progress using a Timeline
        timeline = new Timeline(
                new KeyFrame(Duration.millis(50), event -> {
                    progressBar.setProgress(progress / 100.0);
                    progressLabel.setText(progress + "%");
                    progress++;
                    if (progress > 100) {
                        timeline.stop();
                        primaryStage.close();
                        openLoginPage(); // Open the login page
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void openLoginPage() {
    	Login login = new Login();
        Stage loginStage = new Stage();
        login.start(loginStage);
    }
}