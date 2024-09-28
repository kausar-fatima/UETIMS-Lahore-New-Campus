package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class requestDB {
	private Connection connection;
	int counting = 0;
	 private static DatabaseConnector databaseConnector = new DatabaseConnector();
	public ObservableList<Object[]> requestInfo() {
	
		ObservableList<Object[]> rows = FXCollections.observableArrayList();
		int count = 0;
	// Populate the table model with data
       try {
    	   Connection connection = databaseConnector.connect();
           String selectHostelSql = "SELECT * FROM RequestItem";
           String updateStatusSql = "UPDATE RequestItem SET Status = ? WHERE HostelName = ? AND RoomNo = ? AND ItemName = ? AND ItemCategory = ? AND ReqNo = ?";
           
           try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql);
                ResultSet resultSet = selectHostelStatement.executeQuery()) {

               while (resultSet.next()) {
                   int reqNo = resultSet.getInt("ReqNo");
                   String roomNo = resultSet.getString("RoomNo");
                   String hostelName = resultSet.getString("HostelName");
                   String itemName = resultSet.getString("ItemName");
                   String category = resultSet.getString("ItemCategory");
                   int quantity = resultSet.getInt("Quantity");
                   String description = resultSet.getString("Description");
                   Date reqDate = resultSet.getDate("RequestDate");
                   String status = resultSet.getString("Status");
                   statusUpdation();
                   if (status.equalsIgnoreCase("Inprogress")) {
                	    count = checkAvailability(itemName, category);
                	    if (count >= quantity) {
                	        try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusSql);
                	             PreparedStatement innerQueryStatement = connection.prepareStatement(selectHostelSql)) {

                	            updateStatusStatement.setString(1, "Inprogress(check Inventory)");
                	            updateStatusStatement.setString(2, hostelName);
                	            updateStatusStatement.setString(3, roomNo);
                	            updateStatusStatement.setString(4, itemName);
                	            updateStatusStatement.setString(5, category);
                	            updateStatusStatement.setInt(6, reqNo);
                	            
                	            int UpdateresultSet = updateStatusStatement.executeUpdate();

                	            if (UpdateresultSet>0) {
                	            	
                	               // System.out.println("Status updated");
                	            }
                	        }
                	    }
                	} 
                   // Add row to the table model
                   rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});      
               }
               return rows;
           }
       } catch ( SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error retrieving requisition information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
	return rows;
   }
	public void addRequestInfo(String hostelname,String roomNo, String itemName, String category, int quantity, String description, Date date, String status) {
	    int count = 0;
	    int num = 0;
        boolean check = false; 
	   // int reqNo = -1; // Initialize reqNo

	    try (Connection connection = databaseConnector.connect()) {
	        // Insert into request table
	        String requestSql = "INSERT INTO RequestItem (HostelName, RoomNo, ItemName, ItemCategory, Quantity, Description, RequestDate, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	        String selectInventoryItems = "SELECT * FROM InventoryItems WHERE ItemName = ? AND ItemCategory = ?";
	        String allocatedItemSql = "INSERT INTO AllocatedItems (SuppId,SuppItemId,RoomId, HostelId, ItemName, ItemCategory, ReqNo) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        String requestItemDelSql = "DELETE TOP(1) FROM InventoryItems WHERE ItemName = ? AND ItemCategory = ?";

	        try (PreparedStatement requestStatement = connection.prepareStatement(requestSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	            // Set parameters for the request statement
	            requestStatement.setString(1, hostelname);
	            requestStatement.setString(2, roomNo);
	            requestStatement.setString(3, itemName);
	            requestStatement.setString(4, category);
	            requestStatement.setInt(5, quantity);
	            requestStatement.setString(6, description);
	            requestStatement.setDate(7, (java.sql.Date) date);
	            requestStatement.setString(8, status);

	          

	           // if (affectedHostelRows > 0) {
	                if (status.equalsIgnoreCase("Accept")) {
	                   
	                   
	                    // Check the availability in the inventory
	                        
	                    count = checkAvailability(itemName, category);

	               

	                        if (count >= quantity) {
	                        	  connection.setAutoCommit(false);
	                        	  int affectedrequestRows = requestStatement.executeUpdate();
	                        	  if(affectedrequestRows>0) {
	                        	 try (ResultSet generatedKeys = requestStatement.getGeneratedKeys()) {
	  	                             if (generatedKeys.next()) {
	  	                            	int reqNo = generatedKeys.getInt(1);
             
	                            // Allocate items from the inventory
	                            while (num < quantity) {
	                                
	                            	try(PreparedStatement getInventoryStatement = connection.prepareStatement(selectInventoryItems)){
                                      	getInventoryStatement.setString(1, itemName);
                                      	getInventoryStatement.setString(2, category);
                                      	
                                      	try(ResultSet InventoryresultSet = getInventoryStatement.executeQuery()){
                                      		if(InventoryresultSet.next()) {
                                      			Object suppId = InventoryresultSet.getObject("SuppId");
                                      			Object suppItemId = InventoryresultSet.getObject("SuppItemId");
                                      			
                                      			try (PreparedStatement allocatedItemStatement = connection.prepareStatement(allocatedItemSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            	                                    int hostelid = gethostelId(hostelname);
            	                                    allocatedItemStatement.setObject(1, suppId);
            	                                    allocatedItemStatement.setObject(2, suppItemId);
            	                                    allocatedItemStatement.setInt(3, getroomId(roomNo, hostelid));
            	                                    allocatedItemStatement.setInt(4, gethostelId(hostelname));
            	                                    allocatedItemStatement.setString(5, itemName);
            	                                    allocatedItemStatement.setString(6, category);
            	                                   
            	                                    // Set ReqNo as a foreign key
            	                                    allocatedItemStatement.setInt(7, reqNo);

            	                                    ResultSet affectedAllocatedItemsRows = allocatedItemStatement.executeQuery();

            	                                    if (affectedAllocatedItemsRows.next()) {
            	                                        num++;
            	                                        // Delete allocated items from the inventory
            	    	                                try (PreparedStatement requestItemDelStatement = connection.prepareStatement(requestItemDelSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            	    	                                    
            	    	                                    requestItemDelStatement.setString(1, itemName);
            	    	                                    requestItemDelStatement.setString(2, category);

            	    	                                    int affectedrequestItemsDelRows = requestItemDelStatement.executeUpdate();

            	    	                                    // Add the request to the RequestItem table
            	    	                                    if (affectedrequestItemsDelRows > 0) {
            	    	                                          //  System.out.println(num + " " + itemName + " are extracted from Inventory");
            	    	                                            check = true;
            	    	                                            // Commit the changes
            	    	                                            connection.commit();           	    	                                        
            	    	                                    }else {
        	    	                                            //System.out.println(num + " " + itemName + " are not extracted from Inventory");
        	    	                                            break;      	    	                                            
        	    	                                        }
            	    	                                }
            	                                    } else {
            	                                        JOptionPane.showMessageDialog(null, "Sorry only " + num + " " + itemName + " are allocated from Inventory");
            	                                        break;
            	                                    }
            	                                }
                                      		}else {
                                      			JOptionPane.showMessageDialog(null, "Sorry unable to extract Id's for allocation from Inventory");
    	                                        break;
                                      		}
                                      	}
                                      }
	                                
	                            }

	  		                                }
	  		                            }
	  		                        
	                            
	                         catch (SQLException e) {
		                        // Rollback in case of any exception during allocation
		                        connection.rollback();
		                        throw e;
		                    } finally {
		                        // Always close the database connection
		                        connection.close();
		                    }
	                        }
	                        } else {
	                      
	                            
	                            //System.out.println(count);
	                            //System.out.println(num);
	                            JOptionPane.showMessageDialog(null, "There are " + count + " " + itemName + " existing in Inventory.");
	                           
	                        }
	                   
	                } else {
	                	 int affectedHosteRows = requestStatement.executeUpdate();

	 	                if (affectedHosteRows > 0) {
	 	                    JOptionPane.showMessageDialog(null, "Requisition added successfully");
	 	                }
	                }
	            
	        }
	    } catch (Exception e) {
	        // Handle exceptions
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error adding requisition: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    if(check) {
	    	JOptionPane.showMessageDialog(null, quantity + " " + itemName + " are allocated to Room " + roomNo + " of " + hostelname + " successfully");
	    }
	}
	   
	//update request
	   public void updateRequestInfo(int prevQuantity,String prevStatus,int id, String hostelname,String roomNo, String itemName,String category, int quantity, String description, Date date, String status) {

		   if(status.equalsIgnoreCase("Accept") && prevStatus.equalsIgnoreCase("Accept")) {
			    AcceptStatus(prevQuantity,id, hostelname, roomNo, itemName,category, quantity, description, date, status);
		   } else if(status.equalsIgnoreCase("Accept") && prevStatus.equalsIgnoreCase("Reject")){
		            	
			   completeAllocation(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
 	            	   
		   }else if(status.equalsIgnoreCase("Accept") && (prevStatus.equalsIgnoreCase("Inprogress")|| prevStatus.equalsIgnoreCase("Inprogress(check Inventory)"))) {
			   
			   completeAllocation(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
			   
		   }else if(status.equalsIgnoreCase("Reject") && prevStatus.equalsIgnoreCase("Accept")) {
			   
			   completeDeallocation(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
			   
		   }else if(status.equalsIgnoreCase("Inprogress") && prevStatus.equalsIgnoreCase("Accept")){
			   
			   completeDeallocation(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
			   
		   }else {
			   editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
		   }
		        
    }

	
	// to get hostelId
	    public int gethostelId(String name) {
	        try {
	        	 Connection connection = databaseConnector.connect();
	            String selectHostelIdsSql = "SELECT HostelId FROM Hostel WHERE Name=?";

	            try (PreparedStatement selectHostelIdsStatement = connection.prepareStatement(selectHostelIdsSql)) {
	            	selectHostelIdsStatement.setString(1, name);
	                ResultSet resultSet = selectHostelIdsStatement.executeQuery();
	                
	                if (resultSet.next()) {
	                	return resultSet.getInt("HostelId");
	                }
	                
	            }
	        } catch ( SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error retrieving Id of hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	 	return 0;

	    }
	    
	 // to get roomId
	    public int getroomId(String no,int hostelid) {
	        try {
	        	 Connection connection = databaseConnector.connect();
	            String selectHostelIdsSql = "SELECT RoomId FROM Rooms WHERE RoomNo=? AND HostelId=?";

	            try (PreparedStatement selectRoomIdsStatement = connection.prepareStatement(selectHostelIdsSql)) {
	            	selectRoomIdsStatement.setString(1, no);
	            	selectRoomIdsStatement.setInt(2, hostelid);
	                ResultSet resultSet = selectRoomIdsStatement.executeQuery();
	                
	                if (resultSet.next()) {
	                	return resultSet.getInt("RoomId");
	                }
	                
	            }
	        } catch ( SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error retrieving Id of room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	 	return 0;

	    }
	    
	 // to check items availability
	    public int checkAvailability(String name, String category) {
	    	
	        try {
	        	 Connection connection = databaseConnector.connect();
	            String requestItemSql = "SELECT Count(ItemId) AS totalItem FROM InventoryItems WHERE ItemName = ? AND ItemCategory = ?";

	            //while(count<quantity) {
	            	   try(PreparedStatement requestItemStatement = connection.prepareStatement(requestItemSql, PreparedStatement.RETURN_GENERATED_KEYS)){
	            		  
	            		   requestItemStatement.setString(1, name);
	    	               requestItemStatement.setString(2, category);
	    	               ResultSet resultSet = requestItemStatement.executeQuery();
	    	               if (resultSet.next()) {
	    	                   return resultSet.getInt("totalItem");
	    	               } else {
	    	                   return 0;
	    	               }
	            	   //}
	            }
	           
	            
	        } catch ( SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error retrieving count Inventory Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	 	
	        return 0;
	    }
	    // Check if a hostel exists
	    
        public boolean isHostelExists(String hostelName) {
	        try {
	        	 Connection connection = databaseConnector.connect();
	            String selectHostelSql = "SELECT COUNT(*) AS count FROM Hostel WHERE Name = ?";
	            try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql)) {
	                selectHostelStatement.setString(1, hostelName);

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

	    // Check if a room exists in a hostel
	    public boolean isRoomExists(String selectedRoomNo, String hostelName) {
	        try {
	            int hostelId = GethostelId(hostelName);
	            if (hostelId != 0) {
	            	 Connection connection = databaseConnector.connect();

	                String selectRoomsSql = "SELECT COUNT(*) AS count FROM Rooms WHERE HostelId = ? AND RoomNo = ?";
	                try (PreparedStatement selectRoomsStatement = connection.prepareStatement(selectRoomsSql)) {
	                    selectRoomsStatement.setInt(1, hostelId);
	                    selectRoomsStatement.setString(2, selectedRoomNo);

	                    try (ResultSet resultSet = selectRoomsStatement.executeQuery()) {
	                        if (resultSet.next()) {
	                            int count = resultSet.getInt("count");
	                            return count > 0;
	                        }
	                    }
	                }
	            }
	        } catch ( SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error checking room existence: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    // Get HostelId based on HostelName
	    
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
	            JOptionPane.showMessageDialog(null, "Error retrieving ID of hostel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return 0;
	    }
	   
	    
   // Update status
	    public void statusUpdation() {
	    	int count = 0;
			
		// check the table model with data
	       try {
	    	   Connection connection = databaseConnector.connect();
	           String selectHostelSql = "SELECT * FROM RequestItem";
	           String updateStatusSql = "UPDATE RequestItem SET Status = ? WHERE ReqNo = ?";
	           
	           try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql);
	                ResultSet resultSet = selectHostelStatement.executeQuery()) {

	               while (resultSet.next()) {
	                   int reqNo = resultSet.getInt("ReqNo");
	                   String itemName = resultSet.getString("ItemName");
	                   String category = resultSet.getString("ItemCategory");
	                   //String hostelName = resultSet.getString("HostelName");
	                   int quantity = resultSet.getInt("Quantity");             
	                   String status = resultSet.getString("Status");
	                   
	                   if (status.equalsIgnoreCase("Inprogress(check Inventory)")) {
	                	    count = checkAvailability(itemName, category);
	                	    if (count < quantity) {
	                	        try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusSql);
	                	             PreparedStatement innerQueryStatement = connection.prepareStatement(selectHostelSql)) {

	                	            updateStatusStatement.setString(1, "Inprogress");
	                	            
	                	            updateStatusStatement.setInt(2, reqNo);
	                	            
	                	            int UpdateresultSet = updateStatusStatement.executeUpdate();

	                	            if (UpdateresultSet>0) {
	                	            	
	                	                //System.out.println("Status updated");
	                	            }else {
	                	            	//System.out.println("Status not updated");
	                	            }
	                	        }
	                	    }
	                	}
	                   
	               }
	               
	           }
	       } catch (SQLException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error retrieving requisition information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	       }
		
	   }
	    


  // Updation complete requesst table
  private void editRequest(int id, String hostelname, String roomNo, String itemName,String category, int quantity, String description, Date date, String status) {  
	  try {
		  Connection connection = databaseConnector.connect();
	        
	        String updateRequestSql = "UPDATE RequestItem SET RoomNo = ?, HostelName = ?, ItemName = ?, ItemCategory = ?, Quantity = ?, Description=?, requestDate = ?, Status = ? WHERE ReqNo = ?";

	  try(PreparedStatement updateRequestStatement = connection.prepareStatement(updateRequestSql)){
		  updateRequestStatement.setString(1, roomNo);
          updateRequestStatement.setString(2, hostelname);
          updateRequestStatement.setString(3, itemName);
          updateRequestStatement.setString(4, category);
          updateRequestStatement.setInt(5, quantity);
          updateRequestStatement.setString(6, description);
          updateRequestStatement.setDate(7, (java.sql.Date) date);
          updateRequestStatement.setString(8, status);
          updateRequestStatement.setInt(9, id);
          int resultSet = updateRequestStatement.executeUpdate();
        if (resultSet>0) {
        	JOptionPane.showMessageDialog(null, "Requisition updated successfully");
        }else {
        	JOptionPane.showMessageDialog(null, "Error in updation");
        }
	  }
    }catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error updating requisition: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
  
//Add items to allocatedItems
private boolean allocateItems(int Itemcount, String roomNo, String hostelName, String itemName, String ItemCategory, int reqNo) {
   int num = 0;
   boolean check = false;
   
   try {
       Connection connection = databaseConnector.connect();

       String selectInventoryItems = "SELECT * From InventoryItems WHERE ItemName = ? AND ItemCategory = ?";
       String allocatedItemSql = "INSERT INTO AllocatedItems (SuppId,SuppItemId,ReqNo, RoomId, HostelId, ItemName, ItemCategory) VALUES (?, ?, ?, ?, ?, ?, ?)";
       int hostelId = gethostelId(hostelName); // Replace with the actual hostel name
       while (num < Itemcount) {
    	   try(PreparedStatement getInventoryStatement = connection.prepareStatement(selectInventoryItems)){
             	getInventoryStatement.setString(1, itemName);
             	getInventoryStatement.setString(2, ItemCategory);
             	
             	try(ResultSet InventoryresultSet = getInventoryStatement.executeQuery()){
             		if(InventoryresultSet.next()) {
             			Object suppId = InventoryresultSet.getObject("SuppId");
             			Object suppItemId = InventoryresultSet.getObject("SuppItemId");
             			
             			try (PreparedStatement allocatedItemStatement = connection.prepareStatement(allocatedItemSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            allocatedItemStatement.setObject(1, suppId);
                            allocatedItemStatement.setObject(2, suppItemId);
            
             				allocatedItemStatement.setInt(3, reqNo);
                            allocatedItemStatement.setInt(4, getroomId(roomNo,hostelId));
                            allocatedItemStatement.setInt(5, gethostelId(hostelName));
                            allocatedItemStatement.setString(6, itemName);
                            allocatedItemStatement.setString(7, ItemCategory);

                            ResultSet affectedAllocatedItemsRows = allocatedItemStatement.executeQuery();
                            if (affectedAllocatedItemsRows.next()) {
                                num++;
                                check = ExtractFromInventory(roomNo, hostelName, itemName, ItemCategory);
                            } else {
                                JOptionPane.showMessageDialog(null, "There are only " + num + " " + itemName + " exist in Inventory");
                                break;
                            }
                        }
             			
                    }else {
                    	JOptionPane.showMessageDialog(null, "Sorry unable to extract Id's for allocation from Inventory");
                        break;
                    }
             			}
             		} 
       }
   }catch (Exception e) {
       e.printStackTrace();
       JOptionPane.showMessageDialog(null, "Error in allocation of Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
   } finally {
       if (connection != null) {
           try {
               connection.close();
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
   }
return check;
   
}
  
  // Remove Items from Inventory Items
  private boolean ExtractFromInventory(String roomNo, String hostelname, String Item, String category) {
	  try {
		  Connection connection = databaseConnector.connect();
	        
	        String requestItemDelSql = "DELETE TOP (1) FROM InventoryItems WHERE ItemName = ? AND ItemCategory = ?";
	        
	        try(PreparedStatement requestItemDelStatement = connection.prepareStatement(requestItemDelSql, PreparedStatement.RETURN_GENERATED_KEYS)){
         	   requestItemDelStatement.setString(1, Item);
         	   requestItemDelStatement.setString(2, category);
                int affectedrequestItemsDelRows = requestItemDelStatement.executeUpdate();
                if(affectedrequestItemsDelRows>0) {
             	   
             	   
    	             return true;
                }else {
                	JOptionPane.showMessageDialog(null, "Error in extraction of Items from Inventory");
                    return false;
                }
            }
	  }catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error in extraction of Items from Inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	return false;
  }
  
  // Check allocatedItems to do deallocation
  private int checkAllocatedItems(String item, String category, String hostelname, String roomNo) {
	  try {
		  Connection connection = databaseConnector.connect();
	        
	        String allocatedItemSql = "SELECT Count(ItemId) AS totalItem FROM AllocatedItems WHERE ItemName = ? AND ItemCategory = ? And HostelId = ? AND RoomId = ?";
	        int hostelId = gethostelId(hostelname); // Replace with the actual hostel name
	        try(PreparedStatement allocatedItemStatement = connection.prepareStatement(allocatedItemSql, PreparedStatement.RETURN_GENERATED_KEYS)){
	        	allocatedItemStatement.setString(1, item);
	        	allocatedItemStatement.setString(2, category);
	        	allocatedItemStatement.setInt(3, gethostelId(hostelname));
	        	allocatedItemStatement.setInt(4, getroomId(roomNo,hostelId));
	        	ResultSet resultSet = allocatedItemStatement.executeQuery();
	               if (resultSet.next()) {
	                   return resultSet.getInt("totalItem");
	               } else {
	                   return 0;
	               }
          }
	  }catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error in checking allocated Items " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	return 0;
  }
  
  // deallocate Items to Inventory
  private boolean DeallocateToInventory(int Itemcount, String itemName, String category, String hostelName, String roomNo, int Id) {
    int num = 0;
	boolean check = false;
	  try {
		  Connection connection = databaseConnector.connect();
	        
	        String dellocatedItemSql = "INSERT INTO InventoryItems (SuppId, SuppItemId, ItemName,ItemCategory) VALUES (?, ?, ?, ?)";
	        String selectAllocatedItems = "SELECT * FROM AllocatedItems WHERE ItemName = ? AND ItemCategory = ? and ReqNo = ?";
	        
	        while(num<Itemcount) {
	        	try(PreparedStatement getallocatedStatement = connection.prepareStatement(selectAllocatedItems)){
	             	getallocatedStatement.setString(1, itemName);
	             	
	             	getallocatedStatement.setString(2, category);
	             	getallocatedStatement.setInt(3, Id);
	             	
	             	try(ResultSet allocatedresultSet = getallocatedStatement.executeQuery()){
	             		if(allocatedresultSet.next()) {
	             			Object suppId = allocatedresultSet.getObject("SuppId");
	             			Object suppItemId = allocatedresultSet.getObject("SuppItemId");

	        	try(PreparedStatement dellocatedItemStatement = connection.prepareStatement(dellocatedItemSql, PreparedStatement.RETURN_GENERATED_KEYS)){
		         	  dellocatedItemStatement.setObject(1, suppId);
		         	  dellocatedItemStatement.setObject(2, suppItemId);
	        		  dellocatedItemStatement.setString(3, itemName);
		         	  dellocatedItemStatement.setString(4, category);
		              ResultSet affectedallocatedItemsRows = dellocatedItemStatement.executeQuery();
		            if(affectedallocatedItemsRows.next()) {
		            	
			         	check = RemoveFromAllocatedItems(Itemcount, itemName, category, hostelName,roomNo, Id);
			         	   
		         	   num++;
		         	   counting = num;
		            }else {
		       	   JOptionPane.showMessageDialog(null,"Added Items to Inventory but not deallocated from room" );
		       	   break;
		          }
	        	}
	          }else {
	        	  JOptionPane.showMessageDialog(null,"Only "+num+" "+itemName+ " exist in allocated Items against the requisition "+Id );
	        	  break;
	          }
	        }
	      }
	    }
	  }catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error in dellocation of Items to Inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	  return check;
  }
	
  // Deallocation of Items from allocatedItems
  private boolean RemoveFromAllocatedItems(int Itemcount, String item, String category, String hostelname, String roomNo,int id) {
	  try {
		  Connection connection = databaseConnector.connect();
	        
	        String allocatedItemDelSql = "DELETE TOP (1) FROM AllocatedItems WHERE ItemName = ? AND ItemCategory = ? AND HostelId = ? AND RoomId = ? AND ReqNo=?";
	        int hostelId = gethostelId(hostelname); // Replace with the actual hostel name
	        try(PreparedStatement allocatedItemDelStatement = connection.prepareStatement(allocatedItemDelSql, PreparedStatement.RETURN_GENERATED_KEYS)){

	        	allocatedItemDelStatement.setString(1, item);
	        	allocatedItemDelStatement.setString(2, category);
	        	allocatedItemDelStatement.setInt(3, gethostelId(hostelname));
	        	allocatedItemDelStatement.setInt(4, getroomId(roomNo,hostelId));
	        	allocatedItemDelStatement.setInt(5, id);
              int affectedallocatedItemsDelRows = allocatedItemDelStatement.executeUpdate();
              if(affectedallocatedItemsDelRows>0) {
           	   
           	   
  	             return true;
              }else {
              	JOptionPane.showMessageDialog(null, "Error in extraction of Items from allocated Items");
                 return false;
              }
          }
	  }catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error in extraction of Items from allocated Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	return false;
  }
  
  // Case: if previous and current status are "Accept"
  private void AcceptStatus(int prevQuantity,int id, String hostelname, String roomNo, String itemName,String category, int quantity, String description, Date date, String status) {
	  int Itemcount = 0;
	  int count = 0;
      
	  if(prevQuantity == quantity ) {
		  editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
	  }else if(prevQuantity<quantity) {
		  Itemcount = quantity - prevQuantity;
		  count = checkAvailability(itemName, category);
		  
		  if(count>=Itemcount) {
			  
		     boolean check = allocateItems(Itemcount, roomNo, hostelname, itemName, category,id);
			 
			 if(check) {
				 JOptionPane.showMessageDialog(null, Itemcount + " " + itemName + " are allocated to Room " + roomNo + " of " + hostelname + " successfully");
				 editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
			 }
		  }else {
				 JOptionPane.showMessageDialog(null, "Only "+count+" "+itemName+" are in Inventory");
				 editRequest(id, hostelname, roomNo, itemName, category, prevQuantity, description, date, status);
			 }
		  
	  }else if(prevQuantity>quantity) {//if current entered quantity is less than already entered quantity
		  Itemcount = prevQuantity - quantity;
		  
		  count = checkAllocatedItems(itemName, category, hostelname, roomNo);
		  
		  if(count>=Itemcount) {
			  
			  boolean check =  DeallocateToInventory(Itemcount, itemName, category,hostelname,roomNo,id);
			  if(check) {
				  JOptionPane.showMessageDialog(null, counting+" "+itemName+" deallocated from Room "+roomNo+" of "+hostelname+" successfully");
				  editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
			  }
			  
		  }else {
				 JOptionPane.showMessageDialog(null, "Only "+count+" "+itemName+" are in allocated Items");
				 editRequest(id, hostelname, roomNo, itemName, category, prevQuantity, description, date, status);
			 }
	  }            
  }
  
//Case: if previous status is decline or progress and current status is accept
private void completeAllocation(int id, String hostelname, String roomNo, String itemName, String category, int quantity, String description, Date date, String status) {
   int count = 0;
   count = checkAvailability(itemName, category);

   if (count >= quantity) {
       boolean check = allocateItems(quantity, roomNo, hostelname, itemName, category, id);
       
       if(check) {
    	   JOptionPane.showMessageDialog(null, quantity + " " + itemName + " are allocated to Room " + roomNo + " of " + hostelname + " successfully");
			  editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
		  }
   } else {
       JOptionPane.showMessageDialog(null, "Only " + count + " " + itemName + " available in Inventory");
      
   }
}
  
  // Case: if previous status is accept and current status is decline or progress
  private void completeDeallocation(int id, String hostelname, String roomNo, String itemName,String category, int quantity, String description, Date date, String status) {
	  
	  int count = 0;
	  count = checkAllocatedItems(itemName, category, hostelname, roomNo);
	  
	  if(count>quantity) {
		  
		  boolean check = DeallocateToInventory(quantity, itemName, category, hostelname, roomNo, id);
		  if(check) {
			  JOptionPane.showMessageDialog(null, counting+" "+itemName+" deallocated from 	Room "+roomNo+" of "+hostelname+" successfully");
			  int allocated = count- quantity;
			  editRequest(id, hostelname, roomNo, itemName, category, allocated, description, date, "Accept");
		  }
		   
	  	}
		else if(count==quantity) {
		  
		  boolean check = DeallocateToInventory(quantity, itemName, category, hostelname, roomNo, id);
		  if(check) {
			  
			  JOptionPane.showMessageDialog(null, counting+" "+itemName+" deallocated from 	Room "+roomNo+" of "+hostelname+" successfully");
			  editRequest(id, hostelname, roomNo, itemName, category, counting, description, date, status);
		  }
		   
	  }
	  
//		if(count>=quantity) {
//				  
//				  boolean check = DeallocateToInventory(quantity, itemName, category, hostelname, roomNo, id);
//				  if(check) {
//					  
//					  JOptionPane.showMessageDialog(null, counting+" "+itemName+" deallocated from 	Room "+roomNo+" of "+hostelname+" successfully");
//					  editRequest(id, hostelname, roomNo, itemName, category, counting, description, date, status);
//				  }
//				   
//			  }else if(count==quantity) {
//				  
//				  boolean check = DeallocateToInventory(quantity, itemName, category, hostelname, roomNo, id);
//				  if(check) {
//					  
//					  JOptionPane.showMessageDialog(null, counting+" "+itemName+" deallocated from 	Room "+roomNo+" of "+hostelname+" successfully");
//					  editRequest(id, hostelname, roomNo, itemName, category, counting, description, date, status);
//				  }
//			  }
//		  else {
//			  JOptionPane.showMessageDialog(null, "Only "+count+" "+itemName+" available in allocated Items");
//			  editRequest(id, hostelname, roomNo, itemName, category, quantity, description, date, status);
//		  }
  }
  
  // checking for deletion of not existing rooms or hostels
  public Boolean checkDeletion(int id) {
	    try {
	        Connection connection = databaseConnector.connect();
	        String checkDeletionSql = "SELECT * FROM AllocatedItems WHERE ReqNo = ?";
	        
	        try (PreparedStatement checkDeletionStatement = connection.prepareStatement(checkDeletionSql)) {
	            checkDeletionStatement.setInt(1, id);
	            try (ResultSet resultSet = checkDeletionStatement.executeQuery()) {
	                // Check if there are any rows in the result set
	                if (resultSet.next()) {
	                    // If there are rows, return true
	                    return true;
	                } else {
	                    // If there are no rows, return false
	                    return false;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return false;
	}

//delete request
  public void deleteRequestInfo(int reqNo) {
	    try {
	    	 Connection connection = databaseConnector.connect();

	        // Delete from Requests table
	        String deleteRequestSql = "DELETE FROM RequestItem WHERE ReqNo = ?";

	        try (PreparedStatement deleteRequestStatement = connection.prepareStatement(deleteRequestSql)) {
	            deleteRequestStatement.setInt(1, reqNo);

	            int affectedRequestRows = deleteRequestStatement.executeUpdate();

	            if (affectedRequestRows > 0) {
	                JOptionPane.showMessageDialog(null, "Requisition deleted successfully!");
	        }else {
	        	JOptionPane.showMessageDialog(null, "Sorry Requisition is not deleted!");
	        	}
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error deleting requisition: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
  public ObservableList<Object[]> searchByHostel(String Hostel) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	 Connection connection = databaseConnector.connect();

	        String selectInventoryByNameSql = "SELECT * FROM RequestItem WHERE HostelName LIKE ?";

	        try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
	            selectInventoryByNameStatement.setString(1, "%" + Hostel + "%");

	            try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
	                while (resultSet.next()) {
	                	int reqNo = resultSet.getInt("ReqNo");
	                	 String roomNo = resultSet.getString("RoomNo");
	                    String hostelName = resultSet.getString("HostelName");
	                    String itemName = resultSet.getString("ItemName");
	                    String category = resultSet.getString("ItemCategory");
	                    int quantity = resultSet.getInt("Quantity");
	                    String description = resultSet.getString("Description");
	                    Date reqDate = resultSet.getDate("RequestDate");
	                    String status = resultSet.getString("Status");

	                    rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}
public ObservableList<Object[]> searchByRoomNo(String Room) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	 Connection connection = databaseConnector.connect();

	        String selectInventoryByNameSql = "SELECT * FROM RequestItem WHERE RoomNo LIKE ?";

	        try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
	            selectInventoryByNameStatement.setString(1, "%" + Room + "%");

	            try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
	                while (resultSet.next()) {
	                	int reqNo = resultSet.getInt("ReqNo");
	                    String roomNo = resultSet.getString("RoomNo");
	                    String hostelName = resultSet.getString("HostelName");
	                    String itemName = resultSet.getString("ItemName");
	                    String category = resultSet.getString("ItemCategory");
	                    int quantity = resultSet.getInt("Quantity");
	                    String description = resultSet.getString("Description");
	                    Date reqDate = resultSet.getDate("RequestDate");
	                    String status = resultSet.getString("Status");

	                    rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}
public ObservableList<Object[]> searchByItemCategory(String Category) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	 Connection connection = databaseConnector.connect();

	        String selectInventoryByNameSql = "SELECT * FROM RequestItem WHERE ItemCategory LIKE ?";

	        try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
	            selectInventoryByNameStatement.setString(1, "%" + Category + "%");

	            try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
	                while (resultSet.next()) {
	                	int reqNo = resultSet.getInt("ReqNo");
	                	 String roomNo = resultSet.getString("RoomNo");
	                    String hostelName = resultSet.getString("HostelName");
	                    String itemName = resultSet.getString("ItemName");
	                    String category = resultSet.getString("ItemCategory");
	                    int quantity = resultSet.getInt("Quantity");
	                    String description = resultSet.getString("Description");
	                    Date reqDate = resultSet.getDate("RequestDate");
	                    String status = resultSet.getString("Status");

	                    rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});
	                }
	            }
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}
public ObservableList<Object[]> searchByItems(String items) {
ObservableList<Object[]> rows = FXCollections.observableArrayList();

try {
	 Connection connection = databaseConnector.connect();

    String selectInventoryByNameSql = "SELECT * FROM RequestItem WHERE ItemName LIKE ?";

    try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
        selectInventoryByNameStatement.setString(1, "%" + items + "%");

        try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
            while (resultSet.next()) {
            	int reqNo = resultSet.getInt("ReqNo");
            	 String roomNo = resultSet.getString("RoomNo");
                String hostelName = resultSet.getString("HostelName");
                String itemName = resultSet.getString("ItemName");
                String category = resultSet.getString("ItemCategory");
                int quantity = resultSet.getInt("Quantity");
                String description = resultSet.getString("Description");
                Date reqDate = resultSet.getDate("RequestDate");
                String status = resultSet.getString("Status");

                rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});
            }
        }
    }
} catch ( SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

return rows;
}
public ObservableList<Object[]> searchByStatus(String Status) {
ObservableList<Object[]> rows = FXCollections.observableArrayList();

try {
	 Connection connection = databaseConnector.connect();

String selectInventoryByNameSql = "SELECT * FROM RequestItem WHERE Status LIKE ?";

try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
    selectInventoryByNameStatement.setString(1, "%" + Status + "%");

    try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
        while (resultSet.next()) {
        	int reqNo = resultSet.getInt("ReqNo");
        	 String roomNo = resultSet.getString("RoomNo");
            String hostelName = resultSet.getString("HostelName");
            String itemName = resultSet.getString("ItemName");
            String category = resultSet.getString("ItemCategory");
            int quantity = resultSet.getInt("Quantity");
            String description = resultSet.getString("Description");
            Date reqDate = resultSet.getDate("RequestDate");
            String status = resultSet.getString("Status");

            rows.add(new Object[]{reqNo, hostelName, roomNo, itemName, category, quantity, status, reqDate,description});
        }
    }
}
} catch ( SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(null, "Error searching requisition by : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

return rows;
}
}