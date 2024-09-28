package application;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.MalformedURLException;

import DatabaseClasses.AdminDB;

public class Login extends Application {

    private boolean showPassword = false;
    private TextField passwordAsteriskField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a StackPane with a gradient background
    	 primaryStage.setTitle("Login");
         Image icon = new Image("fi.png");

         // Set the icon image for the stage
         primaryStage.getIcons().add(icon);
        StackPane stackPane = new StackPane();
        stackPane.setBackground(new Background(new BackgroundFill(Color.rgb(34, 66, 122), CornerRadii.EMPTY, Insets.EMPTY)));

        // Add a gradient effect
        stackPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080);");

        // Sliding Bar with Welcome Message
        Text slidingBarText = new Text("Welcome to UET Inventory Management System");
        slidingBarText.setFill(Color.WHITE);
        slidingBarText.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), slidingBarText);
        translateTransition.setToY(-50); // Slide up by 50 pixels
        translateTransition.setInterpolator(Interpolator.EASE_OUT);
        stackPane.getChildren().add(slidingBarText);
        translateTransition.play();

        // Center Panel - Login Form
        GridPane loginGridPane = createLoginFormPane();
        addUIControls(loginGridPane);
        stackPane.getChildren().add(loginGridPane);

        // Create the scene
        //primaryStage.setMaximized(true);
        Scene scene = new Scene(stackPane, 600, 500);

        // Set up the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createLoginFormPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        return gridPane;
    }

    private void addUIControls(GridPane gridPane) {
        // UET logo
        Image logoImage = new Image("uet.png");
        ImageView logoImageView = new ImageView(logoImage);

        logoImageView.setFitWidth(100 + 20);
        logoImageView.setFitHeight(100 + 20);
        GridPane.setConstraints(logoImageView, 0, 1, 3, 1, HPos.CENTER, VPos.TOP);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), logoImageView);
        translateTransition.setToY(-60); // Slide up by 50 pixels
        translateTransition.play();

        gridPane.getChildren().add(logoImageView);

        Label emailLabel = new Label("Email ID:");
        emailLabel.setStyle("-fx-text-fill: white;");
        gridPane.add(emailLabel, 0, 2);

        TextField emailTextField = new TextField();
        gridPane.add(emailTextField, 1, 2, 2, 1);

        // Show/Hide Password CheckBox
        CheckBox showHideCheckBox = new CheckBox("Show Password");
        showHideCheckBox.setStyle("-fx-text-fill: white;");
        GridPane.setConstraints(showHideCheckBox, 2, 3);
        gridPane.getChildren().add(showHideCheckBox);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: white;");
        gridPane.add(passwordLabel, 0, 3);

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-text-fill: black;");
        gridPane.add(passwordField, 1, 3);

        // Asterisk display for password
        
        passwordAsteriskField.setStyle("-fx-text-fill: black;");
        passwordAsteriskField.setManaged(false);
        passwordAsteriskField.setVisible(false);
        gridPane.add(passwordAsteriskField, 1, 3);

        // Action for Show/Hide Password CheckBox
        showHideCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showPassword = newValue;
            passwordField.setVisible(!showPassword);
            passwordField.setManaged(!showPassword);
            passwordAsteriskField.setVisible(showPassword);
            passwordAsteriskField.setManaged(showPassword);

            // Set the text of the passwordAsteriskField to the actual password for displaying asterisks
            if (showPassword) {
                passwordAsteriskField.setText(passwordField.getText());
            } else {
                passwordAsteriskField.clear();
            }
        });

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        GridPane.setConstraints(loginButton, 0, 4, 3, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(loginButton);

        // New User Registration Button
        Button registerButton = new Button("New User? Register Here");
        registerButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        GridPane.setConstraints(registerButton, 0, 5, 3, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(registerButton);

        // Action for Login Button
     // Action for Login Button
        loginButton.setOnAction(event -> {
            String email = emailTextField.getText();
            String password = showPassword ? passwordAsteriskField.getText() : passwordField.getText();
            
            // Connection with internal class
            AdminDB admndb = new AdminDB();
            
            admndb.validateLogin(email, password);
            
            if (admndb.validateLogin(email, password)) {
                // Display the email on the console before opening the home page
               // System.out.println("Login successful. Email: " + email);
                
                showAlert(Alert.AlertType.INFORMATION, gridPane.getScene().getWindow(), "Login Successful", "Welcome to UET Inventory Management System!");
                try {
                    openHomePage(email, password);
                } catch (MalformedURLException | ClassNotFoundException e) {
                    // Handle exceptions
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Login Failed", "Invalid email or password. Please check your credentials.");
            }
        });


        // Action for New User Registration Button
        registerButton.setOnAction(event -> openRegistrationPage());
    }

    private void openHomePage(String email,String password) throws MalformedURLException, ClassNotFoundException {
    	// Create a new instance of the Main class
        Main main = new Main(email,password);

        // Create a new stage and set its title
        Stage mainStage = new Stage();
        mainStage.setTitle("UET Inventory Management System - Home");

        // Call the start method of the Main class to display the home page
        main.start(mainStage);

        // Close the current login stage (optional)
        ((Stage) passwordAsteriskField.getScene().getWindow()).close();
    }

    // You can create a new stage and scene for the Registration page here
    private void openRegistrationPage() {
        // Create a new instance of the SignUp class
        SignUp signUp = new SignUp();

        // Call the start method to display the registration page
        signUp.start(new Stage());

        // Close the current login stage (optional)
        ((Stage) passwordAsteriskField.getScene().getWindow()).close();
    }
 
    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}