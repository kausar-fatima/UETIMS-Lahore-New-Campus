package DatabaseClasses;

import javax.swing.JOptionPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryItemsDB {
    private DatabaseConnector databaseConnector = new DatabaseConnector();
    private String checkRef;

    public ObservableList<Object[]> getInventoryItems() {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectInventorySql = "SELECT * FROM InventoryItems";
            try (PreparedStatement selectInventoryStatement = connection.prepareStatement(selectInventorySql);
                 ResultSet resultSet = selectInventoryStatement.executeQuery()) {

                while (resultSet.next()) {
                    int no = resultSet.getInt("ItemId");
                    String name = resultSet.getString("ItemName");
                    String category = resultSet.getString("ItemCategory");
                    Integer ref = (Integer) resultSet.getObject("SuppId"); // Use Integer instead of int to handle NULL values
                    //System.out.print(checkRef);
                    if(ref == null) {
                    	checkRef = "By User";
                    }else {
                    	checkRef = "By Supplier";
                    }
                    //System.out.print(checkRef);
                    rows.add(new Object[]{no, name, category, checkRef});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving Inventory Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }
        return rows;
    }

    public void addInventoryItem(Integer suppItemId, Integer suppId, String itemName, String itemCategory, int quantity) {
        try (Connection connection = databaseConnector.connect()) {
            String insertInventoryItemSql = "INSERT INTO InventoryItems (SuppItemId, SuppId, ItemName, ItemCategory) VALUES (?, ?, ?, ?)";

            try (PreparedStatement insertInventoryItemStatement = connection.prepareStatement(insertInventoryItemSql)) {
                int count = 0;

                while (count < quantity) {
                    insertInventoryItemStatement.setObject(1, suppItemId); // Set SuppItemId to null if it's null
                    insertInventoryItemStatement.setObject(2, suppId); // Set SuppId to null if it's null
                    insertInventoryItemStatement.setString(3, itemName);
                    insertInventoryItemStatement.setString(4, itemCategory);

                    int affectedRows = insertInventoryItemStatement.executeUpdate();

                    if (affectedRows > 0) {
                        count++;
                    }
                }

                if (count == quantity) {
                    JOptionPane.showMessageDialog(null, "Inventory Items added successfully!");
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding Inventory Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }
    }

    public void updateItemInfo(ObservableList<Object[]> selectedItems, String name, String category) {
        
    	int num = 0;
    	try (Connection connection = databaseConnector.connect()) {
            for(int item = 0;item<selectedItems.size();item++) {
            	try {
            		String id = getProperty(selectedItems.get(item), 0);
                    // Assuming you have the Supplier ID and Supplier Item ID available
                    Integer suppId = getSupplierIdForInventoryItem(connection,Integer.parseInt(id));
                    Integer suppItemId = getSupplierItemIdForInventoryItem(connection,Integer.parseInt(id));
                    //Integer currentSupplierItemId = getSupplierItemIdForInventoryItem(connection, Integer.parseInt(id));

                  
                    if (suppItemId != null && suppId != null && suppItemId != 0 && suppId != 0) {
                    	 //System.out.println("Entering confirmation dialog block.");
                    	num++;
                        if(num == 1) {
                        	int confirmResult = JOptionPane.showConfirmDialog(
                                    null,
                                    "Updating item will also update the corresponding supplier item. Do you want to proceed?",
                                    "Confirmation",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirmResult != JOptionPane.YES_OPTION) {
                                return; // User canceled the update
                            }
                        }
                    }



                    String inventoryItemSql = "UPDATE InventoryItems SET ItemName = ?, ItemCategory = ? WHERE ItemId = ?";

                    try (PreparedStatement updateInventoryItemStatement = connection.prepareStatement(inventoryItemSql)) {
                        updateInventoryItemStatement.setString(1, name);
                        updateInventoryItemStatement.setString(2, category);
                        updateInventoryItemStatement.setInt(3, Integer.parseInt(id));

                        int affectedRows = updateInventoryItemStatement.executeUpdate();

                        if (affectedRows > 0) {
                            //JOptionPane.showMessageDialog(null, "Inventory information updated successfully!");

                            // If supplierItemId and supplierId are not null, update the corresponding supplier item
                            if (suppItemId != null && suppId != null && suppItemId != 0 && suppId != 0) {
                                updateSupplierItemInfo(connection, suppItemId, suppId, name, category);
                                //JOptionPane.showMessageDialog(null, "Supplier Item information updated successfully!");
                            }
                            if(item == selectedItems.size()-1) {
                            	if(num > 0 && selectedItems.size() == 1) {
                            		JOptionPane.showMessageDialog(null, "Inventory as well as Supplier Item updated successfully!");
                            	}else if(num == 0 && selectedItems.size() == 1) {
                            		JOptionPane.showMessageDialog(null, "Inventory Item updated successfully!");
                            	}
                            	else if(num > 0 ) {
                            		JOptionPane.showMessageDialog(null, "Inventory as well as Supplier Items updated successfully!");
                            	}else if(num == 0) {
                            		JOptionPane.showMessageDialog(null, "Inventory Items updated successfully!");
                            	}
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Inventory Item update failed.");
                        }
                    }
            	}catch (Exception e) {
            		JOptionPane.showMessageDialog(null, "Inventory Item update failed.");
				}
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating Inventory Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }
    }


    // Helper method to update Supplier Item information
    private void updateSupplierItemInfo(Connection connection, int supplierItemId, Integer suppId, String name, String category) throws SQLException {
        String supplierItemSql = "UPDATE SupplierItems SET ItemName = ?, ItemCategory = ? WHERE ItemId = ? AND SupplierId = ?";

        try (PreparedStatement updateSupplierItemStatement = connection.prepareStatement(supplierItemSql)) {
            updateSupplierItemStatement.setString(1, name);
            updateSupplierItemStatement.setString(2, category);
            updateSupplierItemStatement.setInt(3, supplierItemId);
            updateSupplierItemStatement.setInt(4, suppId);

            int affectedRows = updateSupplierItemStatement.executeUpdate();

            if (affectedRows > 0) {
                //JOptionPane.showMessageDialog(null, "Supplier Item information updated successfully!");
            } else {
               // JOptionPane.showMessageDialog(null, "Supplier Item update failed.");
            }
        }
    }


    public ObservableList<Object[]> searchInventoryByItemName(String itemName) {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByNameSql = "SELECT * FROM InventoryItems WHERE ItemName LIKE ?";

            try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
                selectInventoryByNameStatement.setString(1, "%" + itemName + "%");

                try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int no = resultSet.getInt("ItemId");
                        String name = resultSet.getString("ItemName");
                        String category = resultSet.getString("ItemCategory");
                        Integer ref = (Integer) resultSet.getObject("SuppId"); // Use Integer instead of int to handle NULL values
                        //System.out.print(checkRef);
                        if(ref == null) {
                        	checkRef = "By User";
                        }else {
                        	checkRef = "By Supplier";
                        }
                        rows.add(new Object[]{no, name, category,checkRef});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching Inventory Items by name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }

        return rows;
    }

    public ObservableList<Object[]> searchInventoryByCategory(String category) {
        ObservableList<Object[]> rows = FXCollections.observableArrayList();

        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByCategorySql = "SELECT * FROM InventoryItems WHERE ItemCategory LIKE ?";

            try (PreparedStatement selectInventoryByCategoryStatement = connection.prepareStatement(selectInventoryByCategorySql)) {
                selectInventoryByCategoryStatement.setString(1, "%" + category + "%");

                try (ResultSet resultSet = selectInventoryByCategoryStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int no = resultSet.getInt("ItemId");
                        String name = resultSet.getString("ItemName");
                        String itemCategory = resultSet.getString("ItemCategory");
                        Integer ref = (Integer) resultSet.getObject("SuppId"); // Use Integer instead of int to handle NULL values
                        //System.out.print(checkRef);
                        if(ref == null) {
                        	checkRef = "By User";
                        }else {
                        	checkRef = "By Supplier";
                        }
                        rows.add(new Object[]{no, name, itemCategory,checkRef});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching Inventory Items by category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            databaseConnector.closeConnection();
        }

        return rows;
    }
 // Helper method to get current SupplierId for an Inventory Item
    public Integer getSupplierIdForInventoryItem(Connection connection, int inventoryItemId) throws SQLException {
        String query = "SELECT SuppId FROM InventoryItems WHERE ItemId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, inventoryItemId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("SuppId");
                }
            }
        }
        return null;
    }

    // Helper method to get current SupplierItemId for an Inventory Item
    public Integer getSupplierItemIdForInventoryItem(Connection connection, int inventoryItemId) throws SQLException {
        String query = "SELECT SuppItemId FROM InventoryItems WHERE ItemId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, inventoryItemId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("SuppItemId");
                }
            }
        }
        return null;
    }
    public boolean deleteInventoryItem(ObservableList<Object[]> selectedItems) {
    	int num = 0;
        try (Connection connection = databaseConnector.connect()) {
            // Check if the item has associated supplier item
        	for (int item = 0;item<selectedItems.size();item++) {
        		try {
        			int itemId = (int) selectedItems.get(item)[0];
                    Integer suppItemId = getSupplierItemIdForInventoryItem(connection, itemId);
                    Integer suppId = getSupplierIdForInventoryItem(connection, itemId);

                    // If supplierItemId and supplierId are not null, show confirmation message
                    if (suppItemId != null && suppId != null && suppItemId != 0 && suppId != 0) {
                    	num++;
                        if(num==1) {
                          int confirmResult = JOptionPane.showConfirmDialog(
                          null,
                          "Deleting this item will also delete the corresponding supplier item. Do you want to proceed?",
                          "Confirmation",
                          JOptionPane.YES_NO_OPTION
                        );

                       if (confirmResult != JOptionPane.YES_OPTION) {
                          return false; // User canceled the delete
                          }
                        }
                    	
                    }

                    String deleteInventoryItemSql = "DELETE FROM InventoryItems WHERE ItemId = ?";

                    try (PreparedStatement deleteInventoryItemStatement = connection.prepareStatement(deleteInventoryItemSql)) {
                        deleteInventoryItemStatement.setInt(1, itemId);

                        int affectedRows = deleteInventoryItemStatement.executeUpdate();

                        if (affectedRows > 0) {
                        	  
                            if (suppItemId != null && suppId != null && suppItemId != 0 && suppId != 0) {
                                // Delete corresponding supplier item        	
                                deleteSupplierItemInfo(connection, suppItemId, suppId);
                            } 
                            if(item == selectedItems.size()-1) {
                            	if(num > 0 && selectedItems.size() == 1) {
                            		JOptionPane.showMessageDialog(null, "Inventory as well as Supplier Item deleted successfully!");
                            	}else if(num == 0 && selectedItems.size() == 1) {
                            		JOptionPane.showMessageDialog(null, "Inventory Item deleted successfully!");
                            	}
                            	else if(num > 0) {
                            		JOptionPane.showMessageDialog(null, "Inventory as well as corresponding Supplier Items deleted successfully!");
                            	}else if(num == 0) {
                            		JOptionPane.showMessageDialog(null, "Inventory Items deleted successfully!");
                            	}
                            }
                            
                        } else {
                            JOptionPane.showMessageDialog(null, "Error deleting Inventory Item.");
                            return false; // Deletion failed
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting Inventory Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
        	}

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting Inventory Item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Deletion failed
        } finally {
            databaseConnector.closeConnection();
        }
		return false;
    }

    private void deleteSupplierItemInfo(Connection connection, int supplierItemId, int supplierId) throws SQLException {
        String deleteSupplierItemSql = "DELETE FROM SupplierItems WHERE ItemId = ? AND SupplierId = ?";

        try (PreparedStatement deleteSupplierItemStatement = connection.prepareStatement(deleteSupplierItemSql)) {
            deleteSupplierItemStatement.setInt(1, supplierItemId);
            deleteSupplierItemStatement.setInt(2, supplierId);

            int affectedRows = deleteSupplierItemStatement.executeUpdate();

            if (affectedRows > 0) {
                 //JOptionPane.showMessageDialog(null, "Supplier Item deleted successfully!");
            } else {
                 JOptionPane.showMessageDialog(null, "Error deleting Supplier Item.");
            }
        }
    }

    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }

}
