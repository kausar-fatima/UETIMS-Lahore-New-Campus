package application;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import validation.InputValidation;


import java.net.InetAddress;
import java.util.List;

import DatabaseClasses.AdminDB;
import Internalclasses.Admin;

public class SignUp extends Application {

    private boolean showPassword = false;
    private TextField emailField, nameField;
    private PasswordField passwordField, confirmPasswordField;
    private TextField passwordAsteriskField;
    private TextField confirmPasswordAsteriskField;
    public AdminDB admndb = new AdminDB();
    private Verification verification; // Moved to class level

    private GridPane gridPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Sign-Up");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        verification = new Verification(this); // Create one instance of Verification
        VBox vbox = new VBox(20);
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(34, 66, 122), CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080);");

        Text createAccountLabel = new Text("Create New Account Here");
        createAccountLabel.setFill(Color.WHITE);
        createAccountLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        GridPane signupGridPane = createSignupFormPane();
        addUIControls(signupGridPane);
        vbox.getChildren().addAll(createAccountLabel, signupGridPane);

        vbox.setAlignment(Pos.CENTER);
        //primaryStage.setMaximized(true);
        Scene scene = new Scene(vbox, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createSignupFormPane() {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        gridPane.setStyle("-fx-border-color: #3498DB; -fx-border-width: 0px; -fx-border-radius: 5px;");

        return gridPane;
    }

    private void addUIControls(GridPane gridPane) {
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: white;");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gridPane.add(emailLabel, 0, 2);

        HBox emailBox = new HBox(5);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        //ImageView emailIcon = new ImageView(new Image("file:C:/Users/PMLS/Downloads/logoUET.png"));
        //emailIcon.setFitHeight(20);
        //emailIcon.setFitWidth(20);
        emailBox.getChildren().addAll(emailField);

        gridPane.add(emailBox, 1, 2, 2, 1);
        Label emailValidationIcon = new Label("");
        emailValidationIcon.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        emailValidationIcon.setTextFill(Color.RED);
        HBox emailIndicatorBox = new HBox(emailValidationIcon);
        gridPane.add(emailIndicatorBox, 3, 2);

        // Action for Email Field
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
        	 if (InputValidation.isValidEmail(newValue)) {
        	        if (newValue.length() > 50) {
        	            emailValidationIcon.setText("✖");
        	            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Email", "Email cannot exceed 50 characters.");
        	        } else {
        	            emailValidationIcon.setText("✔");
        	        }
        	    } else {
        	        emailValidationIcon.setText("✖");
        	    }
        });

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: white;");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gridPane.add(passwordLabel, 0, 3);

        HBox passwordBox = new HBox(5);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        //ImageView passwordIcon = new ImageView(new Image("file:C:/Users/PMLS/Downloads/logoUET.png"));
        //passwordIcon.setFitHeight(20);
        //passwordIcon.setFitWidth(20);
        passwordBox.getChildren().addAll(passwordField);

        gridPane.add(passwordBox, 1, 3);

        CheckBox showHideCheckBox = new CheckBox("Show Password");
        showHideCheckBox.setStyle("-fx-text-fill: white;");
        showHideCheckBox.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        GridPane.setConstraints(showHideCheckBox, 2, 3);
        gridPane.getChildren().add(showHideCheckBox);

        passwordAsteriskField = new TextField();
        passwordAsteriskField.setStyle("-fx-text-fill: black;");
        passwordAsteriskField.setManaged(false);
        passwordAsteriskField.setVisible(false);
        gridPane.add(passwordAsteriskField, 1, 3);

        

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-text-fill: white;");
        confirmPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gridPane.add(confirmPasswordLabel, 0, 4);

        HBox confirmPasswordBox = new HBox(5);
        confirmPasswordBox.setAlignment(Pos.CENTER_LEFT);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        //ImageView confirmPasswordIcon = new ImageView(new Image("file:C:/Users/PMLS/Downloads/logoUET.png"));
        //confirmPasswordIcon.setFitHeight(20);
        //confirmPasswordIcon.setFitWidth(20);
        confirmPasswordBox.getChildren().addAll(confirmPasswordField);

        gridPane.add(confirmPasswordBox, 1, 4);
        
        confirmPasswordAsteriskField = new TextField();
        confirmPasswordAsteriskField.setStyle("-fx-text-fill: black;");
        confirmPasswordAsteriskField.setManaged(false);
        confirmPasswordAsteriskField.setVisible(false);
        gridPane.add(confirmPasswordAsteriskField, 1, 4);
        
        showHideCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
        	showPassword = newValue;

        	// Show or hide the password field based on checkbox state
            passwordField.setVisible(!showPassword);
            passwordField.setManaged(!showPassword);
            passwordAsteriskField.setVisible(showPassword);
            passwordAsteriskField.setManaged(showPassword);

            // Show or hide the confirm password field based on checkbox state
            confirmPasswordField.setVisible(!showPassword);
            confirmPasswordField.setManaged(!showPassword);
            confirmPasswordAsteriskField.setVisible(showPassword);
            confirmPasswordAsteriskField.setManaged(showPassword);

            if (showPassword) {
                passwordAsteriskField.setText(passwordField.getText());
                confirmPasswordAsteriskField.setText(confirmPasswordField.getText());
            } else {
                passwordAsteriskField.clear();
                confirmPasswordAsteriskField.clear();
            }
        });

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-text-fill: white;");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gridPane.add(nameLabel, 0, 5);

        HBox nameBox = new HBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        nameField = new TextField();
        nameField.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(nameField, 1, 5, 2, 1);

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-border-radius: 5px;");
        registerButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        GridPane.setConstraints(registerButton, 0, 6, 3, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(registerButton);

        Button loginButton = new Button("Already Registered? Login Here");
        loginButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-border-radius: 5px;");
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        GridPane.setConstraints(loginButton, 0, 7, 3, 1, HPos.CENTER, VPos.CENTER);
        gridPane.getChildren().add(loginButton);

        registerButton.setOnAction(event -> registerAdmin(verification));

        loginButton.setOnAction(event -> openLoginPage());
    }

    private void registerAdmin(Verification verification) {
        String email = emailField.getText();
        String password = showPassword ? passwordAsteriskField.getText() : passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText();
        
        Admin signup = new Admin();

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Password mismatch", "Please make sure the passwords match.");
            return;
        }

        if (admndb.isEmailAlreadyExists(email)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Email exists", "An account already exists with this email. Please use a different email.");
            return;
        }

        if (!InputValidation.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Email", "Please enter a valid email address with @gmail.com.");
            return;
        }

        if (!InputValidation.isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Password", "Password must be 8 characters long and contain at least one special character.");
            return;
        }
        if (!InputValidation.isValidAlphabeticText(name)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Name","Please enter a valid name containing only alphabets.");
            return;
        }
     // Use the isValidStringLength method to check the length 
        if (!InputValidation.isValidStringLength(name, 50)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Length", "Name should not exceed 50 characters.");
            return;
        }
        // Use the isValidStringLength method to check the length 
        if (!InputValidation.isValidStringLength(email, 50)) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Invalid Length", "Email should not exceed 50 characters.");
            return;
        }
        if (!isInternetReachable()) {
            showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "No Internet Connection", "Please check your internet connection and try again.");
            return;
        }

        signup.setEmail(email);
        signup.setName(name);
        signup.setPassword(password);
        verification.setVerificationCode();
        signup.setVerificationCode(verification.getVerificationCode());
        signup.setStatus(false);
        
        List<String> entries = signup.SignUp();

        admndb.addAdminInfo(entries, this, verification);
    }
    
    private void closeCurrentStage() {
        Stage currentStage = (Stage) passwordAsteriskField.getScene().getWindow();
        currentStage.close();
    }


    private void openLoginPage() {
    	 // Close the current stage
        closeCurrentStage();

        // Call the start method to display the registration page
        new Login().start(new Stage());
    }

    public void openVerificationPage(String email) {
        
        // Close the current stage
        closeCurrentStage();
        
        //verification.setUserEmail(email);
        verification.start(new Stage());
    }

    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
    private boolean isInternetReachable() {
        try {
            // Try to reach Google's DNS server
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(1000); // Timeout in milliseconds
        } catch (Exception e) {
            return false;
        }
    }
}