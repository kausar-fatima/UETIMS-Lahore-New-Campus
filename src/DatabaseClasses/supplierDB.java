package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class supplierDB {
	private Connection connection;
	 private static DatabaseConnector databaseConnector = new DatabaseConnector();
	 
	public ObservableList<Object[]> supplierInfo() {
    	ObservableList<Object[]> rows = FXCollections.observableArrayList();
	// Populate the table model with data
       try {
    	   Connection connection = databaseConnector.connect();
           String selectHostelSql = "SELECT * FROM Suppliers";
           
           try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql);
                ResultSet resultSet = selectHostelStatement.executeQuery()) {

               while (resultSet.next()) {
            	   int supplierId=resultSet.getInt("SupplierId");
                   String name = resultSet.getString("Name");
                   String billno = resultSet.getString("BillNo");
                   String deliveryDate = resultSet.getString("DeliveredDate");
                   int itemquantity = getSuppliersItemsQuantity(resultSet.getInt("SupplierId"));
                   float totalPrice = getSuppliersItemsPrice(resultSet.getInt("SupplierId"));
                   // Add row to the table model
                   
                   rows.add(new Object[]{supplierId,name, billno, deliveryDate,itemquantity, totalPrice});
                   
               }
               return rows;
           }
       } catch ( SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Error retrieving supplier information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
	return rows;

   }
	public ObservableList<Object[]> searchBySupplierName(String supplierName) {
	    ObservableList<Object[]> rows = FXCollections.observableArrayList();

	    try {
	    	Connection connection = databaseConnector.connect();

	        String searchSupplierSql = "SELECT * FROM Suppliers WHERE Name LIKE ?";
	        try (PreparedStatement searchSupplierStatement = connection.prepareStatement(searchSupplierSql)) {
	            searchSupplierStatement.setString(1, "%" + supplierName + "%");
	            ResultSet resultSet = searchSupplierStatement.executeQuery();

	            while (resultSet.next()) {
	                int supplierId = resultSet.getInt("SupplierId");
	                String name = resultSet.getString("Name");
	                String billno = resultSet.getString("BillNo");
	                String deliveryDate = resultSet.getString("DeliveredDate");
	                int itemquantity = getSuppliersItemsQuantity(supplierId);
	                float totalPrice = getSuppliersItemsPrice(supplierId);

	                rows.add(new Object[]{supplierId, name, billno, deliveryDate, itemquantity, totalPrice});
	            }

	            return rows;
	        }
	    } catch ( SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error searching supplier by name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    return rows;
	}

	// Helper method to get total rooms for a hostel from the database
	   public static int getSuppliersItemsQuantity(int supplierId) {
	       try {
	    	   Connection connection = databaseConnector.connect();
	        // Extract totalrooms using COUNT query
	           String selectTotalRoomsQuery = "SELECT COUNT(ItemId) AS Itemquantity FROM SupplierItems WHERE SupplierId = ?";
	           try (PreparedStatement selectTotalRoomsStatement = connection.prepareStatement(selectTotalRoomsQuery)) {
	               selectTotalRoomsStatement.setInt(1, supplierId);
	               ResultSet totalRoomsResult = selectTotalRoomsStatement.executeQuery();

	               int totalquantity = 0;
	               if (totalRoomsResult.next()) {
	                   totalquantity = totalRoomsResult.getInt("Itemquantity");
	               }

	               return totalquantity;
	               }
	           
	       } catch ( SQLException e) {
	           e.printStackTrace();
	       }
	       return 0;
	   }
	   
	// Helper method to get total rooms for a hostel from the database
	   public static float getSuppliersItemsPrice(int supplierId) {
	       try {
	    	   Connection connection = databaseConnector.connect();
	        // Extract totalrooms using COUNT query
	           String selectTotalPriceQuery = "SELECT SUM(ItemPrice) AS ItemsPrice FROM SupplierItems WHERE SupplierId = ?";
	           try (PreparedStatement selectTotalPriceStatement = connection.prepareStatement(selectTotalPriceQuery)) {
	               selectTotalPriceStatement.setInt(1, supplierId);
	               ResultSet totalPriceResult = selectTotalPriceStatement.executeQuery();

	               float totalprice = 0;
	               if (totalPriceResult.next()) {
	                   totalprice = totalPriceResult.getFloat("ItemsPrice");
	               }

	               return totalprice;
	               }
	           
	       } catch ( SQLException e) {
	           e.printStackTrace();
	       }
	       return 0;
	   }
	  
	   
	 //Add supplier information
	   public void addSupplierInfo(String name,String billNo, String deliveredDate) {
		  
		try {
			Connection connection = databaseConnector.connect();
	           // Insert into Suppliers table
	           String supplierSql = "INSERT INTO Suppliers (Name, BillNo, DeliveredDate) VALUES (?, ?, ?)";

	           try (PreparedStatement supplierStatement = connection.prepareStatement(supplierSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

	               supplierStatement.setString(1, name);
	               supplierStatement.setString(2, billNo);
	               supplierStatement.setString(3, deliveredDate);

	               int affectedSupplierRows = supplierStatement.executeUpdate();

	                       if (affectedSupplierRows > 0) {
	                           JOptionPane.showMessageDialog(null, "Supplier added successfully!");
	                       }

	           }
	       } catch ( SQLException | NumberFormatException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error adding supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
	   
	   public void updateSupplierInfo(int supplierId, String name, String billNo, String deliveredDate) {
		   
		    try {
		    	Connection connection = databaseConnector.connect();

		        // Update Supplier table
		        String updateSupplierSql = "UPDATE Suppliers SET Name = ?, BillNo = ?, DeliveredDate = ? WHERE SupplierId = ?";

		        try (PreparedStatement updateSupplierStatement = connection.prepareStatement(updateSupplierSql)) {
		            updateSupplierStatement.setString(1, name);
		            updateSupplierStatement.setString(2, billNo);
		            updateSupplierStatement.setString(3, deliveredDate);
		            updateSupplierStatement.setInt(4, supplierId);

		            int affectedSupplierRows = updateSupplierStatement.executeUpdate();

		            if (affectedSupplierRows > 0) {
		                JOptionPane.showMessageDialog(null, "Supplier information updated successfully!");
		            } else {
		                JOptionPane.showMessageDialog(null, "Update failed.");
		            }
		        }
		    } catch ( SQLException | NumberFormatException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error updating supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	   
	 //DEL supplier information WITHOUT UNIQUENESS OF BILL NO
	   public boolean deleteSupplier(int supplierId) {
		 
		    try {
		    	Connection connection = databaseConnector.connect();
		        // Delete Supplier Item 
		        String InventoryItemSql = "DELETE FROM InventoryItems WHERE SuppId = ?";
		        String SupplierItemSql = "DELETE FROM SupplierItems WHERE SupplierId = ?";
		        String SuppliersSql = "DELETE FROM Suppliers WHERE SupplierId = ?";
                boolean check = doesSuppIdExist(supplierId);
		        if(!check) {
		        	//connection.setAutoCommit(true);
		        	try (PreparedStatement deleteInventoryItemStatement = connection.prepareStatement(InventoryItemSql)) {
			            deleteInventoryItemStatement.setInt(1, supplierId);

			            int affectedInventoryItemRows = deleteInventoryItemStatement.executeUpdate();

			            if (affectedInventoryItemRows > 0) {
			                try (PreparedStatement deleteSupplierItemStatement = connection.prepareStatement(SupplierItemSql)) {
			                    deleteSupplierItemStatement.setInt(1, supplierId);

			                    int affectedSupplierItemRows = deleteSupplierItemStatement.executeUpdate();

			                    if (affectedSupplierItemRows > 0) {
			                        try (PreparedStatement deleteSupplierStatement = connection.prepareStatement(SuppliersSql)) {
			                            deleteSupplierStatement.setInt(1, supplierId);

			                            int affectedSupplierRows = deleteSupplierStatement.executeUpdate();

			                            if (affectedSupplierRows > 0) {
			                            	//connection.commit();
			                                JOptionPane.showMessageDialog(null, "Supplier deleted successfully!");
			                                return true;
			                            } else {
			                                JOptionPane.showMessageDialog(null, "Deletion failed.");
			                                return false;
			                            }
			                        }
			                    } else {
			                        try (PreparedStatement deleteSupplierStatement = connection.prepareStatement(SuppliersSql)) {
			                            deleteSupplierStatement.setInt(1, supplierId);

			                            int affectedSupplierRows = deleteSupplierStatement.executeUpdate();

			                            if (affectedSupplierRows > 0) {
			                            	//connection.commit();
			                                JOptionPane.showMessageDialog(null, "Supplier deleted successfully!");
			                                return true;
			                            } else {
			                                JOptionPane.showMessageDialog(null, "Deletion failed.");
			                                return false;
			                            }
			                        }
			                    }
			                }
			            } else {
			                try (PreparedStatement deleteSupplierStatement = connection.prepareStatement(SuppliersSql)) {
			                    deleteSupplierStatement.setInt(1, supplierId);

			                    int affectedSupplierRows = deleteSupplierStatement.executeUpdate();

			                    if (affectedSupplierRows > 0) {
			                        JOptionPane.showMessageDialog(null, "Supplier deleted successfully!");
			                        return true;
			                    } else {
			                        JOptionPane.showMessageDialog(null, "Deletion failed.");
			                        return false;
			                    }
			                }
			            }
			        }
		        }else {
		        	JOptionPane.showMessageDialog(null, "Can not delete Supplier. Items provided by this supplier might have been allocated");
		        }
		    } catch ( SQLException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error deleting supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	   private boolean doesSuppIdExist(int suppId) {
		    boolean suppIdExists = false;
		    try {
		    	Connection connection = databaseConnector.connect();
		    try (
		         PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS SuppIdCount FROM AllocatedItems WHERE SuppId = ?")) {

		        preparedStatement.setInt(1, suppId);

		        try (ResultSet resultSet = preparedStatement.executeQuery()) {
		            if (resultSet.next()) {
		                int suppIdCount = resultSet.getInt("SuppIdCount");
		                suppIdExists = suppIdCount > 0;
		                //System.out.print(suppIdExists);
		            }
		        }
		      }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        // Handle the exception as needed
		    }

		    return suppIdExists;
		}

	   }

