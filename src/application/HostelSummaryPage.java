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

public class HostelSummaryPage extends Application {
    private DatabaseConnector connector;
    boolean alertShown = false;
    public HostelSummaryPage() {
        connector = new DatabaseConnector();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	 // Create a TableView for displaying summary information
        TableView<HostelSummaryData> tableView = new TableView<>();
    	 tableView.setPrefHeight(500); // Set your desired height
        primaryStage.setTitle("Hostel Summary");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);
        // Create a BorderPane as the root node
        BorderPane root = new BorderPane();

        // Create a label for the title
        Label titleLabel = new Label("Hostel Summary");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setMargin(titleLabel, new Insets(10));

        // Set the title label at the top
        root.setTop(titleLabel);
     // Create ComboBoxes for selecting search criteria
        ComboBox<String> searchByComboBox = new ComboBox<>();
        searchByComboBox.getItems().addAll("Hostel Name","Item Name", "Item Category");
        searchByComboBox.setPromptText("Select search type");

        // Create a TextField for entering search keyword
        TextField searchField = new TextField();
       

    
     searchByComboBox.setOnAction(event -> {
        
             String selectedSearchType =  searchByComboBox.getValue();
             if ("Hostel Name".equals(selectedSearchType)) {
            	 searchField.setPromptText("Enter Hostel Name");
             } else if ("Item Category".equals(selectedSearchType)) {
            	 searchField.setPromptText("Enter Item Category");
             } else if ("Item Name".equals(selectedSearchType)) {
            	 searchField.setPromptText("Enter Item Name");
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
                
                 if ("Hostel Name".equals(searchType)) {
                	 searchByHostelName(tableView, newValue);
                 } else if ("Item Category".equals(searchType)) {
                	 searchByItemCategory(tableView, newValue);
                 }
                 else if ("Item Name".equals(searchType)) {
                	 searchByItemName(tableView, newValue);
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
       
        TableColumn<HostelSummaryData, String> hostelNameCol = new TableColumn<>("Hostel Name");
        hostelNameCol.setPrefWidth(170);
        hostelNameCol.setCellValueFactory(new PropertyValueFactory<>("hostelName"));
        
        TableColumn<HostelSummaryData, Integer> totalRoomsCol = new TableColumn<>("Total Rooms");
        totalRoomsCol.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        
        TableColumn<HostelSummaryData, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setPrefWidth(170);
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));
        
        TableColumn<HostelSummaryData, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setPrefWidth(170);
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<HostelSummaryData, Integer> totalAllocatedItemsCol = new TableColumn<>("Total Allocated Items");
        totalAllocatedItemsCol.setPrefWidth(170);
        totalAllocatedItemsCol.setCellValueFactory(new PropertyValueFactory<>("totalAllocatedItems"));
        
        tableView.getColumns().addAll(hostelNameCol, totalRoomsCol, itemCategoryCol, itemNameCol, totalAllocatedItemsCol);
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
            displayHostelSummary(tableView);
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
    private void searchByHostelName(TableView<HostelSummaryData> tableView, String hostelName) throws SQLException {
        ObservableList<HostelSummaryData> data = FXCollections.observableArrayList();
        
        try (Connection connection = connector.connect()) {
            String query = "SELECT Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName, COUNT(AllocatedItems.ItemId) AS TotalAllocatedItems " +
                    "FROM Hostel " +
                    "JOIN HostelRooms ON Hostel.HostelId = HostelRooms.HostelId " +
                    "JOIN Rooms ON Hostel.HostelId = Rooms.HostelId " +
                    "JOIN AllocatedItems ON Rooms.RoomId = AllocatedItems.RoomId " +
                    "WHERE Hostel.Name LIKE ? " +
                    "GROUP BY Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + hostelName + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("Name");
                        int totalRooms = resultSet.getInt("TotalRooms");
                        String itemCategory = resultSet.getString("ItemCategory");
                        String itemName = resultSet.getString("ItemName");
                        int totalAllocatedItems = resultSet.getInt("TotalAllocatedItems");
                        data.add(new HostelSummaryData(name, totalRooms, itemCategory, itemName, totalAllocatedItems));
                    }
                }
            }
        } finally {
            connector.closeConnection();
        }
        
