package application;

import DatabaseClasses.supplierDB;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;
import java.time.LocalDate;

public class AddSupplier extends Application {
    private final Runnable onCloseCallback;

    public AddSupplier(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Add Supplier");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);

        // Labels
        Label nameLabel = createLabel("Name:");
        Label BillnoLabel = createLabel("Billno.:");
        Label deliverydateLabel = createLabel("Delivery Date:");

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
        BillnoLabel.setFont(new Font(16));
        deliverydateLabel.setFont(new Font(16));

        // Input Fields
        TextField nameTextField = createHoverTextField();
        TextField BillnoTextField = createHoverTextField();
        DatePicker deliverydatePicker = new DatePicker();
        deliverydatePicker.setEditable(false); // Make the DatePicker non-editable

        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        BillnoTextField.setMaxWidth(300);
        deliverydatePicker.setMaxWidth(300);
     // Add an event handler to the DatePicker to restrict future dates
     // Add an event handler to the DatePicker to restrict future dates
        deliverydatePicker.setOnAction(event -> {
            LocalDate selectedDate = deliverydatePicker.getValue();
            LocalDate currentDate = LocalDate.now();

            if (selectedDate != null && selectedDate.isAfter(currentDate)) {
                // Show alert for future date
                showAlert("Error", "Selected date cannot be in the future.");
                
                // Remove the selected date
                deliverydatePicker.setValue(null);
            }
        });

        
        // Button
        Button addButton = createHoverButton("Add Supplier");
        addButton.setOnAction(event -> {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(nameTextField);
            InputValidation.trimSpaces(BillnoTextField);
            //InputValidation.trimSpaces(deliverydatePicker);
            // Check if any field is unfilled
            if (!InputValidation.areFieldsFilled(nameTextField, BillnoTextField, deliverydatePicker)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Validate supplier name
            String supplierName = nameTextField.getText().trim();
            if (!InputValidation.isValidAlphabeticText(supplierName)) {
                showAlert("Error", "Supplier name should contain only alphabetic characters.");
                return;
            }
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Supplier name should not exceed 50 characters.");
                return;
            }
            if (!InputValidation.isValidStringLength(BillnoTextField.getText(), 50)) {
                showAlert("Error", "Billno. should not exceed 50 characters.");
                return;
            }
            // Add your logic to handle the "Add Supplier" button click
            supplierDB supplierdb = new supplierDB();
            supplierdb.addSupplierInfo(
                    supplierName,
                    BillnoTextField.getText(),
                    deliverydatePicker.getValue().toString()
            );
            onCloseCallback.run();
            primaryStage.close();
        });

        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
                BillnoLabel, BillnoTextField,
                deliverydateLabel, deliverydatePicker,
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
