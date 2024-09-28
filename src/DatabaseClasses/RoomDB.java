package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RoomDB {
	private Connection connection;
	 private static DatabaseConnector databaseConnector = new DatabaseConnector();
	public ObservableList<Object[]> RoomInfo(String hostelname) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	Connection connection = databaseConnector.connect();
	        // First, get the HostelId based on the HostelName
	        String selectHostelIdSql = "SELECT HostelId FROM Hostel WHERE Name = ?";
	        try (PreparedStatement selectHostelIdStatement = connection.prepareStatement(selectHostelIdSql)) {
	            selectHostelIdStatement.setString(1, hostelname);

	            try (ResultSet hostelIdResultSet = selectHostelIdStatement.executeQuery()) {
	                if (hostelIdResultSet.next()) {
	                    int hostelId = hostelIdResultSet.getInt("HostelId");

	                    // Now, use the obtained HostelId to select rooms
	                    String selectRoomsSql = "SELECT * FROM Rooms WHERE HostelId = ?";
	                    try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
	                        selectRoomsStatement.setInt(1, hostelId);

	                        try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
	                            while (resultSet.next()) {
	                            	int roomId=resultSet.getInt("RoomId");
	                            	String  roomNo = resultSet.getString("RoomNo");
	                                String floorNo = resultSet.getString("FloorNo");
	                                String roomCategory = resultSet.getString("RoomCategory");
	                                int totalItems = getTotalItems(hostelId, resultSet.getInt("RoomId"));
	                                

	                                // Add row to the table model
	                                rows.add(new Object[]{roomId,roomNo, floorNo, roomCategory, totalItems,hostelId});
	                            }
	                            return rows;
	                        }
	                    }
	                } else {
	                    // Handle the case where no matching HostelName is found
	                   // System.out.println("Hostel not found for HostelName: " + hostelname);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error retrieving room information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return rows;
	}
	public ObservableList<Object[]> searchByRoomNo(String hostelname, String roomNo) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	        Connection connection = databaseConnector.connect();

	        String selectRoomsByRoomNoAndHostelIdSql = "SELECT * FROM Rooms WHERE HostelId = ? AND RoomNo LIKE ?";

	        try (PreparedStatement selectRoomsByRoomNoAndHostelIdStatement = connection.prepareStatement(selectRoomsByRoomNoAndHostelIdSql)) {
	            selectRoomsByRoomNoAndHostelIdStatement.setInt(1, GethostelId(hostelname));
	            selectRoomsByRoomNoAndHostelIdStatement.setString(2, "%" + roomNo + "%");

	            try (ResultSet resultSet = selectRoomsByRoomNoAndHostelIdStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int roomId = resultSet.getInt("RoomId");
	                    String roomNumber = resultSet.getString("RoomNo");
	                    String floorNo = resultSet.getString("FloorNo");
	                    String roomCategory = resultSet.getString("RoomCategory");
	                    int totalItems = getTotalItems(GethostelId(hostelname), roomId);

	                    // Add row to the table model
	                    rows.add(new Object[]{roomId, roomNumber, floorNo, roomCategory, totalItems, GethostelId(hostelname)});
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching Rooms by RoomNo and HostelId: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}
	public ObservableList<Object[]> searchByRoomC(String hostelname, String roomNo) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	        Connection connection = databaseConnector.connect();

	        String selectRoomsByRoomNoAndHostelIdSql = "SELECT * FROM Rooms WHERE HostelId = ? AND RoomCategory LIKE ?";

	        try (PreparedStatement selectRoomsByRoomNoAndHostelIdStatement = connection.prepareStatement(selectRoomsByRoomNoAndHostelIdSql)) {
	            selectRoomsByRoomNoAndHostelIdStatement.setInt(1, GethostelId(hostelname));
	            selectRoomsByRoomNoAndHostelIdStatement.setString(2, "%" + roomNo + "%");

	            try (ResultSet resultSet = selectRoomsByRoomNoAndHostelIdStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int roomId = resultSet.getInt("RoomId");
	                    String roomNumber = resultSet.getString("RoomNo");
	                    String floorNo = resultSet.getString("FloorNo");
	                    String roomCategory = resultSet.getString("RoomCategory");
	                    int totalItems = getTotalItems(GethostelId(hostelname), roomId);

	                    // Add row to the table model
	                    rows.add(new Object[]{roomId, roomNumber, floorNo, roomCategory, totalItems, GethostelId(hostelname)});
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching Rooms Category RoomNo and HostelId: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}
	public ObservableList<Object[]> searchByRoomF(String hostelname, String roomNo) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	        Connection connection = databaseConnector.connect();

	        String selectRoomsByRoomNoAndHostelIdSql = "SELECT * FROM Rooms WHERE HostelId = ? AND FloorNo LIKE ?";

	        try (PreparedStatement selectRoomsByRoomNoAndHostelIdStatement = connection.prepareStatement(selectRoomsByRoomNoAndHostelIdSql)) {
	            selectRoomsByRoomNoAndHostelIdStatement.setInt(1, GethostelId(hostelname));
	            selectRoomsByRoomNoAndHostelIdStatement.setString(2, "%" + roomNo + "%");

	            try (ResultSet resultSet = selectRoomsByRoomNoAndHostelIdStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int roomId = resultSet.getInt("RoomId");
	                    String roomNumber = resultSet.getString("RoomNo");
	                    String floorNo = resultSet.getString("FloorNo");
	                    String roomCategory = resultSet.getString("RoomCategory");
	                    int totalItems = getTotalItems(GethostelId(hostelname), roomId);

	                    // Add row to the table model
	                    rows.add(new Object[]{roomId, roomNumber, floorNo, roomCategory, totalItems, GethostelId(hostelname)});
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching Rooms by RoomNo and HostelId: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}


 // to get total hostelsIds
    public int GethostelId(String name) {
    	
        try {
        	Connection connection = databaseConnector.connect();
            String selectHostelIdSql = "SELECT HostelId FROM Hostel WHERE Name = ?";

            try (PreparedStatement selectnamesStatement = connection.prepareStatement(selectHostelIdSql)) {
                selectnamesStatement.setString(1, name);
                ResultSet resultSet = selectnamesStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("HostelId");
                }
             }
            
        } catch ( SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving name of hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
 	return 0;

    }
// Helper method to get total items for a hostel from the database
   private static int getTotalItems(int HostelId,int roomId) {
       try {
    	   Connection connection = databaseConnector.connect();
           String selectRoomsSql = "SELECT COUNT(ItemId) AS NumberOfItems FROM AllocatedItems WHERE RoomId = ? AND HostelId = ?";
           try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
               selectRoomsStatement.setInt(1, roomId);
               selectRoomsStatement.setInt(2, HostelId);
               ResultSet resultSet = selectRoomsStatement.executeQuery();
               if (resultSet.next()) {
                   return resultSet.getInt("NumberOfItems");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return 0;
   }
   
   //Add Room information
   public void addRoomInfo(String hostelname,String  roomNo, String floorno, String roomCategory) {
	   try {
		   Connection connection = databaseConnector.connect();

           // Insert into Hostel table
           String roomSql = "INSERT INTO Rooms (RoomNo,HostelId, FloorNo, RoomCategory) VALUES (?, ?, ?, ?)";

           try (PreparedStatement hostelStatement = connection.prepareStatement(roomSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

               hostelStatement.setString (1, roomNo);
               hostelStatement.setInt(2, GethostelId(hostelname));
               hostelStatement.setString(3, floorno);
               hostelStatement.setString(4, roomCategory);

               int affectedHostelRows = hostelStatement.executeUpdate();
               if (affectedHostelRows > 0) {
                       int hostelId = GethostelId(hostelname);
                       updateTotalRooms(hostelId);

                       JOptionPane.showMessageDialog(null, "Room added successfully!");
                   }
           }
       } catch ( SQLException | NumberFormatException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error adding room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
   
// Helper method to get total rooms for a hostelRooms from the database
   private static int getTotalRooms(int hostelId) {
       try {
    	   Connection connection = databaseConnector.connect();
           String selectRoomsSql = "SELECT COUNT(RoomId) AS NumberOfRooms FROM Rooms WHERE HostelId = ?";
           try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
               selectRoomsStatement.setInt(1, hostelId);
               ResultSet resultSet = selectRoomsStatement.executeQuery();
               if (resultSet.next()) {
                   return resultSet.getInt("NumberOfRooms");
               }
           }
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       return 0;
   }
   
// Helper method to update total rooms for a hostelRooms from the database
   private static int updateTotalRooms(int hostelId) {
       try {
    	   Connection connection = databaseConnector.connect();
          // Now, use the obtained HostelId to select rooms
              String selectRoomsSql = "UPDATE HostelRooms SET TotalRooms = ? WHERE HostelId = ?";
              try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
                  selectRoomsStatement.setInt(1, getTotalRooms(hostelId));
                  selectRoomsStatement.setInt(2, hostelId);
                 // System.out.println(hostelId);
                  //System.out.println(getTotalRooms(hostelId));
                 int affectedHostelRoomRows = selectRoomsStatement.executeUpdate();
                  if(affectedHostelRoomRows>0) {
                	  //JOptionPane.showMessageDialog(null, "TotalRoom updated successfully.");
                  }
              }
              
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return 0;
   }
   
   //update Room table
   public void updateRoomInfo(String hostelname, int roomId, String  newRoomNo, String newFloorNo, String newRoomCategory) {
	    try {
	    	Connection connection = databaseConnector.connect();

	        // Update Rooms table based on RoomId
	        String updateSql = "UPDATE Rooms SET RoomNo = ?, FloorNo = ?, RoomCategory = ? WHERE RoomId = ? AND HostelId = ?";

	        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
	            updateStatement.setString (1, newRoomNo);
	            updateStatement.setString(2, newFloorNo);
	            updateStatement.setString(3, newRoomCategory);
	            updateStatement.setInt(4, roomId);
	            updateStatement.setInt(5, GethostelId(hostelname));

	            int affectedRows = updateStatement.executeUpdate();

	            if (affectedRows > 0) {
	                JOptionPane.showMessageDialog(null, "Room updated successfully.");
	            } else {
	                JOptionPane.showMessageDialog(null, "Updation failed.");
	            }
	        }
	    } catch ( SQLException | NumberFormatException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error updating room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  //delete room
   public void deleteRoomInfo(String hostelname, int roomId) {
	   
	    try {
	    	Connection connection = databaseConnector.connect();
	        // Delete from AllocatedItems table
	        String deleteAllocatedItemsSql = "DELETE FROM AllocatedItems WHERE RoomId = ? AND HostelId = ?";
	        try (PreparedStatement deleteAllocatedItemsStatement = connection.prepareStatement(deleteAllocatedItemsSql)) {
	            deleteAllocatedItemsStatement.setInt(1, roomId);
	            deleteAllocatedItemsStatement.setInt(2, GethostelId(hostelname));
	            deleteAllocatedItemsStatement.executeUpdate();
	        }

	        // Delete from Rooms table
	        String deleteRoomSql = "DELETE FROM Rooms WHERE RoomId = ? AND HostelId = ?";
	        try (PreparedStatement deleteRoomStatement = connection.prepareStatement(deleteRoomSql)) {
	            deleteRoomStatement.setInt(1, roomId);
	            deleteRoomStatement.setInt(2, GethostelId(hostelname));

	            int affectedRoomRows = deleteRoomStatement.executeUpdate();

	            if (affectedRoomRows > 0) {
	                int hostelId = GethostelId(hostelname);
	                updateTotalRooms(hostelId);
	                JOptionPane.showMessageDialog(null, "Room and associated items deleted successfully.");
	            } else {
	                JOptionPane.showMessageDialog(null, "Room not found or delete failed.");
	            }
	        }
	    } catch ( SQLException | NumberFormatException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error deleting room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
  
   public ObservableList<String> getRoomNumbers(String hostelname) {
	    ObservableList<String > roomNumbers = FXCollections.observableArrayList();

	    try {
	        int hostelId = GethostelId(hostelname);
	        if (hostelId != 0) {
	        	Connection connection = databaseConnector.connect();

	            String selectRoomsSql = "SELECT RoomNo FROM Rooms WHERE HostelId = ?";
	            try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
	                selectRoomsStatement.setInt(1, hostelId);

	                try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
	                    while (resultSet.next()) {
	                    	String  roomNo = resultSet.getString ("RoomNo");
	                        roomNumbers.add(roomNo);
	                    }
	                    return roomNumbers;
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error retrieving room numbers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return roomNumbers;
	}
   public ObservableList<String> getRoomcat(String hostelname) {
	    ObservableList<String> roomNumbers = FXCollections.observableArrayList();

	    try {
	        int hostelId = GethostelId(hostelname);
	        if (hostelId != 0) {
	        	Connection connection = databaseConnector.connect();

	            String selectRoomsSql = "SELECT RoomCategory FROM Rooms WHERE HostelId = ?";
	            try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
	                selectRoomsStatement.setInt(1, hostelId);

	                try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
	                    while (resultSet.next()) {
	                    	String roomNo = resultSet.getString("RoomCategory");
	                        roomNumbers.add(roomNo);
	                    }
	                    return roomNumbers;
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error retrieving room numbers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return roomNumbers;
	}
// Modify the RoomDB class to get the number of floors for a hostel
public int getNumberOfFloors(String hostelname) {
    try {
        int hostelId = GethostelId(hostelname);
        if (hostelId != 0) {
        	Connection connection = databaseConnector.connect();

            String selectHostelSql = "SELECT NumberOfFloors FROM Hostel WHERE HostelId = ?";
            try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql)) {
                selectHostelStatement.setInt(1, hostelId);

                try (ResultSet resultSet = selectHostelStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("NumberOfFloors");
                    }
                }
            }
        }
    } catch ( SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error retrieving number of floors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0;
}
public boolean isDuplicateRoomNumber(String hostelName, String roomNumber) {
    try {
        int hostelId = GethostelId(hostelName);
        if (hostelId != 0) {
        	Connection connection = databaseConnector.connect();

            String selectRoomsSql = "SELECT RoomId FROM Rooms WHERE HostelId = ? AND RoomNo = ?";
            try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
                selectRoomsStatement.setInt(1, hostelId);
                selectRoomsStatement.setString (2, roomNumber);

                try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        }
    } catch ( SQLException e) {
        e.printStackTrace();
    }
    return false;
}
public boolean isDuplicateRoomNumberEdit(String hostelname, String roomNo, int roomIdToExclude) {
    try {
        int hostelId = GethostelId(hostelname);
        if (hostelId != 0) {
        	Connection connection = databaseConnector.connect();

            String selectRoomsSql = "SELECT RoomNo FROM Rooms WHERE HostelId = ? AND RoomNo = ? AND RoomId != ?";
            try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
                selectRoomsStatement.setInt(1, hostelId);
                selectRoomsStatement.setString(2, roomNo);
                selectRoomsStatement.setInt(3, roomIdToExclude);

                try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
                    return resultSet.next(); // If there's a result, the room number is a duplicate
                }
            }
        }
    } catch ( SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error checking duplicate room number: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}
public boolean isRoomExists(String roomNo, String hostelName) {
    try {
    	Connection connection = databaseConnector.connect();
        String selectRoomSql = "SELECT COUNT(*) AS count FROM Rooms WHERE RoomNo = ? AND HostelName = ?";

        try (PreparedStatement selectRoomStatement = connection.prepareStatement(selectRoomSql)) {
            selectRoomStatement.setString (1, roomNo);
            selectRoomStatement.setString(2, hostelName);

            try (ResultSet resultSet = selectRoomStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
    } catch ( SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error checking room existence: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}  

}