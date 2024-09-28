package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;

public class Report extends Application {
public Report() {
    
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Report");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        
        Image logoImage = new Image("uet.png");
        ImageView logoImageView = new ImageView(logoImage);

        logoImageView.setFitWidth(100 + 20);
        logoImageView.setFitHeight(100 + 15);
        
        // Labels
        Label yLabel = createLabel("Year:");
        Label mLabel = createLabel("Month:"); // Moved up


        // Increase font size of labels
        yLabel.setFont(new Font(16));
        mLabel.setFont(new Font(16));


        // Input Fields
        TextField yTextField = createHoverTextField();
        
        TextField mTextField = createHoverTextField();

        // Set maximum width for text fields
        yTextField.setMaxWidth(300);
       
        mTextField.setMaxWidth(300);

       

        // Button
        Button generateReportButton = createHoverButton("Generate Report");
        generateReportButton.setOnAction(event -> {
        	InputValidation.trimSpaces(yTextField);
        	InputValidation.trimSpaces(mTextField);
            // Validate input fields
            if (!InputValidation.areFieldsFilled(yTextField, mTextField)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            // Validate year
            String yText = yTextField.getText().trim();
            if (!InputValidation.isZeroOrPositiveNumber(yText)) {
                showAlert("Error", "Please enter a valid positive number e.g 2024 for Year.");
                return;
            }

            // Validate month
            String mText = mTextField.getText().trim();
            if (!InputValidation.isZeroOrPositiveNumber(mText)) {
                showAlert("Error", "Please enter a valid positive number e.g 1 for Month.");
                return;
            }
            
            // Validate year and month
            String validationMessage = InputValidation.isValidYearAndMonth(yTextField.getText().trim(), mTextField.getText().trim());
            if (validationMessage != null) {
                showAlert("Error", validationMessage);
                return;
            }
            // Inside the generateReportButton.setOnAction(event -> { ... })

            int yearValue = Integer.parseInt(yText);
            int monthValue = Integer.parseInt(mText);

            // Pass year and month to GenerateReport screen
            GenerateReport generateReport = new GenerateReport(yearValue, monthValue);
            generateReport.start(new Stage()); // This line is important to actually show the stage
            primaryStage.close();
        });

       
        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
        		logoImageView,
                yLabel, yTextField,
                mLabel, mTextField,
                generateReportButton
        );
        // Add space to the left of the button using layout parameters
        VBox.setMargin(generateReportButton, new Insets(0, 0, 0, 0));

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
    private Button createHoverButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;"));
        return button;
    }
}
