package application;

import DatabaseClasses.CategoryDB;
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

public class EditCategory extends Application {
    private Object[] selectedcategory;
    private Runnable onEditComplete;
    private CategoryDB categorydb;

    public EditCategory(Object[] selectedcategory, Runnable onEditComplete, CategoryDB categorydb) {
        this.selectedcategory = selectedcategory;
        this.onEditComplete = onEditComplete;
        this.categorydb = categorydb;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Category");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);
        // Labels
        Label nameLabel = createLabel("Name:");
       
        // Increase font size of labels
        nameLabel.setFont(new Font(16));
      
        // Input Fields (Use TextFields to display existing data for editing)
        TextField nameTextField = createHoverTextField("Existing Category");
       
        // Pre-fill text fields with existing data
        nameTextField.setText(getProperty(selectedcategory, 1));
       
        nameTextField.getText();
        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
      

        // Button
        Button editButton = createHoverButton("Edit Category");
        editButton.setOnAction(event -> {
        	InputValidation.trimSpaces(nameTextField);
            // Validate input using the InputValidation class
            if (!InputValidation.areFieldsFilled(nameTextField)) {
                // Display an error message for unfilled fields
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Additional validation for alphabetic category name
            if (!InputValidation.isValidAlphabeticText(nameTextField.getText())) {
                showAlert("Error", "Category name should contain only alphabetic characters.");
                return;
            }

            // Check the length of the name
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Category name should not exceed 50 characters.");
                return;
            }        
            
            // Check if the edit Category name already exists
            String editedName = nameTextField.getText();
           
            // Add your logic to handle the "Edit Category" button click
            int CategoryId = Integer.parseInt(getProperty(selectedcategory, 0));
            
            // Check if the edit Category name already exists
            
            if (!editedName.equals(getProperty(selectedcategory, 0)) && categorydb.isCategoryExists(editedName)) {
                showAlert("Error", "A category with the name '" + editedName + "' already exists. Enter a unique category name.");
                return; // Stop further processing
            }
            // Add your logic to handle the "Edit Category" button click
            categorydb.updateCategoryInfo(CategoryId,nameTextField.getText());
            onEditComplete.run(); // Notify the main class that editing is complete
            primaryStage.close(); // Close the editCategoryStage
        });


        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
               
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
