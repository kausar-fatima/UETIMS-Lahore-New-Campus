package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class allocatesdItemsDB {
    private final DatabaseConnector databaseConnector = new DatabaseConnector();
    //to show items
    public ObservableList<Object[]> GetAllocatedItem(String hostelname, int roomid) {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String allocatedSql = "SELECT * FROM AllocatedItems WHERE HostelId = (SELECT HostelId FROM Hostel WHERE Name = ?) AND RoomId =  ?";

            try (PreparedStatement selectAllocatedItemsStatement = connection.prepareStatement(allocatedSql)) {
                selectAllocatedItemsStatement.setString(1, hostelname);
                selectAllocatedItemsStatement.setInt(2, roomid);
                ResultSet inventorySet = selectAllocatedItemsStatement.executeQuery();

                while (inventorySet.next()) {
                    int id = inventorySet.getInt("ItemId"); 

                    String itemName = inventorySet.getString("ItemName");
                    String category = inventorySet.getString("ItemCategory");
                    int reqNo=inventorySet.getInt("ReqNo");

                    rows.add(new Object[]{id, itemName, category,reqNo});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving Inventory Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return rows;
    }

    //add
    public void addAllocatedItem(Integer SuppItemId,Integer SuppId,Integer ReqNo, String hostelName, int roomId, String itemName, String itemCategory, int quantity) {
	    //Connection connection = null;
	    int count = 0;

	    try {
	        // Get the hostelId using the hostelName
	        int hostelId = gethostelId(hostelName);

	        // Check if the hostelId is valid
	        if (hostelId == 0) {
	            //System.out.println("Invalid hostel information");
	            return;
	        }

	        Connection connection = databaseConnector.connect();

	        String insertAllocatedItemSql = "INSERT INTO AllocatedItems (HostelId, RoomId, ItemName, ItemCategory,ReqNo) VALUES (?, ?, ?, ?,?)";
	        try (PreparedStatement insertAllocatedItemStatement = connection.prepareStatement(insertAllocatedItemSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	        	
	        
	        while (count < quantity) {
	           
	        	

	                insertAllocatedItemStatement.setInt(1, hostelId);
	                insertAllocatedItemStatement.setInt(2, roomId);
	                insertAllocatedItemStatement.setString(3, itemName);
	                insertAllocatedItemStatement.setString(4, itemCategory);
	                insertAllocatedItemStatement.setObject(5,ReqNo);
	                
	               
	                int affectedRows = insertAllocatedItemStatement.executeUpdate();

	                if (affectedRows > 0) {
                        count++;
                    }
                }
	          	       

	        if (count == quantity) {
	            JOptionPane.showMessageDialog(null, "Allocated Items added successfully!");
	        }
	    } 
	    } 
	        catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error adding Allocated Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	    	databaseConnector.closeConnection();
	    }
	}
    
    //update
    public void updateAllocatedItemInfo(String hostelName, ObservableList<Object[]> selectedItems, String newItemName, String newItemCategory, int roomId) {
            int num = 0;
            int hostelId = gethostelId(hostelName);

            if (hostelId == 0) {
                //System.out.println("Invalid hostel information");
                return;
            }

            
            try (Connection connection = databaseConnector.connect()) {
            String allocatedItemSql = "UPDATE AllocatedItems SET ItemName = ?, ItemCategory = ? WHERE RoomId = ? AND ItemId = ? AND HostelId = ?";

            for(int item = 0; item<selectedItems.size();item++) {
                String id = getProperty(selectedItems.get(item), 0);
                String ReqNo = getProperty(selectedItems.get(item), 3);
                
                // Check if ReqNo is not null
                if (ReqNo != null &&  Integer.parseInt(ReqNo)!=0) {
                	num++;
                    if(num == 1) {
                    	// Show alert indicating that the item is uneditable
                        String alertMessage = "Some items allocated to Room through Requisition can not editable.";
                        JOptionPane.showMessageDialog(null, alertMessage);
                        return; // Exit the method
                    }
                }
                
            	try (PreparedStatement updateAllocatedItemStatement = connection.prepareStatement(allocatedItemSql)) {
                	
                    updateAllocatedItemStatement.setString(1, newItemName);
                    updateAllocatedItemStatement.setString(2, newItemCategory);
                    updateAllocatedItemStatement.setInt(3, roomId);
                    updateAllocatedItemStatement.setInt(4, Integer.parseInt(id));
                    updateAllocatedItemStatement.setInt(5, hostelId);

                    int affectedRows = updateAllocatedItemStatement.executeUpdate();

                    if (affectedRows > 0) {
                    	if(item == selectedItems.size()-1 && selectedItems.size()==1) {
                    		JOptionPane.showMessageDialog(null, "Allocated item information updated successfully!");
                    	}else if(item == selectedItems.size()-1) {
                    		JOptionPane.showMessageDialog(null, "Allocated items information updated successfully!");
                    	}
                    } else {
                        JOptionPane.showMessageDialog(null, "Allocated item update failed. Make sure the item exists for the specified room and hostel.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating Allocated Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //delete
    public void deleteAllocatedItem(Integer ReqNo, String hostelname, int roomId, ObservableList<Object[]> selectedItems) {
        int num = 0;
    	try (Connection connection = databaseConnector.connect()) {
        	for(int item = 0; item<selectedItems.size();item++) {
            	int itemId = Integer.parseInt(getProperty(selectedItems.get(item), 0));
            	Integer reqNo = Integer.parseInt(getProperty(selectedItems.get(item), 3));
            	// Check if ReqNo is not null
                if (reqNo != null&& reqNo!=0) {
                	num++;
                    if(num == 1) {
                    	// Show alert indicating that the item is not deletable
                        String alertMessage = "Some allocated items associated with RequisitionNo cannot be deleted.";
                        JOptionPane.showMessageDialog(null, alertMessage);
                        
                    }
                    continue;
                }

                String deleteSql = "DELETE FROM AllocatedItems WHERE HostelId = (SELECT HostelId FROM Hostel WHERE Name = ?) AND RoomId = ? AND ItemId = ?";

                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                    deleteStatement.setString(1, hostelname);
                    deleteStatement.setInt(2, roomId);
                    deleteStatement.setInt(3, itemId);

                    deleteStatement.executeUpdate();
                    if(selectedItems.size()==1) {
                    	JOptionPane.showMessageDialog(null, "Allocated item deleted successfully!");
                    }
                    else if(item == selectedItems.size()-1) {
                    	JOptionPane.showMessageDialog(null, "Allocated items deleted successfully!");
                    }
                    
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as per your application requirements
        }
    }
    
    //hostelId
    public int gethostelId(String name) {
        try (Connection connection = databaseConnector.connect()) {
            String selectHostelIdsSql = "SELECT HostelId FROM Hostel WHERE Name=?";

            try (PreparedStatement selectHostelIdsStatement = connection.prepareStatement(selectHostelIdsSql)) {
                selectHostelIdsStatement.setString(1, name);
                ResultSet resultSet = selectHostelIdsStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("HostelId");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving Id of hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    //roomId
    public int getroomId(int no) {
        try (Connection connection = databaseConnector.connect()) {
            String selectHostelIdsSql = "SELECT RoomId FROM Rooms WHERE RoomNo=?";

            try (PreparedStatement selectRoomIdsStatement = connection.prepareStatement(selectHostelIdsSql)) {
                selectRoomIdsStatement.setInt(1, no);
                ResultSet resultSet = selectRoomIdsStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("RoomId");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving Id of room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
    //request id
    public int getreId(int no) {
        try (Connection connection = databaseConnector.connect()) {
            String selectHostelIdsSql = "SELECT ReqNo FROM RequestItem WHERE ReqNo=?";

            try (PreparedStatement selectRoomIdsStatement = connection.prepareStatement(selectHostelIdsSql)) {
                selectRoomIdsStatement.setInt(1, no);
                ResultSet resultSet = selectRoomIdsStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("ReqNo");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving Id of requisition: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
    //search item by name
    public ObservableList<Object[]> searchaByItemName(String itemName) {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByNameSql = "SELECT * FROM AllocatedItems WHERE ItemName LIKE ?";

            try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
                selectInventoryByNameStatement.setString(1, "%" + itemName + "%");

                try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int no = resultSet.getInt("ItemId");
                        String name = resultSet.getString("ItemName");
                        String category = resultSet.getString("ItemCategory");
                       Object reqNo=resultSet.getObject("ReqNo");
                       // Check if reqNo is null and set it to 0
                       if (reqNo == null) {
                           reqNo = 0;
                       }
                        rows.add(new Object[]{no, name, category,reqNo});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching Items by name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }

        return rows;
    }
  //search item by Category
    public ObservableList<Object[]> searchaByCategory(String category) {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByCategorySql = "SELECT * FROM AllocatedItems WHERE ItemCategory LIKE ?";

            try (PreparedStatement selectInventoryByCategoryStatement = connection.prepareStatement(selectInventoryByCategorySql)) {
                selectInventoryByCategoryStatement.setString(1, "%" + category + "%");

                try (ResultSet resultSet = selectInventoryByCategoryStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int no = resultSet.getInt("ItemId");
                        String name = resultSet.getString("ItemName");
                        String itemCategory = resultSet.getString("ItemCategory");
                        Object reqNo = resultSet.getObject("ReqNo");
                        
                        // Check if reqNo is null and set it to 0
                        if (reqNo == null) {
                            reqNo = 0;
                        }
                        
                        rows.add(new Object[]{no, name, itemCategory, reqNo});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching Items by category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }

        return rows;
    }

    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }
}