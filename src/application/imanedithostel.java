package application;

import DatabaseClasses.hostelDB;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;

public class imanedithostel extends Application {
    private Object[] selectedHostel;
    private Runnable onEditComplete;
    private hostelDB hosteldb;

    public imanedithostel(Object[] selectedHostel, Runnable onEditComplete, hostelDB hosteldb) {
        this.selectedHostel = selectedHostel;
        this.onEditComplete = onEditComplete;
        this.hosteldb = hosteldb;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Hostel");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);
        // Labels
        Label nameLabel = createLabel("Name:");
        Label locationLabel = createLabel("Location:");
        Label floorsLabel = createLabel("No. of Floors:");
        Label wardenLabel = createLabel("Warden:");

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
        locationLabel.setFont(new Font(16));
        floorsLabel.setFont(new Font(16));
        wardenLabel.setFont(new Font(16));

        // Input Fields (Use TextFields to display existing data for editing)
        TextField nameTextField = createHoverTextField("Existing Name");
        TextField locationTextField = createHoverTextField("Existing Location");
        TextField floorsTextField = createHoverTextField("Existing Floors");
        TextField wardenTextField = createHoverTextField("Existing Warden");
        // Pre-fill text fields with existing data
        nameTextField.setText(getProperty(selectedHostel, 0));
        locationTextField.setText(getProperty(selectedHostel, 1));
        floorsTextField.setText(getProperty(selectedHostel, 2));
        wardenTextField.setText(getProperty(selectedHostel, 3));
        String name = nameTextField.getText();
        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        locationTextField.setMaxWidth(300);
        floorsTextField.setMaxWidth(300);
        wardenTextField.setMaxWidth(300);
     
         // Add listener to floorsTextField
            floorsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Check if the quantity is a valid integer and greater than 5000
                if (!newValue.trim().isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(newValue.trim());
                        if (quantity > 5000) {
                            showAlert("Error", "No. of Floors can not exceed 5000.");

                            // Check if the field is focused before clearing it
                            if (!floorsTextField.isFocused()) {
                            	floorsTextField.clear();
                            }
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Error", "No. of Floors should contain only non-negative numeric value e.g. 1");

                        // Check if the field is focused before clearing it
                        if (!floorsTextField.isFocused()) {
                        	floorsTextField.clear();
                        }
                    }
                }
            });

        // Button
        Button editButton = createHoverButton("Edit Hostel");
        editButton.setOnAction(event -> {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(nameTextField);
            InputValidation.trimSpaces(locationTextField);
            InputValidation.trimSpaces(floorsTextField);
            InputValidation.trimSpaces(wardenTextField);
            // Validate input using the InputValidation class
            if (!InputValidation.areFieldsFilled(nameTextField, locationTextField, floorsTextField, wardenTextField)) {
                // Display an error message for unfilled fields
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Additional validation for alphabetic hostel name
            if (!InputValidation.isValidAlphabeticText(nameTextField.getText())) {
                showAlert("Error", "Hostel name should contain only alphabetic characters.");
                return;
            }

            // Additional validation for zero or positive floors
            if (!InputValidation.isValidPositiveNumber(floorsTextField.getText())) {
            	   showAlert("Error", "No. of Floors should contain only non-negative numeric value e.g. 1.");
                
                return;
            }

            // Additional validation for alphabetic warden name
            if (!InputValidation.isValidAlphabeticText(wardenTextField.getText())) {
            	showAlert("Error", "Warden name should contain only alphabetic characters.");
                return;
            }
            // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Hostel name should not exceed 50 characters.");
                return;
            }
         // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(wardenTextField.getText(), 50)) {
                showAlert("Error", "Warden name should not exceed 50 characters.");
                return;
            }
            // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(locationTextField.getText(), 255)) {
                showAlert("Error", "Location should not exceed 255 characters.");
                return;
            }
            // Check if the quantity is greater than 5000
            if (!InputValidation.isValidNumber(Integer.parseInt(floorsTextField.getText()) , 5000)) {
                showAlert("Error", "No. of Floors can not exceed 5000.");
                return;
            }
            // Check if the edited hostel name already exists
            String editedName = nameTextField.getText();
            if (!editedName.equals(getProperty(selectedHostel, 0)) && hosteldb.isHostelExists(editedName)) {
                showAlert("Error", "A hostel with the name '" + editedName + "' already exists. Enter a unique hostel name.");
                return; // Stop further processing
            }

            // Add your logic to handle the "Edit Hostel" button click
            hosteldb.updateHostelInfo(name, nameTextField.getText(), locationTextField.getText(), Integer.parseInt(floorsTextField.getText()), wardenTextField.getText());
            onEditComplete.run(); // Notify the main class that editing is complete
            primaryStage.close(); // Close the editHostelStage
        });


        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
                locationLabel, locationTextField,
                floorsLabel, floorsTextField,
                wardenLabel, wardenTextField,
                editButton
        );
        // Add space to the left of the button using layout parameters
        VBox.setMargin(editButton, new Insets(0, 0, 0, 90));

        StackPane stackPane = new StackPane();
        stackPane.setBackground(new Background(new BackgroundFill(Color.rgb(34, 66, 122), CornerRadii.EMPTY, Insets.EMPTY)));
        stackPane.getChildren().addAll(inputLayout);

        // Scene
        Scene scene = new Scene(stackPane, 500, 500); // Increase the width
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private TextField createHoverTextField(String defaultText) {
        TextField textField = new TextField();
        textField.setPromptText(defaultText);
        textField.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: black;"); // Set text color to black
        textField.setOnMouseEntered(e -> textField.setStyle("-fx-background-color: #b3e0ff;"));
        textField.setOnMouseExited(e -> textField.setStyle("-fx-background-color: #e6f7ff;"));
        return textField;
    }

    private Button createHoverButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;"));
        return button;
    }

    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }
    // Add this method to display alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }
}
