package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SupplierItemsDB {
	private Connection connection;
	 private static DatabaseConnector databaseConnector = new DatabaseConnector();
	public ObservableList<Object[]> supplierItemsInfo(int id) {
		ObservableList<Object[]> rows = FXCollections.observableArrayList();
	// Populate the table model with data
       try {
    	   Connection connection = databaseConnector.connect();
           String selectSupplierItemsSql = "SELECT * FROM SupplierItems WHERE SupplierId = ?";
           String CheckInventorySql = "SELECT * FROM InventoryItems WHERE SuppId = ? AND SuppItemId = ?";
           try (PreparedStatement selectSupplierItemsStatement = connection.prepareStatement(selectSupplierItemsSql);
                ) {
               selectSupplierItemsStatement.setInt(1, id);
               ResultSet resultSet = selectSupplierItemsStatement.executeQuery();
               while (resultSet.next()) {
            	   int Itemid = resultSet.getInt("ItemId");
                   String name = resultSet.getString("ItemName");
                   String cat = resultSet.getString("ItemCategory");
                   float price = resultSet.getFloat("ItemPrice");
                   
                // Check Inventory for Item Status
                   try (PreparedStatement CheckInventoryStatement = connection.prepareStatement(CheckInventorySql)) {
                       CheckInventoryStatement.setInt(1, id);
                       CheckInventoryStatement.setInt(2, Itemid);
                       // Add row to the table model
                       String Status;
                       try (ResultSet CheckresultSet = CheckInventoryStatement.executeQuery()) {
                           if (CheckresultSet.next()) {
                               Status = "Unallocated";
                           } else {
                               Status = "Allocated";
                           }
                       }
                       //if you face issue that any column entry is being passed but not shown
                       // make sure that entries passing from backend to frontend are same. like here on front we have supplierid 
                       //on index 4 that is invisible so we have to mention it as well
                       rows.add(new Object[]{Itemid, name, cat, price, id, Status});
                   }     
               }
               return rows;
           }
       } catch ( SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error retrieving supplier items information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
	return rows;

   }
	public ObservableList<Object[]> searchInventoryByItemName(String itemName) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try (Connection connection = databaseConnector.connect()) {
	        String selectInventoryByNameSql = "SELECT si.ItemId, si.ItemName, si.ItemCategory, si.ItemPrice, si.SupplierId, " +
	                                          "CASE WHEN ii.SuppItemId IS NOT NULL AND ii.SuppId IS NOT NULL THEN 'Unallocated' ELSE 'Allocated' END AS Status " +
	                                          "FROM SupplierItems si " +
	                                          "LEFT JOIN InventoryItems ii ON si.ItemId = ii.SuppItemId AND si.SupplierId = ii.SuppId " +
	                                          "WHERE si.ItemName LIKE ?";

	        try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
	            selectInventoryByNameStatement.setString(1, "%" + itemName + "%");

	            try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int no = resultSet.getInt("ItemId");
	                    String name = resultSet.getString("ItemName");
	                    String category = resultSet.getString("ItemCategory");
	                    float price = resultSet.getFloat("ItemPrice");
	                    int supplierId = resultSet.getInt("SupplierId"); // Include the "SupplierId" column
	                    String status = resultSet.getString("Status");

	                    rows.add(new Object[]{no, name, price, category, supplierId, status});
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching Supplier Items by name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        databaseConnector.closeConnection();
	    }

	    return rows;
	}


	public ObservableList<Object[]> searchInventoryByCategory(String category) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try (Connection connection = databaseConnector.connect()) {
	        String selectInventoryByCategorySql = "SELECT si.ItemId, si.ItemName, si.ItemCategory, si.ItemPrice, si.SupplierId, " +
	                                              "CASE WHEN ii.SuppItemId IS NOT NULL AND ii.SuppId IS NOT NULL THEN 'Unallocated' ELSE 'Allocated' END AS Status " +
	                                              "FROM SupplierItems si " +
	                                              "LEFT JOIN InventoryItems ii ON si.ItemId = ii.SuppItemId AND si.SupplierId = ii.SuppId " +
	                                              "WHERE si.ItemCategory LIKE ?";

	        try (PreparedStatement selectInventoryByCategoryStatement = connection.prepareStatement(selectInventoryByCategorySql)) {
	            selectInventoryByCategoryStatement.setString(1, "%" + category + "%");

	            try (ResultSet resultSet = selectInventoryByCategoryStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int no = resultSet.getInt("ItemId");
	                    String name = resultSet.getString("ItemName");
	                    String itemCategory = resultSet.getString("ItemCategory");
	                    float price = resultSet.getFloat("ItemPrice");
	                    int supplierId = resultSet.getInt("SupplierId");
	                    String status = resultSet.getString("Status");

	                    rows.add(new Object[]{no, name, price, itemCategory, supplierId, status});
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching Supplier Items by category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    } finally {
	        databaseConnector.closeConnection();
	    }

	    return rows;
	}
  
	 //Add supplierItems information
	   public void addSupplierItemsInfo(Integer quantity,int id,String name,String category, float price) {
		  
		   int count = 0;
		try {
			Connection connection = databaseConnector.connect();

	           // Insert into Suppliers table
	           String supplierItemSql = "INSERT INTO SupplierItems (SupplierId, ItemName, ItemCategory, ItemPrice) VALUES (?, ?, ?, ?)";
	           String InventoryItemSql = "INSERT INTO InventoryItems (SuppItemId, SuppId,ItemName, ItemCategory) VALUES (?, ?, ?, ?)";

	           while(count<quantity) {
	        	   try (PreparedStatement supplierItemStatement = connection.prepareStatement(supplierItemSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

	        		    supplierItemStatement.setInt(1, id);
		        	    supplierItemStatement.setString(2, name);
		        	    supplierItemStatement.setString(3, category);
		        	    supplierItemStatement.setFloat(4, price);

		        	    int affectedRows = supplierItemStatement.executeUpdate();

		        	    if (affectedRows > 0) {
		        	        try (ResultSet generatedKeys = supplierItemStatement.getGeneratedKeys()) {
		        	            if (generatedKeys.next()) {
		        	                int suppItemId = generatedKeys.getInt(1);
		        	                try (PreparedStatement InventoryItemStatement = connection.prepareStatement(InventoryItemSql)) {
		        	                    InventoryItemStatement.setInt(1, suppItemId);
		        	                    InventoryItemStatement.setInt(2, id);
		        	                    InventoryItemStatement.setString(3, name);
		        	                    InventoryItemStatement.setString(4, category);

		        	                    int affectedInventoryItemsRows = InventoryItemStatement.executeUpdate();
		        	                    if (affectedInventoryItemsRows > 0) {
		        	                        count++;
		        	                    }
		        	                }
		        	            }
		        	        }
		        	    }
		        	}

	           }
	           if(count==quantity) {
	        	   JOptionPane.showMessageDialog(null, "Supplier Items added successfully!");
	           }
	       } catch ( SQLException | NumberFormatException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error adding Supplier Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
	   
	// Update supplier information
	   public void updateSupplierItemInfo(ObservableList<Object[]> selectedSupplier, int supid, String name, String category, float price) {
	     int num = 0;
	       try {
	    	   Connection connection = databaseConnector.connect();

	           // Update Supplier Item table
	           String updateSupplierItemSql = "UPDATE SupplierItems SET ItemName = ?, ItemCategory = ?, ItemPrice = ? OUTPUT INSERTED.ItemId WHERE SupplierId = ? AND ItemId = ?";
	           String InventoryItemSql = "UPDATE InventoryItems SET ItemName = ?, ItemCategory = ? WHERE SuppId = ? AND SuppItemId = ?";
               String CheckInventorySql = "SELECT * FROM InventoryItems WHERE SuppId = ? AND SuppItemId = ?";
	           for(int item = 0; item<selectedSupplier.size(); item++) {
	               String id = getProperty(selectedSupplier.get(item), 0);
	               try(PreparedStatement checkInventoryStatement = connection.prepareStatement(CheckInventorySql)){
	            	   checkInventoryStatement.setInt(1, supid);
	            	   checkInventoryStatement.setInt(2, Integer.parseInt(id));
	            	   
	            	   // Execute the check query and get the result
	            	   ResultSet result = checkInventoryStatement.executeQuery(); 
		               if(result.next()) {
		            	   try (PreparedStatement updateSupplierItemStatement = connection.prepareStatement(updateSupplierItemSql)) {
				               updateSupplierItemStatement.setString(1, name);
				               updateSupplierItemStatement.setString(2, category);
				               updateSupplierItemStatement.setFloat(3, price);
				               updateSupplierItemStatement.setInt(4, supid);
				               updateSupplierItemStatement.setInt(5, Integer.parseInt(id));

				               // Execute the update query and get the generated keys
				               ResultSet generatedKeys = updateSupplierItemStatement.executeQuery();

				               if (generatedKeys.next()) {
				                   // Get the generated ItemId
				                   int ItemId = generatedKeys.getInt("ItemId");

				                   // Prepare and execute the update query for InventoryItems table
				                   try (PreparedStatement updateInventoryItemStatement = connection.prepareStatement(InventoryItemSql)) {
				                       updateInventoryItemStatement.setString(1, name);
				                       updateInventoryItemStatement.setString(2, category);
				                       updateInventoryItemStatement.setInt(3, supid);
				                       updateInventoryItemStatement.setInt(4, ItemId);

				                       // Execute the update query for InventoryItems table
				                       int affectedInventoryItemRows = updateInventoryItemStatement.executeUpdate();

				                       if (affectedInventoryItemRows > 0) {
				                    	   if(selectedSupplier.size() == 1) {
				                    		   JOptionPane.showMessageDialog(null, "Supplier Item information updated successfully!");
				                    	   }
				                    	   else if(item == selectedSupplier.size()-1) {
				                    		   JOptionPane.showMessageDialog(null, "Supplier Items information updated successfully!");
				                    	   }
				                           
				                       } else {
				                           JOptionPane.showMessageDialog(null, "Inventory Item update failed.");
				                       }
				                   }
				               } else {
				                   JOptionPane.showMessageDialog(null, "Supplier Item update failed.");
				               }
				           }
		               }else {
		            	  num++;
		            	  if(num == 1)
		            	  {
		            		  JOptionPane.showMessageDialog(null, "Some Supplier Items updation failed. Items does not exist in inventory. They might have been allocated.");
		            	  }
		               }
	               }  
	           }
	           
	       } catch ( SQLException | NumberFormatException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error updating Supplier Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	   
	 //Delete supplier item
	   public void deleteSupplierItem(ObservableList<Object[]> supplierItems) {
		  int num = 0;
		try {
			Connection connection = databaseConnector.connect();
	
			// delete Supplier Item 
            String InventoryItemSql = "DELETE Top(1) FROM InventoryItems WHERE SuppId = ? AND SuppItemId = ?";
            String SupplierItemSql = "DELETE FROM SupplierItems WHERE SupplierId = ? AND ItemId = ?";

            for(int item = 0; item<supplierItems.size(); item++) {
            	int id = (Integer) supplierItems.get(item)[0];
            	int supplierId = (Integer) supplierItems.get(item)[4];
            	
            	try (PreparedStatement deleteInventoryItemStatement = connection.prepareStatement(InventoryItemSql)) {
    	        	
    	            deleteInventoryItemStatement.setInt(1, supplierId);
    	        	deleteInventoryItemStatement.setInt(2, id);

    	        	int affectedInventoryItemRows = deleteInventoryItemStatement.executeUpdate();
    	            
    	            if (affectedInventoryItemRows > 0) {
    	            	try (PreparedStatement deleteSupplierItemStatement = connection.prepareStatement(SupplierItemSql)) {
    	            		deleteSupplierItemStatement.setInt(1, supplierId);
    	 		            deleteSupplierItemStatement.setInt(2, id);
    	 		            
    	 		           int affectedSupplierItemRows = deleteSupplierItemStatement.executeUpdate();
    			            
    			            if(affectedSupplierItemRows>0) {
    			            	if(supplierItems.size()==1) {
    			            		JOptionPane.showMessageDialog(null, "Supplier Item deleted successfully!");
    			            	}
    			            	else if(item == supplierItems.size()-1) {
    			            		JOptionPane.showMessageDialog(null, "Supplier Items deleted successfully!");
    			            	  }
    			            	
    			            	}
    			            }

    	                
    	            } else {
    	            	num++;
    	            	if(supplierItems.size()==1) {
    	            		JOptionPane.showMessageDialog(null, "Deletion Failed. This item does not exist in inventory. It might be allocated.");
    	            	}
    	            	else if(num == 1) {
    	            		JOptionPane.showMessageDialog(null, "Deletion Failed. Some Items does not exist in inventory. They might be allocated.");
    	            	} 
    	            }
    	        }
            }
	        
	       
	       } catch ( SQLException | NumberFormatException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error deleting supplier Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	    private static <T> String getProperty(T[] array, int index) {
	        return array[index].toString();
	    }

}
