package application;


import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DatabaseClasses.DatabaseConnector;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

public class DashBoard {

	 private Connection connection;
	    private static DatabaseConnector databaseConnector = new DatabaseConnector();
	    private SettingsPane settingsPane; // Change the type to SettingsPane
	    private BorderPane borderPane;
	    private Button userProfileButton; // Move the declaration outside the method

	    public DashBoard(BorderPane borderPane, String email, String password) {
	        this.borderPane = borderPane;
	        // Initialize the SettingsPane with email and password
	        settingsPane = new SettingsPane(email, password);

	        // Initialize userProfileButton
	        userProfileButton = new Button("User Account");
	        Circle circle = new Circle();
	        circle.setRadius(30); // You can adjust the radius based on your preference
	        circle.setStyle("-fx-background-color: white; -fx-border-color: #ffffff; -fx-text-fill: white;");
	        userProfileButton.setShape(circle);
	        userProfileButton.setMinSize(2 * circle.getRadius(), 2 * circle.getRadius());
	        userProfileButton.setVisible(false); // Set visibility to false initially

	        // Set the userProfileButton's action
	        userProfileButton.setOnAction(event -> {
	            // Clear and hide search fields
	            // Remove previous center and bottom components
	            borderPane.setCenter(null);
	            borderPane.setBottom(null);
	            borderPane.setRight(null);
	            borderPane.setTop(null);

	            // Add the new components to the BorderPane
	            try {
	                borderPane.setTop(createTopNavigationBar(false));

	            } catch (MalformedURLException e) {
	                e.printStackTrace();
	            }

	            // Set the settings pane as the center
	            borderPane.setCenter(settingsPane.getSettingsPane());

	            // Hide userProfileButton
	            userProfileButton.setVisible(false);
	        });
	    }

	    public HBox createTopNavigationBar(boolean showUserProfileButton) throws MalformedURLException {
	        HBox topNavigationBar = new HBox(15);
	        topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20");

	        ImageView logoImageView = new ImageView(new String("uet.png"));
	        logoImageView.setFitHeight(80);
	        logoImageView.setPreserveRatio(true);

	        // Set the logoImageView to the left corner
	        //HBox.setMargin(logoImageView, new Insets(0, 10, 0, 10));

	        // Align the elements to the right corner
	        HBox rightButtons = new HBox(userProfileButton);
	        rightButtons.setAlignment(Pos.CENTER_RIGHT);

	        // Set the right margin for the userProfileButton
	        HBox.setMargin(userProfileButton, new Insets(0, 20, 0, 0));

	        // Set the rightButtons HBox to take remaining space
	        HBox.setHgrow(rightButtons, Priority.ALWAYS);

	        topNavigationBar.getChildren().addAll(logoImageView, rightButtons);
	        // Set the visibility of userProfileButton based on the parameter
	        userProfileButton.setVisible(showUserProfileButton);
	        return topNavigationBar;
	    }
	
	 public BarChart<String, Number> createBarChart() throws ClassNotFoundException {
		    CategoryAxis xAxis = new CategoryAxis();
		    NumberAxis yAxis = new NumberAxis();
		    
		    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

		    try {
		        updateBarChartData(barChart); // Update data from the database
		    } catch (SQLException e) {
		        e.printStackTrace();
		        // Handle the exception appropriately in a real application
		    } finally {
		        try {
		            if (connection != null) {
		                connection.close();
		            }
		        } catch (SQLException e) {
		            e.printStackTrace();
		            // Handle the exception appropriately in a real application
		        }
		    }

		    return barChart;
		}

