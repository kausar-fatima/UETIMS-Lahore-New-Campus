package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryDB {

    private final DatabaseConnector databaseConnector = new DatabaseConnector();
    private Connection connection;
    public ObservableList<String> getItemCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectCategoriesSql = "SELECT CategoryName FROM ItemsCategory ";
            try (PreparedStatement selectCategoriesStatement = connection.prepareStatement(selectCategoriesSql);
                 ResultSet resultSet = selectCategoriesStatement.executeQuery()) {

                while (resultSet.next()) {
                    String category = resultSet.getString("CategoryName");
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving item categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return categories;
    }

	 
	public ObservableList<Object[]> getItemCategoriesInfo() {
   	ObservableList<Object[]> rows = FXCollections.observableArrayList();
	// Populate the table model with data
      try {
   	   Connection connection = databaseConnector.connect();
          String selectHostelSql = "SELECT * FROM ItemsCategory";
          
          try (PreparedStatement selectHostelStatement = connection.prepareStatement(selectHostelSql);
               ResultSet resultSet = selectHostelStatement.executeQuery()) {

              while (resultSet.next()) {
           	   int supplierId=resultSet.getInt("CategoryId");
                  String name = resultSet.getString("CategoryName");
                
                  // Add row to the table model
                  
                  rows.add(new Object[]{supplierId,name, });
                  
              }
              return rows;
          }
      } catch ( SQLException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error retrieving supplier information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
	return rows;

  }
    //Add information
	   public void addCategoryInfo(String name) {
		  
		try {
			Connection connection = databaseConnector.connect();
	           // Insert into table
	           String supplierSql = "INSERT INTO ItemsCategory (CategoryName) VALUES (?)";

	           try (PreparedStatement supplierStatement = connection.prepareStatement(supplierSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

	               supplierStatement.setString(1, name);
	               

	               int affectedSupplierRows = supplierStatement.executeUpdate();

	                       if (affectedSupplierRows > 0) {
	                           JOptionPane.showMessageDialog(null, "Category added successfully!");
	                       }

	           }
	       } catch ( SQLException | NumberFormatException e) {
	           e.printStackTrace();
	           JOptionPane.showMessageDialog(null, "Error adding category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
	   
	   //update
	   public void updateCategoryInfo(int categoryId, String newName) {
		    Connection connection = null;
		    try {
		        connection = databaseConnector.connect();
		        connection.setAutoCommit(false); // Disable auto-commit for transaction

		        // Fetch the current category name from ItemsCategory table
		        String currentCategoryName = null;
		        String fetchCategoryNameSql = "SELECT CategoryName FROM ItemsCategory WHERE CategoryId = ?";
		        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchCategoryNameSql)) {
		            fetchStmt.setInt(1, categoryId);
		            try (ResultSet rs = fetchStmt.executeQuery()) {
		                if (rs.next()) {
		                    currentCategoryName = rs.getString("CategoryName");
		                }
		            }
		        }

		        if (currentCategoryName == null) {
		            JOptionPane.showMessageDialog(null, "Category not found for the given CategoryId.");
		            return;
		        }

		        // Update ItemsCategory table
		        String updateItemsCategorySql = "UPDATE ItemsCategory SET CategoryName = ? WHERE CategoryId = ?";
		        try (PreparedStatement stmt = connection.prepareStatement(updateItemsCategorySql)) {
		            stmt.setString(1, newName);
		            stmt.setInt(2, categoryId);
		            stmt.executeUpdate();
		        }

		        // Update AllocatedItems table
		        String updateAllocatedItemsSql = "UPDATE AllocatedItems SET ItemCategory = ? WHERE ItemCategory = ?";
		        try (PreparedStatement stmt = connection.prepareStatement(updateAllocatedItemsSql)) {
		            stmt.setString(1, newName);
		            stmt.setString(2, currentCategoryName);
		            stmt.executeUpdate();
		        }

		        // Update InventoryItems table
		        String updateInventoryItemsSql = "UPDATE InventoryItems SET ItemCategory = ? WHERE ItemCategory = ?";
		        try (PreparedStatement stmt = connection.prepareStatement(updateInventoryItemsSql)) {
		            stmt.setString(1, newName);
		            stmt.setString(2, currentCategoryName);
		            stmt.executeUpdate();
		        }

		        // Update RequestItem table
		        String updateRequestItemSql = "UPDATE RequestItem SET ItemCategory = ? WHERE ItemCategory = ?";
		        try (PreparedStatement stmt = connection.prepareStatement(updateRequestItemSql)) {
		            stmt.setString(1, newName);
		            stmt.setString(2, currentCategoryName);
		            stmt.executeUpdate();
		        }

		        // Update SupplierItems table
		        String updateSupplierItemsSql = "UPDATE SupplierItems SET ItemCategory = ? WHERE ItemCategory = ?";
		        try (PreparedStatement stmt = connection.prepareStatement(updateSupplierItemsSql)) {
		            stmt.setString(1, newName);
		            stmt.setString(2, currentCategoryName);
		            stmt.executeUpdate();
		        }

		        // Commit the transaction if all updates are successful
		        connection.commit();
		        JOptionPane.showMessageDialog(null, "Category information updated successfully in all tables.");

		    } catch (SQLException e) {
		        try {
		            if (connection != null) {
		                connection.rollback(); // Rollback transaction on error
		            }
		        } catch (SQLException rollbackEx) {
		            rollbackEx.printStackTrace();
		        }
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error updating category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		    } finally {
		        if (connection != null) {
		            try {
		                connection.setAutoCommit(true); // Re-enable auto-commit
		                connection.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		}
	   
	   //delete
	   public void deleteCategory( int roomId) {
	        try (Connection connection = databaseConnector.connect()) {
	            String deleteSql = "DELETE FROM ItemsCategory WHERE CategoryId = ?";

	            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
	                
	                deleteStatement.setInt(1, roomId);
	               

	                int affectedcategory=deleteStatement.executeUpdate();
	                if (affectedcategory>0) {
	                	 JOptionPane.showMessageDialog(null, "Category deleted successfully.");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            // Handle the exception as per your application requirements
	        }
	    }


	   //search
	   public ObservableList<Object[]> searchByCategory(String category) {
	        ObservableList<Object[]> rows = FXCollections.observableArrayList();

	        try (Connection connection = databaseConnector.connect()) {
	            String selectInventoryByCategorySql = "SELECT * FROM ItemsCategory WHERE CategoryName LIKE ?";

	            try (PreparedStatement selectInventoryByCategoryStatement = connection.prepareStatement(selectInventoryByCategorySql)) {
	                selectInventoryByCategoryStatement.setString(1, "%" + category + "%");

	                try (ResultSet resultSet = selectInventoryByCategoryStatement.executeQuery()) {
	                    while (resultSet.next()) {
	                      
	                        String itemCategory = resultSet.getString("CategoryName");
	                        int supplierId=resultSet.getInt("CategoryId");
	                        rows.add(new Object[]{ supplierId,itemCategory});
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error searching category by name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        } finally {
	            databaseConnector.closeConnection();
	        }

	        return rows;
	    }
	   
	   //to have unique cat
	   public boolean isCategoryExists(String name) {
		    try {
		    	Connection connection = databaseConnector.connect();

		        String selectHostelSql = "SELECT COUNT(*) AS count FROM ItemsCategory WHERE CategoryName = ?";
		        
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


}
