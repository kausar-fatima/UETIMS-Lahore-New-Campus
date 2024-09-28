package application;

import java.time.LocalDate;

import DatabaseClasses.CategoryDB;
import DatabaseClasses.RoomDB;
import DatabaseClasses.hostelDB;
import DatabaseClasses.requestDB;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;
import javafx.scene.control.DatePicker;

public class AddRequest extends Application {
    
	private Runnable onCloseCallBack;
    public AddRequest(Runnable onCloseCallBack) {
		this.onCloseCallBack = onCloseCallBack;
	}

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Add Requisition");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);

        // Labels
        
        Label hostelLabel = createLabel("Hostel Name:");
        Label roomLabel = createLabel("Room No.:");
        Label categoryLabel = createLabel("Item Category:"); // Moved up
        Label nameLabel = createLabel("Item Name:");
        Label quantityLabel = createLabel("Quantity:");
        Label descriptionLabel = createLabel("Description:");
        Label statusLabel = createLabel("Status:"); // Added
        Label dateLabel = createLabel("Date:");

        // Date Picker
        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false); // Make the DatePicker non-editable

        // ComboBoxes
        ComboBox<String> hostelComboBox = createComboBox();
        ComboBox<String> roomComboBox = createComboBox1();
        ComboBox<String> categoryComboBox = createComboBox(); // Placeholder, replace with your data
        ComboBox<String> statusComboBox = createComboBoxStatus(); // Added

        // Description TextArea
        TextArea descriptionTextArea = createHoverTextArea();
        descriptionTextArea.setWrapText(true); // Enable text wrapping
        descriptionTextArea.setPrefRowCount(2); // Set the preferred number of rows

        // Placeholders
        TextField nameTextField = createHoverTextField();
        TextField quantityTextField = createHoverTextField();
        
        // Increase font size of labels
        dateLabel.setFont(new Font(16));
        hostelLabel.setFont(new Font(16));
        roomLabel.setFont(new Font(16));
        categoryLabel.setFont(new Font(16));
        nameLabel.setFont(new Font(16));
        quantityLabel.setFont(new Font(16));
        statusLabel.setFont(new Font(16)); // Added
        descriptionLabel.setFont(new Font(16)); // Added

        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        quantityTextField.setMaxWidth(300);
        descriptionTextArea.setMaxWidth(300);
        
        // Set fixed width for ComboBoxes
        hostelComboBox.setMaxWidth(300);
        roomComboBox.setMaxWidth(300);
        categoryComboBox.setMaxWidth(300);
        statusComboBox.setMaxWidth(300);
        datePicker.getEditor().setStyle("-fx-pref-width: 276;");
        
        hostelComboBox.setPromptText("Select Hostel ");
        hostelComboBox.setDisable(false);
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setDisable(false);
        statusComboBox.setPromptText("Select Status");
        statusComboBox.setDisable(false);
        
        //retrieving data from db
        hostelComboBox.setItems(new hostelDB().getHostelNames());
        roomComboBox.setItems(new RoomDB().getRoomNumbers(hostelComboBox.getValue())); 
        categoryComboBox.setItems(new CategoryDB().getItemCategories());
        quantityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the quantity is a valid integer and greater than 5000
            if (!newValue.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(newValue.trim());
                    if (quantity > 5000) {
                        showAlert("Error", "You can add only 5000 items at a time.");

                        // Check if the field is focused before clearing it
                        if (!quantityTextField.isFocused()) {
                            quantityTextField.clear();
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Quantity should contain only non-negative numeric value e.g. 1");

                    // Check if the field is focused before clearing it
                    if (!quantityTextField.isFocused()) {
                        quantityTextField.clear();
                    }
                }
            }
        });
     // Update the roomComboBox when a different hostel is selected
        hostelComboBox.setOnAction(event -> {
            String selectedHostel = hostelComboBox.getValue();

            if (selectedHostel == null || selectedHostel.isEmpty()) {
                // If no hostel is selected, update the prompt text
                roomComboBox.setPromptText("Select Hostel First");
                roomComboBox.setDisable(true);
            } else {
                // If a hostel is selected, update the prompt text and enable the roomComboBox
                roomComboBox.setPromptText("Select Room");
                roomComboBox.setDisable(false);
                roomComboBox.setItems(new RoomDB().getRoomNumbers(selectedHostel));
            }
        });
        // Add an event handler to the DatePicker to restrict future dates
        datePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalDate selectedDate = datePicker.getValue();

                // Check if selectedDate is not null
                if (selectedDate != null) {
                    LocalDate currentDate = LocalDate.now();

                    if (selectedDate.isAfter(currentDate)) {
                        // Show alert for future date
                        showAlert("Error", "Selected date cannot be in the future.");

                        // Remove the selected date
                        datePicker.setValue(null);
                    }
                }
            }
        });
    
    // Button
        Button addButton = createHoverButton("Add Requisition");
     // Inside addButton.setOnAction
        addButton.setOnAction(event -> {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(nameTextField);
            InputValidation.trimSpaces(quantityTextField);
            InputValidation.trimSpaces(descriptionTextArea);
           // InputValidation.trimSpaces(datePicker);
            // Initialize quantity here
            Integer quantity = 0;

            // Retrieve data from the GUI components
            String selectedDate = datePicker.getValue() == null ? "" : datePicker.getValue().toString();
            String selectedHostel = hostelComboBox.getValue();
            String selectedRoom = roomComboBox.getValue();
            String selectedCategory = categoryComboBox.getValue();
            String itemName = nameTextField.getText();
            String quantityText = quantityTextField.getText();
            String descriptionText = descriptionTextArea.getText();
           

         // Check if any field is unfilled
            if (!InputValidation.areFieldsFilled(datePicker, hostelComboBox, roomComboBox, categoryComboBox, nameTextField, quantityTextField, descriptionTextArea,statusComboBox)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (!InputValidation.isValidPositiveInteger(quantityText)) {
            	 showAlert("Error", "Quantity should contain only non-negative numeric value e.g. 1");
                return;
            }

            if (!InputValidation.isValidAlphabeticText(itemName)) {
                showAlert("Error", " Item name should contain only alphabetic characters.");
                return;
            }
            

            // Check if the quantity is greater than 5000
            if (!InputValidation.isValidNumber(Integer.parseInt(quantityTextField.getText()) , 5000)) {
                showAlert("Error", "You can add only 5000 items at a time.");
                return;
            }
            
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Item name should not exceed 50 characters.");
                return;
            }
            if (!InputValidation.isValidStringLength(descriptionTextArea.getText(), 255)) {
                showAlert("Error", "Description should not exceed 255 characters.");
                return;
            }
            String selectedStatus = statusComboBox.getValue();

            // Add the request to the database
            try {
                // Create an instance of your requestDB class
                requestDB requestDatabase = new requestDB();

                // Convert the selected date to a java.sql.Date object
                java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

                // Assign the validated quantity value
                quantity = Integer.parseInt(quantityText);
                // Add the request to the database
                requestDatabase.addRequestInfo(selectedHostel, selectedRoom, itemName, selectedCategory, quantity,descriptionText, sqlDate, selectedStatus);
                // Display a success message
                onCloseCallBack.run();
                primaryStage.close();
            } catch (Exception e) {
                // Handle exceptions, e.g., display an error alert
                showAlert("Error", "Error adding requisition: " + e.getMessage());
                e.printStackTrace();
            }
        });



        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                dateLabel, datePicker,
                hostelLabel, hostelComboBox,
                roomLabel, roomComboBox,
                categoryLabel, categoryComboBox,
                nameLabel, nameTextField,
                quantityLabel, quantityTextField,
                descriptionLabel, descriptionTextArea,
                statusLabel, statusComboBox, // Added
                addButton
        );

        // Add bottom margin to create space between the last input field and the button
        VBox.setMargin(quantityTextField, new Insets(0, 0, 10, 0));
        // Add space to the left of the button using layout parameters
        VBox.setMargin(addButton, new Insets(0, 0, 0, 90));

        StackPane contentStackPane = new StackPane();
        contentStackPane.getChildren().addAll(inputLayout);
        contentStackPane.setBackground(new Background(new BackgroundFill(Color.rgb(34, 66, 122), CornerRadii.EMPTY, Insets.EMPTY)));

        // Wrap the contentStackPane in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentStackPane);
        scrollPane.setFitToWidth(true); // Enable width resizing
        scrollPane.setFitToHeight(true); // Enable height resizing

     // Scene
        Scene scene = new Scene(scrollPane, 500, 500); // Increased the height
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

    private TextArea createHoverTextArea() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter here");
        textArea.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        textArea.setOnMouseEntered(e -> textArea.setStyle("-fx-background-color: #b3e0ff;"));
        textArea.setOnMouseExited(e -> textArea.setStyle("-fx-background-color: #e6f7ff;"));
        return textArea;
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

    private ComboBox<String> createComboBox1() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select hostel first");
        comboBox.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        comboBox.setOnMouseEntered(e -> comboBox.setStyle("-fx-background-color: #b3e0ff;"));
        comboBox.setOnMouseExited(e -> comboBox.setStyle("-fx-background-color: #e6f7ff;"));
        return comboBox;
    }

    // Added method to create ComboBox for status
    private ComboBox<String> createComboBoxStatus() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        comboBox.setOnMouseEntered(e -> comboBox.setStyle("-fx-background-color: #b3e0ff;"));
        comboBox.setOnMouseExited(e -> comboBox.setStyle("-fx-background-color: #e6f7ff;"));
        // Populate ComboBox with your data
        comboBox.getItems().addAll("Accept", "Reject", "Inprogress"); // Example data
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