		private void updateBarChartData(BarChart<String, Number> barChart) throws SQLException, ClassNotFoundException {
		    try {
		        Connection connection = databaseConnector.connect();

		        // Query to get allocated and unallocated items count for each category with item names
		        String query = "SELECT\r\n"
		        		+ "    ic.CategoryName,\r\n"
		        		+ "    COALESCE(ai.TotalAllocatedItems, 0) AS TotalAllocatedItems,\r\n"
		        		+ "    COALESCE(ii.TotalInventoryItems, 0) AS TotalInventoryItems,\r\n"
		        		+ "    (COALESCE(ai.TotalAllocatedItems, 0) + COALESCE(ii.TotalInventoryItems, 0)) AS TotalItems,\r\n"
		        		+ "    COALESCE(ai.AllocatedItemNames, '') AS AllocatedItemNames,\r\n"
		        		+ "    COALESCE(ii.InventoryItemNames, '') AS InventoryItemNames\r\n"
		        		+ "FROM\r\n"
		        		+ "    ItemsCategory ic\r\n"
		        		+ "LEFT JOIN (\r\n"
		        		+ "    SELECT\r\n"
		        		+ "        ItemCategory,\r\n"
		        		+ "        COUNT(*) AS TotalAllocatedItems,\r\n"
		        		+ "        STUFF((SELECT ', ' + ItemName FROM AllocatedItems WHERE ItemCategory = ai.ItemCategory FOR XML PATH('')), 1, 2, '') AS AllocatedItemNames\r\n"
		        		+ "    FROM\r\n"
		        		+ "        AllocatedItems ai\r\n"
		        		+ "    GROUP BY\r\n"
		        		+ "        ItemCategory\r\n"
		        		+ ") AS ai ON ai.ItemCategory = ic.CategoryName\r\n"
		        		+ "LEFT JOIN (\r\n"
		        		+ "    SELECT\r\n"
		        		+ "        ItemCategory,\r\n"
		        		+ "        COUNT(*) AS TotalInventoryItems,\r\n"
		        		+ "        STUFF((SELECT ', ' + ItemName FROM InventoryItems WHERE ItemCategory = ii.ItemCategory FOR XML PATH('')), 1, 2, '') AS InventoryItemNames\r\n"
		        		+ "    FROM\r\n"
		        		+ "        InventoryItems ii\r\n"
		        		+ "    GROUP BY\r\n"
		        		+ "        ItemCategory\r\n"
		        		+ ") AS ii ON ii.ItemCategory = ic.CategoryName;\r\n"
		        		+ "";

		        PreparedStatement preparedStatement = connection.prepareStatement(query);
		        ResultSet resultSet = preparedStatement.executeQuery();

		        // Clear existing data
		        barChart.getData().clear();

		        // Add new data from the database
		        while (resultSet.next()) {
		            String categoryName = resultSet.getString("CategoryName");
		            int allocatedCount = resultSet.getInt("TotalAllocatedItems");
		            int unallocatedCount = resultSet.getInt("TotalInventoryItems");

		            BarChart.Series<String, Number> series = new BarChart.Series<>();
		            series.setName(categoryName);
		            series.getData().add(new BarChart.Data<>("Allocated", allocatedCount));
		            series.getData().add(new BarChart.Data<>("Unallocated", unallocatedCount));
		            barChart.getData().add(series);
		        }
		    } finally {
		        // Close resources
		        if (connection != null) {
		            connection.close();
		        }
		    }
		}

    public PieChart createPieChart() throws ClassNotFoundException {
        PieChart pieChart = new PieChart();

        try {
            updatePieChartData(pieChart); // Update data from the database
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately in a real application
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception appropriately in a real application
            }
        }

        return pieChart;
    }

    private void updatePieChartData(PieChart pieChart) throws SQLException, ClassNotFoundException {
        try {
        	Connection connection = databaseConnector.connect();

            // Query to get category data and individual counts
            String query = "SELECT ItemCategory, COUNT(*) as Count FROM InventoryItems GROUP BY ItemCategory";
            PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Calculate total count
            int totalCount = 0;
            while (resultSet.next()) {
                totalCount += resultSet.getInt("Count");
            }

            // Clear existing data
            pieChart.getData().clear();

            // Add new data from the database with percentages
            resultSet.beforeFirst(); // Move the cursor back to the beginning
            while (resultSet.next()) {
                String categoryName = resultSet.getString("ItemCategory");
                int count = resultSet.getInt("Count");
                double percentage = (count * 100.0) / totalCount;
                pieChart.getData().add(new PieChart.Data(categoryName + " (" + String.format("%.2f", percentage) + "%)", count));
            }
        } finally {
            // Close resources
            if (connection != null) {
                connection.close();
            }
        }
    }
  

}
