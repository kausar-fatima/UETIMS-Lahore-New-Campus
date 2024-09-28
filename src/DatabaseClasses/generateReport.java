package DatabaseClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class generateReport {
	Connection connection = null;
	private int yearValue = 0;
	private int monthValue = 0;
	 private final DatabaseConnector databaseConnector = new DatabaseConnector();
	 
    public generateReport(int yearValue, int monthValue) {
		this.yearValue = yearValue;
		this.monthValue = monthValue;
	}

    public Map<Integer, Map<String, Map<String, Map<String, Object>>>> getReportDetails() {
        Map<Integer, Map<String, Map<String, Map<String, Object>>>> supplierDetails = new HashMap<>();

        try {
        	connection = databaseConnector.connect();

            // Your SQL query
            String sqlQuery = "SELECT " +
                    "s.SupplierId, " +
                    "s.Name AS SupplierName, " +
                    "si.ItemCategory " +
                    "FROM Suppliers s " +
                    "JOIN SupplierItems si ON s.SupplierId = si.SupplierId " +
                    "WHERE YEAR(s.DeliveredDate) = ? AND MONTH(s.DeliveredDate) = ? " +
                    "GROUP BY s.SupplierId, s.Name, si.ItemCategory, si.ItemName";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, yearValue);
                preparedStatement.setInt(2, monthValue);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int supplierId = resultSet.getInt("SupplierId");
                    String supplierName = resultSet.getString("SupplierName");
                    String category = resultSet.getString("ItemCategory");

                    // Create or get the supplierId map
                    Map<String, Map<String, Map<String, Object>>> supplierIdMap = supplierDetails
                            .computeIfAbsent(supplierId, k -> new HashMap<>());

                    // Create or get the suppliername map
                    Map<String, Map<String, Object>> supplierNameMap = supplierIdMap
                            .computeIfAbsent(supplierName, k -> new HashMap<>());

                    // Create or get the category map within the supplier map
                    Map<String, Object> categoryMap = supplierNameMap
                            .computeIfAbsent(category, k -> new HashMap<>());
                }

                return supplierDetails;
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return supplierDetails;
    }

  
  // to extract unallocated(Inventory) Items
  public ObservableList<Object> getInventoryItems(){
	  ObservableList<Object> rows = FXCollections.observableArrayList();
	  try {
		  connection = databaseConnector.connect();
          

          String sqlQuery = "SELECT ii.ItemName, COUNT(ii.ItemId) AS TotalItemCount " +
                  "FROM Suppliers s " +
                  "JOIN InventoryItems ii ON s.SupplierId = ii.SuppId " +
                  "WHERE YEAR(s.DeliveredDate) = ? AND MONTH(s.DeliveredDate) = ? " +
                  "GROUP BY ii.ItemName";
          
          try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

        	  preparedStatement.setInt(1, yearValue);
              preparedStatement.setInt(2, monthValue);
              try (ResultSet resultSet = preparedStatement.executeQuery()) {
                  // Process the result set
                  while (resultSet.next()) {
                      String itemName = resultSet.getString("ItemName");
                      int itemCount = resultSet.getInt("TotalItemCount");
                      
                      rows.addAll(itemName, itemCount);
                      //System.out.println("Item Name: " + itemName + ", Item Count: " + itemCount);
                  }
                  return rows;
              }
          }
	  }catch ( SQLException | NumberFormatException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error Extracting Inventory Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
      }
	return rows;
  }

  
  // get desire Items
  public ObservableList<Object> getdesireItems(int suppId, String category){
	  ObservableList<Object> rows = FXCollections.observableArrayList();
	  try {
		  connection = databaseConnector.connect();
          
          String sqlQuery = "SELECT ItemName, COUNT(ItemId) AS ItemCount, SUM(ItemPrice) AS TotalPrice " +
                  "FROM SupplierItems " +
                  "WHERE SupplierId = ? AND ItemCategory = ? " +
                  "GROUP BY ItemName";

          try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
              preparedStatement.setInt(1, suppId);
              preparedStatement.setString(2, category);

              try (ResultSet resultSet = preparedStatement.executeQuery()) {
                  // Process the result set
                  while (resultSet.next()) {
                      String itemName = resultSet.getString("ItemName");
                      int itemCount = resultSet.getInt("ItemCount");
                      double totalPrice = resultSet.getDouble("TotalPrice");
                      rows.addAll(itemName, itemCount, totalPrice);
                      //System.out.println("Item Name: " + itemName + ", Item Count: " + itemCount);
                  }
                  return rows;
              }
          }
	  }catch (SQLException | NumberFormatException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error Extracting Supplier Items " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
      }
	return rows;
  }
  
  
  public ObservableList<Object> getdesireInventory(int suppId, String category){
	  ObservableList<Object> rows = FXCollections.observableArrayList();
	  try {
		  connection = databaseConnector.connect();
          
          String sqlQuery = "SELECT ItemName, COUNT(ItemId) AS ItemCount " +
                  "FROM InventoryItems " +
                  "WHERE SuppId = ? AND ItemCategory = ? " +
                  "GROUP BY ItemName";

          try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
              preparedStatement.setInt(1, suppId);
              preparedStatement.setString(2, category);

              try (ResultSet resultSet = preparedStatement.executeQuery()) {
                  // Process the result set
                  while (resultSet.next()) {
                      String itemName = resultSet.getString("ItemName");
                      int itemCount = resultSet.getInt("ItemCount");
                      rows.addAll(itemName, itemCount);
                      //System.out.println("Item Name: " + itemName + ", Item Count: " + itemCount);
                  }
                  return rows;
              }
          }
	  }catch ( SQLException | NumberFormatException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error Extracting Inventory Items " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
      }
	return rows;
  }
  
  // to extract categories
  public ObservableList<Object> getCategories(){
	  ObservableList<Object> rows = FXCollections.observableArrayList();
	  try {
		  connection = databaseConnector.connect();
          
          // Execute the SQL query
          String sqlQuery = "SELECT DISTINCT si.ItemCategory " +
                  "FROM Suppliers s " +
                  "JOIN SupplierItems si ON s.SupplierId = si.SupplierId " +
                  "WHERE YEAR(s.DeliveredDate) = ? AND MONTH(s.DeliveredDate) = ?";


          try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

        	  preparedStatement.setInt(1, yearValue);
              preparedStatement.setInt(2, monthValue);
              try (ResultSet resultSet = preparedStatement.executeQuery()) {
            	  
                  // Process the result set
                  while (resultSet.next()) {
                      String categoryName = resultSet.getString("ItemCategory");
                      
                      rows.addAll(categoryName);
                     // System.out.println("Item Name: " + categoryName );
                  }
                  return rows;
              }
          }
	  }catch ( SQLException | NumberFormatException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error Extracting Supplier Categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
      }
	return rows;
  }
  
  // to extract items
  public ObservableList<Object> getItems(){
	  ObservableList<Object> rows = FXCollections.observableArrayList();
	  try {
		  connection = databaseConnector.connect();
          

          String sqlQuery = "SELECT si.ItemName, COUNT(si.ItemId) AS TotalItemCount, SUM(si.ItemPrice) AS TotalPrice  " +
                  "FROM Suppliers s " +
                  "JOIN SupplierItems si ON s.SupplierId = si.SupplierId " +
                  "WHERE YEAR(s.DeliveredDate) = ? AND MONTH(s.DeliveredDate) = ? " +
                  "GROUP BY si.ItemName";
          
          try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

        	  preparedStatement.setInt(1, yearValue);
              preparedStatement.setInt(2, monthValue);
              try (ResultSet resultSet = preparedStatement.executeQuery()) {
                  // Process the result set
                  while (resultSet.next()) {
                      String itemName = resultSet.getString("ItemName");
                      int itemCount = resultSet.getInt("TotalItemCount");
                      double totalPrice = resultSet.getDouble("TotalPrice");
                      rows.addAll(itemName, itemCount, totalPrice);
                      //System.out.println("Item Name: " + itemName + ", Item Count: " + itemCount);
                  }
                  return rows;
              }
          }
	  }catch ( SQLException | NumberFormatException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error Extracting Supplier Items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
      }
	return rows;
  }
  
//to extract suppliers
 public String getSuppName(int suppId){
	 String Name = null;
	  try {
		  connection = databaseConnector.connect();
         

         String sqlQuery = "SELECT Name FROM Suppliers WHERE SupplierId=?";
         
         try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

       	  preparedStatement.setInt(1, suppId);

             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 // Process the result set
                 while (resultSet.next()) {
                     Name = resultSet.getString("Name");
                     
                     //System.out.println("Item Name: " + itemName + ", Item Count: " + itemCount);
                 }
                 return Name;
             }
         }
	  }catch ( SQLException | NumberFormatException e) {
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "Error Extracting Supplier Name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
     } finally {
         if (connection != null) {
             try {
                 connection.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
     }
	return Name;
 }

}
