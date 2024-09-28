package application;

import java.time.LocalDate;

import DatabaseClasses.CategoryDB;
import DatabaseClasses.RoomDB;
import DatabaseClasses.hostelDB;
import DatabaseClasses.requestDB;
import javafx.application.Application;
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

public class EditRequest extends Application {
	private Object[] selectedRequest;
	private Runnable onCloseEditCall;
	private requestDB requestdb;
	
	public EditRequest(Object[] selectedRequest, Runnable onCloseEditCall, requestDB requestdb) {
	    this.selectedRequest = selectedRequest;
	    this.onCloseEditCall = onCloseEditCall;
	    this.requestdb = requestdb;
	}


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Requisition");
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

       
        // ComboBoxes
        ComboBox<String> hostelComboBox = createComboBox();
        ComboBox<String> roomComboBox = createComboBox1();
        ComboBox<String> categoryComboBox = createComboBox(); // Placeholder, replace with your data
        ComboBox<String> statusComboBox = createComboBoxStatus(); // Added

        // Placeholders
        TextField nameTextField = createHoverTextField();
        TextField quantityTextField = createHoverTextField();
        // Description TextArea
        TextArea descriptionTextArea = createHoverTextArea();
        descriptionTextArea.setWrapText(true); // Enable text wrapping
        descriptionTextArea.setPrefRowCount(2); // Set the preferred number of rows
        //TextField roomTextField = createHoverTextField();
        // Date Picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(276); // Set fixed width
        datePicker.setEditable(false); // Make the DatePicker non-editable


        // Increase font size of labels
        
        hostelLabel.setFont(new Font(16));
        roomLabel.setFont(new Font(16));
        categoryLabel.setFont(new Font(16));
        nameLabel.setFont(new Font(16));
        quantityLabel.setFont(new Font(16));
        statusLabel.setFont(new Font(16)); // 
        dateLabel.setFont(new Font(16));
        descriptionLabel.setFont(new Font(16)); // Added
        
     // Pre-fill text fields with existing data
        hostelComboBox.setValue(getProperty(selectedRequest, 1));
        roomComboBox.setValue(getProperty(selectedRequest, 2));
        nameTextField.setText(getProperty(selectedRequest, 3));
        categoryComboBox.setValue(getProperty(selectedRequest, 4));
        quantityTextField.setText(getProperty(selectedRequest, 5));
        descriptionTextArea.setText(getProperty(selectedRequest, 8));
        datePicker.setValue(LocalDate.parse(getProperty(selectedRequest, 7)));
        statusComboBox.setValue(getProperty(selectedRequest, 6));

        String reqId = getProperty(selectedRequest, 0);
        String prevStatus = getProperty(selectedRequest, 6);
        String prevQuantity = getProperty(selectedRequest, 5);

        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        quantityTextField.setMaxWidth(300);
        descriptionTextArea.setMaxWidth(300);

        // Set fixed width for ComboBoxes
        hostelComboBox.setMaxWidth(300);
        roomComboBox.setMaxWidth(300);
        categoryComboBox.setMaxWidth(300);
        statusComboBox.setMaxWidth(300);
        datePicker.getEditor().setStyle("-fx-pref-width: 300;");

        // Make fields uneditable when the status is "Accept"
        if ("Accept".equalsIgnoreCase(statusComboBox.getValue())) {
            hostelComboBox.setDisable(true);
            roomComboBox.setDisable(true);
            nameTextField.setEditable(false);
            categoryComboBox.setDisable(true);
          
        }

        // Add an event handler to update field editability when the status changes
        statusComboBox.setOnAction(event -> {
            String selectedStatus = statusComboBox.getValue();
            if ("Accept".equalsIgnoreCase(selectedStatus)) {
                hostelComboBox.setDisable(true);
                roomComboBox.setDisable(true);
                nameTextField.setEditable(false);
                categoryComboBox.setDisable(true);
                
            } else if ("Reject".equalsIgnoreCase(selectedStatus) && "Accept".equalsIgnoreCase(prevStatus)) {
                // If the previous status was "Accept" and the current status is "Reject", disable the fields
                hostelComboBox.setDisable(true);
                roomComboBox.setDisable(true);
                nameTextField.setEditable(false);
                categoryComboBox.setDisable(true);
                quantityTextField.setDisable(true);
            }else if ("Inprogress".equalsIgnoreCase(selectedStatus) && "Accept".equalsIgnoreCase(prevStatus)) {
                // If the previous status was "Accept" and the current status is "Reject", disable the fields
                hostelComboBox.setDisable(true);
                roomComboBox.setDisable(true);
                nameTextField.setEditable(false);
                categoryComboBox.setDisable(true);
                quantityTextField.setDisable(true);
            }else {
                // Enable fields if the status is not "Accept"
                hostelComboBox.setDisable(false);
                roomComboBox.setDisable(false);
                nameTextField.setEditable(true);
                categoryComboBox.setDisable(false);
               
            }
        });
        
