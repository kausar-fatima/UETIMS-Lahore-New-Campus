package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.JOptionPane;

import application.Login;
import application.SignUp;
import application.Verification;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AdminDB {
	
	private Connection connection = null;
	private DatabaseConnector databaseConnector = new DatabaseConnector();
	
	public void addAdminInfo(List<String> entries, SignUp signup, Verification verification) {
        // Check if one admin already exists
        if (isOneAdminAlreadyExists()) {
            showAlert("Error", "Only one admin is allowed.");
            return;
        }

        try {
            connection = databaseConnector.connect();

            String sql = "INSERT INTO Admin (Email, Password, Name, verificationCode, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, entries.get(0));
                preparedStatement.setString(2, entries.get(1));
                preparedStatement.setString(3, entries.get(2));
                preparedStatement.setString(4, entries.get(3));
                preparedStatement.setString(5, entries.get(4));

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    signup.openVerificationPage(entries.get(0));
                    verification.setUserEmail(entries.get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error to add admin Info: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }
    }

	 // Check if Email already exists
	public boolean isEmailAlreadyExists(String email) {
	    try {
	        connection = databaseConnector.connect();

	        String sql = "SELECT COUNT(*) FROM Admin WHERE Email = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setString(1, email);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    int count = resultSet.getInt(1);
	                    return count > 0;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        databaseConnector.closeConnection();
	    }
	    return false;
	}

	// validate
	public boolean validateLogin(String email, String password) {
	    try {
	        connection = databaseConnector.connect();

	        String query = "SELECT * FROM Admin WHERE Email = ? AND Password = ? AND Status = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, email);
	            preparedStatement.setString(2, password);
	            preparedStatement.setBoolean(3, true);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                return resultSet.next(); // If there is a result, credentials are valid
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // If an exception occurs, login fails
	    } finally {
	        databaseConnector.closeConnection();
	    }
	}

	//updateStatus
	public void updateStatusInDatabase(boolean status, String userEmail) {
	    try {
	        connection = databaseConnector.connect();

	        String sql = "UPDATE Admin SET status = ? WHERE Email = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setBoolean(1, status);
	            preparedStatement.setString(2, userEmail);

	            int rowsUpdated = preparedStatement.executeUpdate();

	            if (rowsUpdated > 0) {
	                //System.out.println("Status updated in the database: " + status);

	                // Create a new instance of the SignUp class
	                Login login = new Login();

	                // Call the start method to display the login page
	                login.start(new Stage());
	            } else {
	               // System.out.println("Failed to update status in the database.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        databaseConnector.closeConnection();
	    }
	}

	public boolean isOneAdminAlreadyExists() {
	    try {
	        connection = databaseConnector.connect();

	        String sql = "SELECT COUNT(*) FROM Admin";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
	             ResultSet resultSet = preparedStatement.executeQuery()) {
	            if (resultSet.next()) {
	                int count = resultSet.getInt(1);
	                return count > 0;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        databaseConnector.closeConnection();
	    }
	    return false;
	}

	public String getEmail(String email) {
	    String userEmail = null;
	    try {
	        connection = databaseConnector.connect();

	        String sql = "SELECT Email FROM Admin WHERE Email = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setString(1, email);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    userEmail = resultSet.getString("Email");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        databaseConnector.closeConnection();
	    }
	    return userEmail;
	}

	public String getUsernameByEmail(String email) {
	    String username = null;
	    try {
	        connection = databaseConnector.connect();

	        String sql = "SELECT Name FROM Admin WHERE Email = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setString(1, email);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    username = resultSet.getString("Name");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        databaseConnector.closeConnection();
	    }
	    return username;
	}

	// method to update the username by email
    public void updateUsernameByEmail(String email, String newUsername) {
        try {
            connection = databaseConnector.connect();

            String sql = "UPDATE Admin SET Name = ? WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, email);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                   // System.out.println("Username updated in the database: " + newUsername);
                } else {
                   // System.out.println("Failed to update username in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseConnector.closeConnection();
        }
    }


    public String getPasswordByEmail(String email) {
        String password = null;
        try {
            connection = databaseConnector.connect();

            String sql = "SELECT Password FROM Admin WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        password = resultSet.getString("Password");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseConnector.closeConnection();
        }
        return password;
    }

    public void deleteAccountByEmail(String email) {
        try {
            connection = databaseConnector.connect();

            String sql = "DELETE FROM Admin WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                int rowsDeleted = preparedStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    //System.out.println("Account deleted from the database: " + email);
                    // Optionally, you can show a success message or navigate to a different view
                } else {
                    //System.out.println("Failed to delete account from the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseConnector.closeConnection();
        }
    }

   

  //  method to display alerts
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
