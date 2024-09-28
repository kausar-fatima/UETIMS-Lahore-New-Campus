package application;

import java.sql.Connection;
import java.sql.SQLException;

import DatabaseClasses.CategoryDB;
import DatabaseClasses.DatabaseConnector;
import DatabaseClasses.InventoryItemsDB;
import javafx.application.Application;
import javafx.collections.ObservableList;
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

public class EditInventoryItem extends Application {
	private static DatabaseConnector databaseConnector = new DatabaseConnector();
    private Runnable onCloseCallback;
    private ObservableList<Object[]> selectedItems; 
    public EditInventoryItem(ObservableList<Object[]> selectedItems, Runnable onCloseCallback, InventoryItemsDB inventorydb) {
        this.onCloseCallback = onCloseCallback;
        this.selectedItems = selectedItems;
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
    	  primaryStage.setTitle("Edit Inventory Item");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);
        // Labels
        Label nameLabel = createLabel("Item Name:");
        Label categoryLabel = createLabel("Item Category:");

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
        categoryLabel.setFont(new Font(16));
       

        // Input Fields
        TextField nameTextField = createHoverTextField();
        ComboBox<String> categoryComboBox = createComboBox();
    

        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        categoryComboBox.setMaxWidth(300);
    

        // Retrieving data from the database
        categoryComboBox.setItems(new CategoryDB().getItemCategories());

        // Retrieve existing item data based on the itemId
        // Assuming the existence of a suitable method in InventoryItemsDB
        InventoryItemsDB inventoryItemsDB = new InventoryItemsDB();
        

        // Pre-fill text fields with existing data

        if(selectedItems.size()==1) {
        	nameTextField.setText(getProperty(selectedItems.get(0),1));
        	categoryComboBox.setValue(getProperty(selectedItems.get(0), 2));
        }   

        Connection connection = databaseConnector.connect();

        // Button
        Button editButton = createHoverButton("Edit Item");
        editButton.setOnAction(event -> {
        	InputValidation.trimSpaces(nameTextField);
            // Validate input fields
            if (!InputValidation.areFieldsFilled(nameTextField, categoryComboBox )) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Validate item name
            String itemName = nameTextField.getText().trim();
            if (!InputValidation.isValidAlphabeticText(itemName)) {
                showAlert("Error", "Item name should contain only alphabetic characters.");
                return;
            }

            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Item name should not exceed 50 characters.");
                return;
            }
            
            // Add your logic to handle the "Edit Item" button click
            // Assuming the existence of a suitable InventoryItemsDB class
            inventoryItemsDB.updateItemInfo(selectedItems, nameTextField.getText(), categoryComboBox.getValue());

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

    private TextField createHoverTextField() {
        TextField textField = new TextField();
        textField.setPromptText("Enter here");
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

    private ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select");
        comboBox.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        comboBox.setOnMouseEntered(e -> comboBox.setStyle("-fx-background-color: #b3e0ff;"));
        comboBox.setOnMouseExited(e -> comboBox.setStyle("-fx-background-color: #e6f7ff;"));
        return comboBox;
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