        //retrieving data from db
        hostelComboBox.setItems(new hostelDB().getHostelNames());
        roomComboBox.setItems(new RoomDB().getRoomNumbers(hostelComboBox.getValue())); 
        categoryComboBox.setItems(new CategoryDB().getItemCategories());
        
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
        // Add listener to itemQuantityTextField
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
     // Button
        Button editButton = createHoverButton("Edit Requisition");
        editButton.setOnAction(event -> {
        	
            String selectedHostel = hostelComboBox.getValue();
            String selectedRoom = roomComboBox.getValue();
            String selectedCategory = categoryComboBox.getValue();
            String itemName = nameTextField.getText();
            String descriptionText = descriptionTextArea.getText();
         // Trim spaces from the input fields
            InputValidation.trimSpaces(nameTextField);
            InputValidation.trimSpaces(quantityTextField);
            InputValidation.trimSpaces(descriptionTextArea);
           // InputValidation.trimSpaces(datePicker);
            int quantity;

            // Validate the input
            if (!InputValidation.areFieldsFilled(datePicker, hostelComboBox, roomComboBox, categoryComboBox, nameTextField, quantityTextField,descriptionTextArea, statusComboBox)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (!InputValidation.isValidAlphabeticText(itemName)) {
                showAlert("Error", " Item name should contain only alphabetic characters.");
                return;
            }

            if (!InputValidation.isValidPositiveInteger(quantityTextField.getText())) {
            	 showAlert("Error", "Quantity should contain only non-negative numeric value e.g. 1.");
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
            // Check if the quantity is greater than 5000
            if (!InputValidation.isValidNumber(Integer.parseInt(quantityTextField.getText()) , 5000)) {
                showAlert("Error", "You can add only 5000 items at a time.");
                return;
            }
            
            
            // Check if the selected date is in the future
            LocalDate selectedDate1 = datePicker.getValue();
            LocalDate currentDate = LocalDate.now();

            if (selectedDate1.isAfter(currentDate)) {
                showAlert("Error", "Selected date cannot be in the future.");
             // Remove the selected date
                // Set back to previous date
                datePicker.setValue(LocalDate.parse(getProperty(selectedRequest, 7)));
                           return;
            }

            quantity = Integer.parseInt(quantityTextField.getText());

            String selectedStatus = statusComboBox.getValue();

            // Add the request to the database
            try {
                // Create an instance of your requestDB class
                //requestDB requestDatabase = new requestDB();

                // Convert the selected date to a java.sql.Date object
                java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate1);

                // Add the status parameter to the updateRequestInfo method
                requestdb.updateRequestInfo(Integer.parseInt(prevQuantity),prevStatus,Integer.parseInt(reqId), selectedHostel, selectedRoom, itemName, selectedCategory, quantity, descriptionText, sqlDate, selectedStatus);
                onCloseEditCall.run();
                primaryStage.close();
                // Display a success message
                //System.out.println("Request Added!");
            } catch (Exception e) {
                // Handle exceptions, e.g., display an error message
                e.printStackTrace();
                System.out.println("Error editing requisition: " + e.getMessage());
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
                descriptionLabel,descriptionTextArea,
                statusLabel, statusComboBox, // Added
                editButton
        );

        // Add bottom margin to create space between the last input field and the button
        VBox.setMargin(quantityTextField, new Insets(0, 0, 10, 0));
        // Add space to the left of the button using layout parameters
        VBox.setMargin(editButton, new Insets(0, 0, 0, 90));

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
    private TextArea createHoverTextArea() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter here");
        textArea.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: black;"); // Set text color to black
        textArea.setOnMouseEntered(e -> textArea.setStyle("-fx-background-color: #b3e0ff;"));
        textArea.setOnMouseExited(e -> textArea.setStyle("-fx-background-color: #e6f7ff;"));
        return textArea;
    }
}
