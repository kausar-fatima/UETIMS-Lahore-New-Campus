package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import DatabaseClasses.DatabaseConnector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TotalItemsPage extends Application {
    private DatabaseConnector connector;
    private final DatabaseConnector databaseConnector = new DatabaseConnector();
    boolean alertShown = false;
    public TotalItemsPage() {
        connector = new DatabaseConnector();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	   // Create a TableView for displaying total items information
        TableView<TotalItemsData> tableView = new TableView<>();
        tableView.setPrefHeight(500); // Set your desired height

        primaryStage.setTitle("Total Items");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        // Create a BorderPane as the root node
        BorderPane root = new BorderPane();

        // Create a label for the title
        Label titleLabel = new Label("Total Items");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setMargin(titleLabel, new Insets(10));

        // Set the title label at the top
        root.setTop(titleLabel);

        // Create ComboBoxes for selecting search criteria
        ComboBox<String> searchByComboBox = new ComboBox<>();
        searchByComboBox.getItems().addAll("Item Name", "Item Category");
        searchByComboBox.setPromptText("Select search type");

        // Create a TextField for entering search keyword
        TextField searchField = new TextField();
       

    
     searchByComboBox.setOnAction(event -> {
        
             String selectedSearchType =  searchByComboBox.getValue();
             if ("Item Name".equals(selectedSearchType)) {
            	 searchField.setPromptText("Enter Item Name");
             } else if ("Item Category".equals(selectedSearchType)) {
            	 searchField.setPromptText("Enter Item Category");
             } 
        
     });

   
     

     // Add event listener to searchTextField
     searchField.textProperty().addListener((observable, oldValue, newValue) -> {
         if (searchByComboBox.getSelectionModel().isEmpty()) {
             if (!alertShown) {
                 showAlert("Select Search Type", "Please select a search type from the dropdown.");
                 alertShown = true;
             }
             searchField.clear();
         } else {
             // Reset the flag if the selection is made
             alertShown = false;
             String searchType =  searchByComboBox.getValue();
             try {
                
                 if ("Item Name".equals(searchType)) {
                	 searchByItemName(tableView, newValue);
                 } else if ("Item Category".equals(searchType)) {
                	 searchByItemCategory(tableView, newValue);
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             
         }
     });


      

        // Create a HBox to hold search components
        HBox searchBox = new HBox(10, searchByComboBox, searchField);
        searchBox.setAlignment(Pos.CENTER);
        BorderPane.setMargin(searchBox, new Insets(10));
     

     
        TableColumn<TotalItemsData, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setPrefWidth(170);
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<TotalItemsData, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setPrefWidth(170);
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        TableColumn<TotalItemsData, Integer> totalItemsCol = new TableColumn<>("Total Items");
        totalItemsCol.setCellValueFactory(new PropertyValueFactory<>("totalItems"));

        tableView.getColumns().addAll(itemNameCol, itemCategoryCol, totalItemsCol);
        VBox tableContainer = new VBox(tableView);
        BorderPane.setMargin(tableContainer, new Insets(10));
        root.setCenter(tableContainer);

        ImageView homeImageView = new ImageView("dd.png");
        // Set the size of the image icon
        homeImageView.setFitWidth(20);
        homeImageView.setFitHeight(20); // Set the height of the image icon

        // Create a download button with the image icon
        Button downloadButton = new Button("", homeImageView);

        downloadButton.setOnAction(e -> {
            try {
                downloadAsCSV(tableView);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Add the download button in a HBox and position it in the top right corner
        HBox buttonContainer = new HBox(downloadButton);
        buttonContainer.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(buttonContainer, new Insets(10));
     // Combine searchBox and buttonContainer into a single node
        HBox topContainer = new  HBox(10,searchBox, buttonContainer);
        topContainer.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(topContainer, new Insets(10));
        // Set the topContainer as the top of the BorderPane
        root.setTop(topContainer);

        // Create the scene
        Scene scene = new Scene(root, 800, 600);

        // Display the scene
        primaryStage.setScene(scene);
        primaryStage.show();

        // Call the displayHostelSummary method to populate the table with data
        try {
            displayTotalItems(tableView);
        } catch (SQLException e) {
            e.printStackTrace();
        }

      
    }

    void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }
    public void displayTotalItems(TableView<TotalItemsData> tableView) throws SQLException {
        try {
            String query = "SELECT ic.CategoryName AS ItemCategory, ii.ItemName, COUNT(*) AS TotalItems " +
                           "FROM InventoryItems ii " +
                           "JOIN ItemsCategory ic ON ii.ItemCategory = ic.CategoryName " +
                           "GROUP BY ic.CategoryName, ii.ItemName";

            ResultSet resultSet = connector.executeQuery(query);

            // Process resultSet and populate the TableView
            ObservableList<TotalItemsData> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String itemName = resultSet.getString("ItemName");
                String itemCategory = resultSet.getString("ItemCategory");
                int totalItems = resultSet.getInt("TotalItems");

                data.add(new TotalItemsData(itemName, itemCategory, totalItems));
                //System.out.print(data);
            }
            System.out.println("Total Items Data: " + data.stream()
            .map(Object::toString)
            .collect(Collectors.joining(", ")));


            resultSet.close();

            // Set the data to the TableView
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connector.closeConnection();
        }
    }
    private void downloadAsCSV(TableView<TotalItemsData> tableView) throws IOException {
        // Create a file chooser dialog to select the save location
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            // Check if the file is already open
            if (isFileOpen(file)) {
                showAlert("File In Use", "The file is already open in another application. Please close it and try again.");
                return;
            }

            // Ensure the file has a .csv extension
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                file = new File(filePath + ".csv");
            }

            // Write the CSV data to the selected file
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write the header row
                writer.println("Item Name,Item Category,Total Items");

                // Write the data rows
                for (TotalItemsData item : tableView.getItems()) {
                    writer.println(String.format("%s,%s,%d",
                            item.getItemName(),
                            item.getItemCategory(),
                            item.getTotalItems()));
                }
            }

            // Show a success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Successful");
            alert.setHeaderText(null);
            alert.setContentText("CSV file downloaded successfully.");
            alert.showAndWait();
        }
    }
    private boolean isFileOpen(File file) {
        // Check if the file exists and is readable
        if (file.exists() && !file.isDirectory()) {
            try {
                // Try to open the file in write mode
                new FileOutputStream(file).close();
                // If successful, the file is not open
                return false;
            } catch (IOException e) {
                // If an IOException occurs, the file is already open
                return true;
            }
        }
        // If the file does not exist or is a directory, assume it's not open
        return false;
    }
    private void searchByItemName(TableView<TotalItemsData> tableView, String itemName) throws SQLException {
        ObservableList<TotalItemsData> data = FXCollections.observableArrayList();
       
        
        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByNameSql ="SELECT ic.CategoryName AS ItemCategory, ii.ItemName, COUNT(*) AS TotalItems " +
                    "FROM InventoryItems ii " +
                    "JOIN ItemsCategory ic ON ii.ItemCategory = ic.CategoryName " +
                    "WHERE ii.ItemName LIKE ? " +
                    "GROUP BY ic.CategoryName, ii.ItemName";

            try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
                selectInventoryByNameStatement.setString(1, "%" + itemName + "%");

                try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
                    while (resultSet.next()) {
                    String name = resultSet.getString("ItemName");
                    String category = resultSet.getString("ItemCategory");
                    int totalItems = resultSet.getInt("TotalItems");
                    data.add(new TotalItemsData(name, category, totalItems));
                }
            }
        }
        } tableView.setItems(data);
    }
    private void searchByItemCategory(TableView<TotalItemsData> tableView, String itemCategory) throws SQLException {
        ObservableList<TotalItemsData> data = FXCollections.observableArrayList();
       
        
        try (Connection connection = databaseConnector.connect()) {
            String selectInventoryByNameSql ="SELECT ic.CategoryName AS ItemCategory, ii.ItemName, COUNT(*) AS TotalItems " +
                    "FROM InventoryItems ii " +
                    "JOIN ItemsCategory ic ON ii.ItemCategory = ic.CategoryName " +
                    "WHERE ic.CategoryName LIKE ? " +
                    "GROUP BY ic.CategoryName, ii.ItemName";

            try (PreparedStatement selectInventoryByNameStatement = connection.prepareStatement(selectInventoryByNameSql)) {
                selectInventoryByNameStatement.setString(1, "%" + itemCategory + "%");

                try (ResultSet resultSet = selectInventoryByNameStatement.executeQuery()) {
                    while (resultSet.next()) {
                    String name = resultSet.getString("ItemName");
                    String category = resultSet.getString("ItemCategory");
                    int totalItems = resultSet.getInt("TotalItems");
                    data.add(new TotalItemsData(name, category, totalItems));
                }
            }
        }
        } tableView.setItems(data);
    }
   
    // Define a class for holding total items data
    public static class TotalItemsData {
        private String itemName;
        private String itemCategory;
        private int totalItems;

        public TotalItemsData(String itemName, String itemCategory, int totalItems) {
            this.itemName = itemName;
            this.itemCategory = itemCategory;
            this.totalItems = totalItems;
        }

        // Define getters for accessing data
        public String getItemName() {
            return itemName;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public int getTotalItems() {
            return totalItems;
        }
    }
}
