package application;

import DatabaseClasses.RoomDB;
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

public class imanEditRooms extends Application {

    private Runnable onEditComplete;
    private RoomDB roomdb;
    private String hostelname;
    private Object[] selectedRoom;
    private int roomId;

    public imanEditRooms(Object[] selectedHostel, int roomId, Runnable onEditComplete, RoomDB roomdb, String hostelname) {
        this.onEditComplete = onEditComplete;
        this.roomdb = roomdb;
        this.hostelname = hostelname;
        this.selectedRoom = selectedHostel;
        this.roomId = roomId;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Room");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);
        // Labels
        Label roomLabel = createLabel("Room No.:");
        Label floorLabel = createLabel("Floor No.:");
        Label roomCategoryLabel = createLabel("Room Category:");

        // Increase font size of labels
        roomLabel.setFont(new Font(16));
        floorLabel.setFont(new Font(16));
        roomCategoryLabel.setFont(new Font(16));

        // Input Fields (Use TextFields to display existing data for editing)
        TextField roomTextField = createHoverTextField("Existing Room No.");
        TextField roomCategoryTextField = createHoverTextField("Existing Room Category");

        // Create a ComboBox for floor number
        ComboBox<String> floorComboBox = createFloorComboBox();

        // Pre-fill text fields with existing data
        roomTextField.setText(getProperty(selectedRoom, 1));
        roomCategoryTextField.setText(getProperty(selectedRoom, 3));

        String existingFloor = getProperty(selectedRoom, 2);
        floorComboBox.getSelectionModel().select(existingFloor);

        // Set maximum width for text fields and combo box
        roomTextField.setMaxWidth(300);
        floorComboBox.setMaxWidth(300);
        roomCategoryTextField.setMaxWidth(300);
     // Set style for the ComboBox
        floorComboBox.setStyle("-fx-background-color: #e6f7ff; -fx-text-inner-color: white;");
        floorComboBox.setOnMouseEntered(e -> floorComboBox.setStyle("-fx-background-color: #b3e0ff;"));
        floorComboBox.setOnMouseExited(e -> floorComboBox.setStyle("-fx-background-color: #e6f7ff;"));
        Button editButton = createHoverButton("Edit Room");
        editButton.setOnAction(event -> {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(roomTextField);
            InputValidation.trimSpaces(roomCategoryTextField);
            // Validate input using the InputValidation class
            if (!InputValidation.areFieldsFilled(roomTextField, roomCategoryTextField)) {
                // Display an error message for unfilled fields
                showAlert("Error", "Please fill in all fields.");
                return;
            }

          

            // Check for duplicate room number
            String editedRoomNo = roomTextField.getText();
            if (roomdb.isDuplicateRoomNumberEdit(hostelname, editedRoomNo, roomId)) {
                showAlert("Error", "Duplicate room number. Please enter a unique room number within the hostel.");
                return;
            }
            // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(roomCategoryTextField.getText(), 50)) {
                showAlert("Error", "Room category should not exceed 50 characters.");
                return;
            }
         // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(roomTextField.getText(), 50)) {
                showAlert("Error", "Room number should not exceed 50 characters.");
                return;
            }
            // Perform the update operation using roomId and other roomData values
            roomdb.updateRoomInfo(hostelname, roomId, editedRoomNo, floorComboBox.getValue(), roomCategoryTextField.getText());
            onEditComplete.run(); // Notify the main class that editing is complete
            primaryStage.close(); // Close the editHostelStage
        });

        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                floorLabel, floorComboBox,
             
                roomCategoryLabel, roomCategoryTextField,
                roomLabel, roomTextField,
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
    private ComboBox<String> createFloorComboBox() {
        ComboBox<String> floorComboBox = new ComboBox<>();

        // Fetch the number of floors for the hostel
        int numberOfFloors = roomdb.getNumberOfFloors(hostelname);

        // Populate the ComboBox based on the number of floors
        for (int i = 0; i < numberOfFloors; i++) {
            floorComboBox.getItems().add((i == 0) ? "Ground Floor" : "Floor " + i);
        }

        return floorComboBox;
    }
}