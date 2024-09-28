package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import validation.InputValidation;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class Verification extends Application {

    private String userEmail;
    private String verificationCode;
    private SignUp signup;

    public Verification(SignUp signup) {
    	this.signup = signup;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode() {
    	this.verificationCode = generateVerificationCode();
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = createVerificationPane();

        Scene scene = new Scene(gridPane, 400, 150);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Email Verification");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        primaryStage.show();
     // Set the close request handler
        primaryStage.setOnCloseRequest(event -> handleCloseRequest());
    }

    private GridPane createVerificationPane() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));

        Label verificationLabel = new Label("Enter Verification Code:");
        TextField verificationField = new TextField();
        Button verifyButton = new Button("Verify");

        GridPane.setConstraints(verificationLabel, 0, 0);
        GridPane.setConstraints(verificationField, 1, 0);
        GridPane.setConstraints(verifyButton, 2, 0);

        gridPane.getChildren().addAll(verificationLabel, verificationField, verifyButton);

        verifyButton.setOnAction(event ->
        handleVerification(verificationField, (Stage) verifyButton.getScene().getWindow()));

        return gridPane;
    }

    private void handleVerification(TextField verificationField, Stage currentStage) {
        String enteredCode = verificationField.getText();
        if (enteredCode.equals(verificationCode)) {
        	// Trim spaces from the input fields
            InputValidation.trimSpaces(verificationField);
           
            showAlert(Alert.AlertType.INFORMATION, "Verification Success", "Account verified successfully!");

            // Update status in the database
            signup.admndb.updateStatusInDatabase(true, userEmail);

            currentStage.close();
        } else {
            showAlertWithCallback(Alert.AlertType.ERROR, "Verification Failed", "Invalid verification code. Please try signing up again.", () -> {
                // Delete the account from the database
                signup.admndb.deleteAccountByEmail(userEmail);
                currentStage.close();
                // Open SignUp page
                signup.start(new Stage());
            });
        }
    }

    private void showAlertWithCallback(Alert.AlertType alertType, String title, String message, Runnable callback) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            callback.run();
        }
    }



    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        

        // Send verification email
        sendVerificationEmail();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail() {
        final String senderEmail = "laibaejaz9797@gmail.com";
        final String senderPassword = "iymk upgp jfsk omhq"; // Replace with your actual email password

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Email Verification");
            message.setText("Your verification code is: " + getVerificationCode());

            Transport.send(message);

            //System.out.println("Verification code sent to the user's email.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.show();
    }
    private void handleCloseRequest() {
        // Delete the account from the database when the window is closed
        signup.admndb.deleteAccountByEmail(userEmail);
    }
}
