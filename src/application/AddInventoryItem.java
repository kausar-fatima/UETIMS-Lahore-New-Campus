package application;

import DatabaseClasses.CategoryDB;
import DatabaseClasses.InventoryItemsDB;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;

public class AddInventoryItem extends Application {
    private Runnable onCloseCallback;

    public AddInventoryItem(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Add Inventory Item");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);
        // Labels
        Label nameLabel = createLabel("Item Name:");
        Label categoryLabel = createLabel("Item Category:"); // Moved up
        Label itemQuantityLabel = createLabel("Quantity:");

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
        categoryLabel.setFont(new Font(16));
        itemQuantityLabel.setFont(new Font(16));

        // Input Fields
        TextField nameTextField = createHoverTextField();
        ComboBox<String> categoryComboBox = createComboBox(); // Placeholder, replace with your data
        TextField itemQuantityTextField = createHoverTextField();

        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        categoryComboBox.setMaxWidth(300);
        itemQuantityTextField.setMaxWidth(300);

        // Retrieving data from the database
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setDisable(false);
        categoryComboBox.setItems(new CategoryDB().getItemCategories());
        itemQuantityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the quantity is a valid integer and greater than 5000
            if (!newValue.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(newValue.trim());
                    if (quantity > 5000) {
                        showAlert("Error", "You can add only 5000 items at a time.");

                        // Check if the field is focused before clearing it
                        if (!itemQuantityTextField.isFocused()) {
                            itemQuantityTextField.clear();
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Quantity should contain only non-negative numeric value e.g. 1");

                    // Check if the field is focused before clearing it
                    if (!itemQuantityTextField.isFocused()) {
                        itemQuantityTextField.clear();
                    }
                }
            }
        });


        // Button
        Button addButton = createHoverButton("Add Item");
        addButton.setOnAction(event -> {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(nameTextField);
            InputValidation.trimSpaces(itemQuantityTextField);
            
            // Validate input fields
            if (!InputValidation.areFieldsFilled(nameTextField, categoryComboBox, itemQuantityTextField)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Validate item name
            String itemName = nameTextField.getText().trim();
            if (!InputValidation.isValidAlphabeticText(itemName)) {
                showAlert("Error", "Item name should contain only alphabetic characters.");
                return;
            }

            // Validate item quantity
            // Validate item quantity
            String itemQuantityText = itemQuantityTextField.getText().trim();
            if (!InputValidation.isValidPositiveInteger(itemQuantityText)) {
            	 showAlert("Error", "Quantity should contain only non-negative numeric value e.g. 1");
                return;
            }
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Item name should not exceed 50 characters.");
                return;
            }
            // Check if the quantity is greater than 5000
            if (!InputValidation.isValidNumber(Integer.parseInt(itemQuantityText) , 5000)) {
                showAlert("Error", "You can add only 5000 items at a time.");
                return;
            }
            
            // Add your logic to handle the "Add Item" button click
            // Assuming the existence of a suitable InventoryItemsDB class
            InventoryItemsDB addItem = new InventoryItemsDB();
            addItem.addInventoryItem(null, null, itemName, categoryComboBox.getValue(), Integer.parseInt(itemQuantityText));

            // Run the onCloseCallback to refresh the InventoryView table
            onCloseCallback.run();
            primaryStage.close();
        });

        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
                categoryLabel, categoryComboBox,
                itemQuantityLabel, itemQuantityTextField,
                addButton
        );
        // Add space to the left of the button using layout parameters
        VBox.setMargin(addButton, new Insets(0, 0, 0, 90));

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

    private TextField createHoverTextField() {
        TextField textField = new TextField();
        textField.setPromptText("Enter here");
        textField.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
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

    private ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select");
        comboBox.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        comboBox.setOnMouseEntered(e -> comboBox.setStyle("-fx-background-color: #b3e0ff;"));
        comboBox.setOnMouseExited(e -> comboBox.setStyle("-fx-background-color: #e6f7ff;"));
        return comboBox;
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
