package application;

import java.util.Optional;
import javax.swing.JOptionPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle; // Import Rectangle class
import javafx.stage.Stage;
import DatabaseClasses.AdminDB;
import validation.InputValidation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox; // Import HBox for horizontal layout


public class SettingsPane {
    private ImageView profileImageView;
    private Image defaultProfileImage;
    private VBox outerVBox;
    private AdminDB adminDB; // Add this field to hold an instance of AdminDB

    private TextField editUsernameTextField;
    private PasswordField passwordField;

    public SettingsPane(String email, String password) {
        adminDB = new AdminDB(); // Initialize the AdminDB instance
        outerVBox = createSettingsPane(email, password);
    }

    public VBox getSettingsPane() {
        return outerVBox;
    }

    private VBox createSettingsPane(String email, String password) {
        // Outer VBox to center the content
        outerVBox = new VBox();
        outerVBox.setStyle("-fx-background-color: #f4f4f4;"); // Background color
        outerVBox.setAlignment(Pos.CENTER);
       outerVBox.setPadding(new Insets(20));

        // Inner VBox with the settings content
        VBox innerVBox = new VBox(7); // Vertical spacing
        innerVBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20; -fx-border-radius: 10px; -fx-border-color: #cccccc; -fx-border-width: 1px;"); // Styling
        innerVBox.setAlignment(Pos.CENTER);
        innerVBox.setMaxWidth(700);

        profileImageView = new ImageView();
        defaultProfileImage = new Image("mmm.png"); // Default profile image
        profileImageView.setImage(defaultProfileImage);
        profileImageView.setFitWidth(150); // Size
        profileImageView.setFitHeight(150);
        
        Rectangle clip = new Rectangle(150, 150); // Create a rectangle with the desired width and height
        clip.setArcWidth(75); // Set arc width to make it appear circular
        clip.setArcHeight(75); // Set arc height to make it appear circular
        profileImageView.setClip(clip); // Apply the rectangle as a clipping mask to the image
        
        // Label for user profile
        Label userProfileLabel = new Label("User Profile");
        userProfileLabel.setStyle("-fx-text-fill: black; -fx-font-size: 30;-fx-font-weight: bold;"); 
        // Styling
        Image emailIconImage = new Image("Ee.png"); // Load your email icon image
        ImageView emailIconImageView = new ImageView(emailIconImage);
        emailIconImageView.setFitWidth(40); // Set the width of the icon
        emailIconImageView.setFitHeight(35); // Set the height of the icon
        
        
        Image userIconImage = new Image("nam.png"); // Load your username icon image
        ImageView userIconImageView = new ImageView(userIconImage);
        userIconImageView.setFitWidth(40); // Set the width of the icon
        userIconImageView.setFitHeight(35); // Set the height of the icon
        
        String userEmailValue = email;
        String usernameValue = adminDB.getUsernameByEmail(email);
        
       Label usernameValueLabel = new Label(usernameValue); 
        usernameValueLabel.setStyle("-fx-text-fill: #336699; -fx-font-size: 20;-fx-font-weight: bold;"); // Styling
        
        Label emailValueLabel = new Label(userEmailValue);
        emailValueLabel.setStyle("-fx-text-fill: #336699; -fx-font-size: 20;-fx-font-weight: bold;"); // Styling
        
        HBox emailHBox = new HBox(5); // Create an HBox to hold the email icon and label
        emailHBox.getChildren().addAll(emailIconImageView, emailValueLabel); // Add icon, label, and value
        emailHBox.setAlignment(Pos.CENTER); // Align content to the left
        
        HBox usernameHBox = new HBox(5); // Create an HBox to hold the username icon and label
        usernameHBox.getChildren().addAll(userIconImageView, usernameValueLabel); // Add icon, label, and value
        usernameHBox.setAlignment(Pos.CENTER); // Align content to the left
        
        editUsernameTextField = new TextField();
        editUsernameTextField.setPromptText("Enter new username");
        editUsernameTextField.setVisible(false); // Initially hide the text field
        editUsernameTextField.setMaxWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setVisible(false); // Initially hide the password field
        passwordField.setMaxWidth(250);
        
        Button deleteAccountButton = new Button("Confirm Deletion");
        deleteAccountButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5em;");
        deleteAccountButton.setVisible(false);
        deleteAccountButton.setOnAction(event -> {
            String enteredPassword = passwordField.getText();
            String storedPassword = adminDB.getPasswordByEmail(email);
            if (enteredPassword.isEmpty()) {
                showAlert("Error", "Please enter your password.");
                return;
            }
            if (!enteredPassword.equals(storedPassword)) {
                showAlert("Error", "Incorrect password.");
                return;
            }
            boolean confirmed = showConfirmationAlert();
            if (confirmed) {
                adminDB.deleteAccountByEmail(email);
                Stage currentStage = (Stage) deleteAccountButton.getScene().getWindow();
                currentStage.close();
            }
        });

        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5em;");
        saveChangesButton.setVisible(false); // Initially hide the button
        saveChangesButton.setOnAction(event -> {
            String newUsername = editUsernameTextField.getText();
            if (!InputValidation.areFieldsFilled(editUsernameTextField)) {
                showAlert("Error", "Please fill in the username field.");
                return;
            }
            adminDB.updateUsernameByEmail(email, newUsername);
            JOptionPane.showMessageDialog(null, "Username updated successfully.");
            usernameValueLabel.setText(newUsername);
            editUsernameTextField.setVisible(false);
            saveChangesButton.setVisible(false);
        });

        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5em;");
        editProfileButton.setOnAction(event -> {
            editUsernameTextField.setVisible(true);
            saveChangesButton.setVisible(true);
            passwordField.setVisible(false);
            deleteAccountButton.setVisible(false);
        });

        Button deleteButton = new Button("Delete Account");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16; -fx-background-radius: 5em;");
        deleteButton.setOnAction(event -> {
            editUsernameTextField.setVisible(false);
            saveChangesButton.setVisible(false);
            passwordField.setVisible(true);
            deleteAccountButton.setVisible(true);
        });

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.getChildren().addAll(editProfileButton, deleteButton);

        //innerVBox.getChildren().addAll(profileImageView, userProfileLabel, usernameValueLabel, emailValueLabel, editUsernameTextField, passwordField, saveChangesButton, deleteAccountButton, buttonsHBox);
        innerVBox.getChildren().addAll(profileImageView, userProfileLabel, emailHBox, usernameHBox, editUsernameTextField, passwordField, saveChangesButton, deleteAccountButton, buttonsHBox);

        outerVBox.getChildren().add(innerVBox);

        return outerVBox;
    }

    private boolean showConfirmationAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your account? This action is irreversible.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonInAlert = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButtonInAlert);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

    void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }

    // Method to reset the settings pane to its initial state
    public void resetSettingsPane() {
        editUsernameTextField.setVisible(false);
        passwordField.setVisible(false);
    }
}
