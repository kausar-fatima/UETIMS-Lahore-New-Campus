package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class hostelDB {
	private Connection connection;
	 private static DatabaseConnector databaseConnector = new DatabaseConnector();
	
    public ObservableList<Object[]> hostelInfo() {
    	ObservableList<Object[]> rows = FXCollections.observableArrayList();
	// Populate the table model with data
       try {
    	   Connection connection = databaseConnector.connect();
           String selectHostelSql = "SELECT * FROM Hostel";
           
           try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql);
                ResultSet resultSet = selectHostelStatement.executeQuery()) {

               while (resultSet.next()) {
            	   int id=resultSet.getInt("HostelId");
                   String name = resultSet.getString("Name");
                   String location = resultSet.getString("Location");
                   String floors = resultSet.getString("NumberOfFloors");
                   String warden = resultSet.getString("Warden");
                   String totalRooms = getTotalRooms(resultSet.getInt("HostelId"));
                   
                   // Add row to the table model
                   
                   rows.add(new Object[]{name, location, floors, warden, totalRooms,id});
                   
               }
               return rows;
           }
       } catch (SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error retrieving hostel information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
	return rows;

   }
    
   
// Helper method to get total rooms for a hostel from the database
   private static String getTotalRooms(int hostelId) {
       try {
    	   Connection connection = databaseConnector.connect();
           String selectRoomsSql = "SELECT TotalRooms FROM HostelRooms WHERE HostelId = ?";
           try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
               selectRoomsStatement.setInt(1, hostelId);
               ResultSet resultSet = selectRoomsStatement.executeQuery();
               if (resultSet.next()) {
                   return String.valueOf(resultSet.getInt("TotalRooms"));
               }
           }
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       return "0";
   }
   // method to delete total rooms
   
   
   //Add hostel information
   public void addHostelInfo(String name, String location, int floors, String warden) {
	   try {
		   Connection connection = databaseConnector.connect();

           // Insert into Hostel table
           String hostelSql = "INSERT INTO Hostel (Name, Location, NumberOfFloors, Warden) VALUES (?, ?, ?, ?)";

           try (PreparedStatement hostelStatement = connection.prepareStatement(hostelSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

               hostelStatement.setString(1, name);
               hostelStatement.setString(2, location);
               hostelStatement.setInt(3, floors);
               hostelStatement.setString(4, warden);

               int affectedHostelRows = hostelStatement.executeUpdate();

               // Retrieve auto-generated HostelId
               try (ResultSet generatedKeys = hostelStatement.getGeneratedKeys()) {
                   if (generatedKeys.next()) {
                       int hostelId = generatedKeys.getInt(1);
                      // System.out.println(hostelId);
                       addHostelTotalRooms(hostelId);
                       if (affectedHostelRows > 0) {
                           JOptionPane.showMessageDialog(null, "Hostel added successfully!");
                       }
                   }
               }
           }
       } catch (SQLException | NumberFormatException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error adding hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       } finally {
           if (connection != null) {
               try {
                   connection.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   
   // add totalrooms to hostelRooms table
   public void addHostelTotalRooms(int hostelId) {
       try {
    	   Connection connection = databaseConnector.connect();
       
               // Insert into HostelRooms table
               String hostelSql = "INSERT INTO HostelRooms (HostelId, TotalRooms) VALUES (?, ?)";
               try (PreparedStatement hostelStatement = connection.prepareStatement(hostelSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                   hostelStatement.setInt(1, hostelId);
                   hostelStatement.setInt(2, 0);

                   int affectedHostelRows = hostelStatement.executeUpdate();

                   // Retrieve auto-generated HostelId
                   try (ResultSet generatedKeys = hostelStatement.getGeneratedKeys()) {
                       if (generatedKeys.next()) {
                           //int hostelId = generatedKeys.getInt(1);

                           if (affectedHostelRows > 0) {
                              // JOptionPane.showMessageDialog(null, "HostelRooms added successfully!");
                           }
                       }
                   }
               }
           
       } catch (SQLException | NumberFormatException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error adding hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       } 
       finally {
           if (connection != null) {
               try {
                   connection.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   
   //update hostel
   public void updateHostelInfo(String updatename, String name, String location, int floors, String warden) {
	    try {
	    	Connection connection = databaseConnector.connect();

	        // Update Hostel table
	        String updateHostelSql = "UPDATE Hostel SET Name = ?, Location = ?, NumberOfFloors = ?, Warden = ? WHERE Name = ?";

	        try (PreparedStatement updateHostelStatement = connection.prepareStatement(updateHostelSql)) {
	        	updateHostelStatement.setString(1, name);
	        	updateHostelStatement.setString(2, location);
	            updateHostelStatement.setInt(3, floors);
	            updateHostelStatement.setString(4, warden);
	            updateHostelStatement.setString(5, updatename);

	            int affectedHostelRows = updateHostelStatement.executeUpdate();

	            if (affectedHostelRows > 0) {
	                JOptionPane.showMessageDialog(null, "Hostel information updated successfully!");
	            } else {
	                JOptionPane.showMessageDialog(null, "updation failed.");
	            }
	        }

	    } catch ( SQLException | NumberFormatException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error updating hostel information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
   
   //delete
   public void deleteHostelInfo(String name) {
	   
	    try {
	    	Connection connection = databaseConnector.connect();

	        // Retrieve HostelId for the given hostel name
	        String selectHostelIdSql = "SELECT HostelId FROM Hostel WHERE Name = ?";
	        int hostelId;

	        try (PreparedStatement selectHostelIdStatement = connection.prepareStatement(selectHostelIdSql)) {
	            selectHostelIdStatement.setString(1, name);

	            try (ResultSet resultSet = selectHostelIdStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    hostelId = resultSet.getInt("HostelId");

	                    // Delete from AllocatedItems table
	                    String deleteAllocatedItemsSql = "DELETE FROM AllocatedItems WHERE HostelId = ?";
	                    try (PreparedStatement deleteAllocatedItemsStatement = connection.prepareStatement(deleteAllocatedItemsSql)) {
	                        deleteAllocatedItemsStatement.setInt(1, hostelId);
	                        deleteAllocatedItemsStatement.executeUpdate();
	                    }

	                    // Delete from Rooms table
	                    String deleteRoomsSql = "DELETE FROM Rooms WHERE HostelId = ?";
	                    try (PreparedStatement deleteRoomsStatement = connection.prepareStatement(deleteRoomsSql)) {
	                        deleteRoomsStatement.setInt(1, hostelId);
	                        deleteRoomsStatement.executeUpdate();
	                    }

	                    // Delete from HostelRooms table
	                    String deleteHostelRoomsSql = "DELETE FROM HostelRooms WHERE HostelId = ?";
	                    try (PreparedStatement deleteHostelRoomsStatement = connection.prepareStatement(deleteHostelRoomsSql)) {
	                        deleteHostelRoomsStatement.setInt(1, hostelId);
	                        deleteHostelRoomsStatement.executeUpdate();
	                    }

	                    // Delete from Hostel table
	                    String deleteHostelSql = "DELETE FROM Hostel WHERE HostelId = ?";
	                    try (PreparedStatement deleteHostelStatement = connection.prepareStatement(deleteHostelSql)) {
	                        deleteHostelStatement.setInt(1, hostelId);

	                        int affectedHostelRows = deleteHostelStatement.executeUpdate();

	                        if (affectedHostelRows > 0) {
	                            JOptionPane.showMessageDialog(null, "Hostel, associated rooms inside hostel and associated items deleted successfully");
	                        } else {
	                            JOptionPane.showMessageDialog(null, "Hostel not deleted.");
	                        }
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(null, "Hostel not found.");
	                }
	            }
	        }
	    } catch ( SQLException | NumberFormatException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error deleting hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

// to get total hostels
   public ObservableList<String> getHostelNames() {
   	ObservableList<String> Names = FXCollections.observableArrayList();
       try {
    	   Connection connection = databaseConnector.connect();
           String selectHostelIdSql = "SELECT Name FROM Hostel";

           try (PreparedStatement selectnamesStatement = connection.prepareStatement(selectHostelIdSql)) {

               ResultSet resultSet = selectnamesStatement.executeQuery();
               while (resultSet.next()) {
                   Names.add(resultSet.getString("Name"));
               }
               return Names;
            }
           
       } catch ( SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error retrieving name of hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
	return null;
   }
   
   //unique hostel
   public boolean isHostelExists(String name) {
	    try {
	    	Connection connection = databaseConnector.connect();

	        String selectHostelSql = "SELECT COUNT(*) AS count FROM Hostel WHERE Name = ?";
	        
	        try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql)) {
	            selectHostelStatement.setString(1, name);

	            try (ResultSet resultSet = selectHostelStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    int count = resultSet.getInt("count");
	                    return count > 0;
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error checking hostel existence: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return false;
	}


 

   //search
   public ObservableList<Object[]> searchHostelByName(String hostelName) {
       ObservableList<Object[]> rows = FXCollections.observableArrayList();

       try {
       	Connection connection = databaseConnector.connect();

           String selectHostelSql = "SELECT * FROM Hostel WHERE Name LIKE ?";
           
           try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql)) {
               selectHostelStatement.setString(1, "%" + hostelName + "%");

               try (ResultSet resultSet = selectHostelStatement.executeQuery()) {
                   while (resultSet.next()) {
                       String name = resultSet.getString("Name");
                       String location = resultSet.getString("Location");
                       String floors = resultSet.getString("NumberOfFloors");
                       String warden = resultSet.getString("Warden");
                       String totalRooms = getTotalRooms(resultSet.getInt("HostelId"));
                       rows.add(new Object[]{name, location, floors, warden, totalRooms});
                   }
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
           showAlert("Error", "Error retrieving hostel information: " + e.getMessage());
       }

       return rows;
   }
   public ObservableList<Object[]> searchHostelByFloors(String numberOfFloors) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	Connection connection = databaseConnector.connect();

	        String selectHostelSql = "SELECT * FROM Hostel WHERE NumberOfFloors LIKE ?";

	        try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql)) {
	        	  selectHostelStatement.setString(1, "%" + numberOfFloors + "%");

	            try (ResultSet resultSet = selectHostelStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    String name = resultSet.getString("Name");
	                    String location = resultSet.getString("Location");
	                    String floors = resultSet.getString("NumberOfFloors");
	                    String warden = resultSet.getString("Warden");
	                    String totalRooms = getTotalRooms(resultSet.getInt("HostelId"));
	                    rows.add(new Object[]{name, location, floors, warden, totalRooms});
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        showAlert("Error", "Error retrieving hostel information: " + e.getMessage());
	    }

	    return rows;
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