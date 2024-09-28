package application;

import DatabaseClasses.CategoryDB;
import DatabaseClasses.allocatesdItemsDB;
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

public class EditAllocatedItem extends Application {

    private Runnable onCloseCallback;
    private ObservableList<Object[]> selectedItems; 
    private allocatesdItemsDB itemdb;
    private String hostelname;
    private int roomId;
    public EditAllocatedItem(String hostelname,ObservableList<Object[]> selectedItems, Runnable onCloseCallback, allocatesdItemsDB itemdb,int roomId) {
    	this.hostelname = hostelname;
    	this.selectedItems = selectedItems; 
        this.onCloseCallback = onCloseCallback;
        this.itemdb = itemdb;
        this.roomId = roomId;
        
    }
   
	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Allocated Item");
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
        
        if(selectedItems.size() == 1) {
        	// Pre-fill text fields with existing data
            String existingName = getProperty(selectedItems.get(0), 1);
            String existingCategory = getProperty(selectedItems.get(0), 2);

            nameTextField.setText(existingName);
            categoryComboBox.getSelectionModel().select(existingCategory);
        }
        

        // Button
        Button editButton = createHoverButton("Edit Item");
        editButton.setOnAction(event -> {
        	// Trim spaces from the input fields
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
            itemdb.updateAllocatedItemInfo(hostelname, selectedItems, nameTextField.getText(), categoryComboBox.getValue(), roomId);

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