        tableView.setItems(data);
    }

    private void searchByItemCategory(TableView<HostelSummaryData> tableView, String itemCategory) throws SQLException {
        ObservableList<HostelSummaryData> data = FXCollections.observableArrayList();
        
        try (Connection connection = connector.connect()) {
            String query = "SELECT Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName, COUNT(AllocatedItems.ItemId) AS TotalAllocatedItems " +
                    "FROM Hostel " +
                    "JOIN HostelRooms ON Hostel.HostelId = HostelRooms.HostelId " +
                    "JOIN Rooms ON Hostel.HostelId = Rooms.HostelId " +
                    "JOIN AllocatedItems ON Rooms.RoomId = AllocatedItems.RoomId " +
                    "WHERE AllocatedItems.ItemCategory LIKE ? " +
                    "GROUP BY Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + itemCategory + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String hostelName = resultSet.getString("Name");
                        int totalRooms = resultSet.getInt("TotalRooms");
                        String categoryName = resultSet.getString("ItemCategory");
                        String itemName = resultSet.getString("ItemName");
                        int totalAllocatedItems = resultSet.getInt("TotalAllocatedItems");
                        data.add(new HostelSummaryData(hostelName, totalRooms, categoryName, itemName, totalAllocatedItems));
                    }
                }
            }
        } finally {
            connector.closeConnection();
        }
        
        tableView.setItems(data);
    }

    private void searchByItemName(TableView<HostelSummaryData> tableView, String itemName) throws SQLException {
        ObservableList<HostelSummaryData> data = FXCollections.observableArrayList();
        
        try (Connection connection = connector.connect()) {
            String query = "SELECT Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName, COUNT(AllocatedItems.ItemId) AS TotalAllocatedItems " +
                    "FROM Hostel " +
                    "JOIN HostelRooms ON Hostel.HostelId = HostelRooms.HostelId " +
                    "JOIN Rooms ON Hostel.HostelId = Rooms.HostelId " +
                    "JOIN AllocatedItems ON Rooms.RoomId = AllocatedItems.RoomId " +
                    "WHERE AllocatedItems.ItemName LIKE ? " +
                    "GROUP BY Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + itemName + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String hostelName = resultSet.getString("Name");
                        int totalRooms = resultSet.getInt("TotalRooms");
                        String itemCategory = resultSet.getString("ItemCategory");
                        String name = resultSet.getString("ItemName");
                        int totalAllocatedItems = resultSet.getInt("TotalAllocatedItems");
                        data.add(new HostelSummaryData(hostelName, totalRooms, itemCategory, name, totalAllocatedItems));
                    }
                }
            }
        } finally {
            connector.closeConnection();
        }
        
        tableView.setItems(data);
    }
    public void displayHostelSummary(TableView<HostelSummaryData> tableView) throws SQLException {
        try {
            String query = "SELECT Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName, COUNT(AllocatedItems.ItemId) AS TotalAllocatedItems " +
                    "FROM Hostel " +
                    "JOIN HostelRooms ON Hostel.HostelId = HostelRooms.HostelId " +
                    "JOIN Rooms ON Hostel.HostelId = Rooms.HostelId " +
                    "JOIN AllocatedItems ON Rooms.RoomId = AllocatedItems.RoomId " +
                    "GROUP BY Hostel.Name, HostelRooms.TotalRooms, AllocatedItems.ItemCategory, AllocatedItems.ItemName";

            ResultSet resultSet = connector.executeQuery(query);

            // Process resultSet and populate the TableView
            ObservableList<HostelSummaryData> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String hostelName = resultSet.getString("Name");
                int totalRooms = resultSet.getInt("TotalRooms");
                String itemCategory = resultSet.getString("ItemCategory");
                String itemName = resultSet.getString("ItemName");
                int totalAllocatedItems = resultSet.getInt("TotalAllocatedItems");

                data.add(new HostelSummaryData(hostelName, totalRooms, itemCategory, itemName, totalAllocatedItems));
            }

            resultSet.close();

            // Set the data to the TableView
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connector.closeConnection();
        }
    }
    private void downloadAsCSV(TableView<HostelSummaryData> tableView) throws IOException {
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
                writer.println("Hostel Name,Total Rooms,Item Category,Item Name,Total Allocated Items");

                // Write the data rows
                for (HostelSummaryData item : tableView.getItems()) {
                    writer.println(String.format("%s,%d,%s,%s,%d",
                            item.getHostelName(),
                            item.getTotalRooms(),
                            item.getItemCategory(),
                            item.getItemName(),
                            item.getTotalAllocatedItems()));
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


    // Define a class for holding hostel summary data
    public static class HostelSummaryData {
        private String hostelName;
        private int totalRooms;
        private String itemCategory;
        private String itemName;
        private int totalAllocatedItems;

        public HostelSummaryData(String hostelName, int totalRooms, String itemCategory, String itemName, int totalAllocatedItems) {
            this.hostelName = hostelName;
            this.totalRooms = totalRooms;
            this.itemCategory = itemCategory;
            this.itemName = itemName;
            this.totalAllocatedItems = totalAllocatedItems;
        }

        // Define getters for accessing data
        public String getHostelName() {
            return hostelName;
        }

        public int getTotalRooms() {
            return totalRooms;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public String getItemName() {
            return itemName;
        }

        public int getTotalAllocatedItems() {
            return totalAllocatedItems;
        }


    }
}