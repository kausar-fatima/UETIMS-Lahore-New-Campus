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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import validation.InputValidation;

public class AddCategory extends Application {
	private final Runnable onCloseCallback;
	public AddCategory(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	  primaryStage.setTitle("Add Category");
          Image icon = new Image("fi.png");

          // Set the icon image for the stage
          primaryStage.getIcons().add(icon);

        // Labels
        Label nameLabel = createLabel("Category Name:");
        

        // Increase font size of labels
        nameLabel.setFont(new Font(16));
 
        // Input Fields
        TextField nameTextField = createHoverTextField();


        // Set maximum width for text fields
        nameTextField.setMaxWidth(300);


        // Button
        Button addButton = createHoverButton("Add Category");
        addButton.setOnAction(event -> {
        	 InputValidation.trimSpaces(nameTextField);
            // Check for unfilled values
            if (!InputValidation.areFieldsFilled(nameTextField)) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }
           // Check for non-alphabetic hostel names
            if (!InputValidation.isValidAlphabeticText(nameTextField.getText())) {
                showAlert("Error", "Category name should contain only alphabetic characters.");
                return;
            }
            
        
         // Check the length of the hostel name
            if (!InputValidation.isValidStringLength(nameTextField.getText(), 50)) {
                showAlert("Error", "Category name should not exceed 50 characters.");
                return;
            }
         
            // Check if the hostel with the same name already exists
            String Name = nameTextField.getText();
            CategoryDB CategoryDb = new CategoryDB();
            if (CategoryDb.isCategoryExists(Name)) {
                showAlert("Error", "A category with the name '" + Name + "' already exists. Enter a unique category name.");
                return; // Stop further processing
            }

            // Add your logic to handle the "Add Hostel" button click
            CategoryDB addhostel = new CategoryDB();
            addhostel.addCategoryInfo(nameTextField.getText());
            onCloseCallback.run();
            primaryStage.close();
        });

        // Layout
        VBox inputLayout = new VBox(10);
        inputLayout.setAlignment(Pos.CENTER_LEFT); // Align input fields to the left
        inputLayout.setPadding(new Insets(20));
        inputLayout.getChildren().addAll(
                nameLabel, nameTextField,
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