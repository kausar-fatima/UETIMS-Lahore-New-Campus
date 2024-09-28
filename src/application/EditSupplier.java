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

public class EditSupplier extends Application {
	private final Runnable onCloseCallback;
	private Object[] selectedSupplier;
	private supplierDB supplierdb;
    public EditSupplier(Object[] selectedSupplier, Runnable onCloseCallback, supplierDB supplierdb) {
    	this.onCloseCallback = onCloseCallback;
    	this.selectedSupplier = selectedSupplier; 
    	this.supplierdb = supplierdb;
	}

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Edit Supplier");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);

        // Labels
        Label nameLabel = createLabel("Name:");
        Label BillnoLabel = createLabel("Billno:");
        Label deliverydateLabel = createLabel("Delivery Date:");

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
        BillnoLabel.setFont(new Font(16));
        deliverydateLabel.setFont(new Font(16));

      //Input Fields (Use TextFields to display existing data for editing)
        TextField nameTextField = createHoverTextField("Existing Name");
        TextField billNoTextField = createHoverTextField("Existing Bill No");
        DatePicker deliverydatePicker = new DatePicker(LocalDate.now());

        // Pre-fill text fields with existing data
        nameTextField.setText(getProperty(selectedSupplier, 1));
        billNoTextField.setText(getProperty(selectedSupplier, 2));
        deliverydatePicker.setValue(LocalDate.parse(getProperty(selectedSupplier, 3)));
        deliverydatePicker.setEditable(false); // Make the DatePicker non-editable


        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);
        billNoTextField.setMaxWidth(300);


     // Button
        Button EditButton = createHoverButton("Edit Supplier");
        EditButton.setOnAction(event -> {
        	InputValidation.trimSpaces(nameTextField);
        	InputValidation.trimSpaces(billNoTextField);
        	//InputValidation.trimSpaces(deliverydatePicker);
            // Validate input using the InputValidation class
            if (!InputValidation.areFieldsFilled(nameTextField, billNoTextField, deliverydatePicker)) {
                // Display an error message for unfilled fields
            	showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Additional validation for alphabetic supplier name
            if (!InputValidation.isValidAlphabeticText(nameTextField.getText())) {
            	showAlert("Error", "Supplier name should contain only alphabetic characters.");
                return;
            }
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Supplier name should not exceed 50 characters.");
                return;
            }
            if (!InputValidation.isValidStringLength(billNoTextField.getText(), 50)) {
                showAlert("Error", "Billno. should not exceed 50 characters.");
                return;
            }
            // Check if the selected date is in the future
            LocalDate selectedDate = deliverydatePicker.getValue();
            LocalDate currentDate = LocalDate.now();

            if (selectedDate.isAfter(currentDate)) {
                showAlert("Error", "Selected date cannot be in the future.");
                deliverydatePicker.setValue(LocalDate.parse(getProperty(selectedSupplier, 3)));
                return;
            }
            // Add your logic to handle the "Edit Supplier" button click
            int SupplierId = Integer.parseInt(getProperty(selectedSupplier, 0));
            supplierdb.updateSupplierInfo(SupplierId, nameTextField.getText(), billNoTextField.getText(), deliverydatePicker.getValue().toString());
            onCloseCallback.run();
            primaryStage.close();
        });



        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        //Node deliverydatePicker;
		inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
                BillnoLabel, billNoTextField,
                deliverydateLabel, deliverydatePicker,
                EditButton
        );
        // Add space to the left of the button using layout parameters
        VBox.setMargin(EditButton, new Insets(0, 0, 0, 90));

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

    private TextField createHoverTextField(String string) {
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

    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
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





