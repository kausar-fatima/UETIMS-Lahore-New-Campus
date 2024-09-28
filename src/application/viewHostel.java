package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import DatabaseClasses.hostelDB;

public class viewHostel {
    private TableView<Object[]> hostelTable;
    private hostelDB hosteldb;
    private String hostelname;
    private BorderPane borderPane;
    private static ComboBox<String> searchComboBox;
    private static TextField searchTextField;
    static boolean alertShown = false;
    public viewHostel(Main mainInstance,BorderPane borderPane) {
    	 searchComboBox = new ComboBox<>();
         searchTextField = new TextField();
    	this.borderPane = borderPane;
    	hosteldb = new hostelDB();
    	
    }
    public HBox createTopNavigationBar()  {
        HBox topNavigationBar = new HBox(15);
        //topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20;");
        topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20;");
        ImageView logoImageView = new ImageView(new String("uet.png"));
        logoImageView.setFitHeight(80);
        logoImageView.setPreserveRatio(true);

        // Set the logoImageView to the left corner
       // HBox.setMargin(logoImageView, new Insets(0, 10, 0, 10)); // Adjust margins as needed

        searchTextField = new TextField();
        searchTextField.setMinWidth(170);
        searchTextField.setMaxWidth(170);
        searchTextField.setPrefWidth(170);
        searchTextField.setVisible(false);

        // Add event listener to searchComboBox
        searchComboBox.setVisible(false);
        
        searchComboBox.setOnAction(event -> {
            if (searchTextField == null) {
               // System.out.println("searchTextField is null!");
            } else {
                String selectedSearchType = searchComboBox.getValue();
                if ("Hostel Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Hostel Name");
                } else if ("Number of Floors".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Number of Floors");
                }
                
                }
           
        });

        // Add styles to make searchComboBox look like a button
        searchComboBox.setStyle("-fx-background-color: white; -fx-border-color: #ffffff; -fx-text-fill: white; -fx-font-size: 12;");
        searchComboBox.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        searchComboBox.setMinWidth(150);
        searchComboBox.setMaxWidth(150);
        searchComboBox.setPrefWidth(150);

        // Add event listener to searchTextField
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchComboBox.getSelectionModel().isEmpty()) {
               
					if (!alertShown) {
					    showAlert("Select Search Type", "Please select a search type from the dropdown.");
					    
							alertShown = true;
						
					}
				
                searchTextField.clear();
            } else {
                // Reset the flag if the selection is made
                alertShown = false;

                String searchType = searchComboBox.getValue();
                if ("Hostel Name".equals(searchType)) {
                    searchByHostelName(newValue); // Call the method in viewhostel to update the table
                } else if ("Number of Floors".equals(searchType)) {
                   searchByNumberOfFloors(newValue); // Call the method in viewhostel to update the table
                }
              
              
            }
        });

       


        // Align the elements to the right corner
        HBox rightButtons = new HBox(searchComboBox, searchTextField);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        // Set the rightButtons HBox to take remaining space
        HBox.setHgrow(rightButtons, Priority.ALWAYS);

        topNavigationBar.getChildren().addAll(logoImageView, rightButtons);

        return topNavigationBar;
    }
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }
    @SuppressWarnings("unchecked")
	public TableView<Object[]> createHostelTable() {
    	TableView<Object[]> table = new TableView<>();
        table.setEditable(true);

        // Columns
        TableColumn<Object[], String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        nameColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        locationColumn.setPrefWidth(200);
        
        TableColumn<Object[], String> floorsColumn = new TableColumn<>("Number of Floors");
        floorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        
        TableColumn<Object[], String> wardenColumn = new TableColumn<>("Warden");
        wardenColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        wardenColumn.setPrefWidth(170);
        TableColumn<Object[], String> totalRoomsColumn = new TableColumn<>("Total Rooms");
        totalRoomsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 4)));
       
        // Columns
        TableColumn<Object[], String> idColumn = new TableColumn<>("Hostel Id");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 5)));
        idColumn.setVisible(false); // Make the SupplierId column non-visible
        
        table.getColumns().addAll( nameColumn, locationColumn, floorsColumn, wardenColumn, totalRoomsColumn,idColumn);

        ObservableList<Object[]> hostelData = FXCollections.observableArrayList(hosteldb.hostelInfo());
        table.getItems().setAll(hostelData);
        hostelTable = table;
        return table;
    }
    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }
    public void searchByHostelName(String hostelName) {
        // Implement logic to filter the table by hostelName
        String trimmedHostelName = hostelName.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(hosteldb.searchHostelByName(trimmedHostelName));
        hostelTable.getItems().setAll(filteredData);
    }

    public void searchByNumberOfFloors(String numberOfFloors) {
        // Implement logic to filter the table by numberOfFloors
        String trimmedNumberOfFloors = numberOfFloors.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(hosteldb.searchHostelByFloors(trimmedNumberOfFloors));
        hostelTable.getItems().setAll(filteredData);
    }

    private void openAddHostelPage() {
    	imanaddhostel addHostel = new imanaddhostel(this::refreshHostelTable);
        Stage addHostelStage = new Stage();
        addHostel.start(addHostelStage);

        // Add an event handler to refresh the table when the addhostel stage is closed
        addHostelStage.setOnCloseRequest(event -> refreshHostelTable());
    }
    
    private void openViewRoomPage() {
    	// Get the selected item
        Object[] selectedHostel = hostelTable.getSelectionModel().getSelectedItem();

     
        if (selectedHostel != null) {
            // Open the edithostel page with selected data
        	hostelname = (String) selectedHostel[0];
        	
        	imanViewRooms viewRooms = new imanViewRooms(this,hostelname,borderPane);

        	// Remove previous center and bottom components
        	borderPane.setTop(null);
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            
            
            
            
            // Add the new components to the BorderPane
            borderPane.setTop(viewRooms.createTopNavigationBar());
            viewRooms.setupRoomSearch();
            borderPane.setCenter(viewRooms.createRoomTable());
            borderPane.setBottom(viewRooms.createFooterButtons());
        } else {
            // No row selected, show a warning
        	 Alert alert=  new Alert(Alert.AlertType.WARNING, "Please select a hostel to view rooms.");
        	 // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
        }
    }
    
    public void refreshHostelTable() {
    	
    	hostelTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> hostelData = FXCollections.observableArrayList(hosteldb.hostelInfo());
        hostelTable.getItems().setAll(hostelData);
    }
    
    private void openEditHostelPage(Object[] selectedHostel) {
        imanedithostel editHostel = new imanedithostel(selectedHostel, this::refreshHostelTable,hosteldb);
        Stage editHostelStage = new Stage();
        editHostel.start(editHostelStage);

        // Add an event handler to refresh the table when the edithostel stage is closed
        editHostelStage.setOnCloseRequest(event -> refreshHostelTable());
    }
    private void openHostelSummaryPage() {
        // Implement logic to open the hostel summary page
        // For example:
        HostelSummaryPage hostelSummaryPage = new HostelSummaryPage(); // Create an instance of the HostelSummaryPage class
        Stage hostelSummaryStage = new Stage(); // Create a new stage
        hostelSummaryPage.start(hostelSummaryStage); // Start the HostelSummaryPage with the new stage

        // Add an event handler to refresh the table or perform any other necessary actions when the hostel summary stage is closed
        hostelSummaryStage.setOnCloseRequest(event -> {
            // Perform any necessary actions upon closing the hostel summary stage
            // For example, you might want to refresh the hostel table:
            refreshHostelTable();
        });
    }

    public HBox createFooterButtons() {
        Button editHostelButton = new Button("Edit Hostel");
        Button deleteHostelButton =  new Button("Delete Hostel");
        Button viewRoomsButton = new Button("View Rooms");
        Button addhostelButton = new Button("Add Hostel");
        Button showHostelSummary=new Button("Hostel Summary");

        HBox footButtons = new HBox(10, editHostelButton, deleteHostelButton, viewRoomsButton, addhostelButton,showHostelSummary);
        footButtons.setAlignment(Pos.CENTER_RIGHT);
        footButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom

        
        // Event handler for Edit Hostel button
        editHostelButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedHostel = hostelTable.getSelectionModel().getSelectedItem();

            if (selectedHostel != null) {
                // Open the edithostel page with selected data
                openEditHostelPage(selectedHostel);
            } else {
                // No row selected, show a warning
            	 Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a hostel to edit.");
                // Set the icon image for the alert
               	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
               	alertStage.getIcons().add(new Image("fi.png"));

               	// Show the alert
               	alert.showAndWait();
            }
        });
        // Event handler for Delete Hostel button
        deleteHostelButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedHostel = hostelTable.getSelectionModel().getSelectedItem();

            if (selectedHostel != null) {
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            	confirmationAlert.setTitle("Confirmation");
            	confirmationAlert.setHeaderText("Confirmation");

            	// Set a larger font size for the content text
            	confirmationAlert.getDialogPane().setContentText("Are you sure you want to delete this hostel?\nThis action will delete associated rooms and their allocated items.");
            	 // Set a custom size for the dialog pane
            	confirmationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            	ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            	ButtonType cancelButtonInAlert = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            	 // Set the icon image for the alert
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	
            	confirmationAlert.getButtonTypes().setAll(okButton, cancelButtonInAlert);

            	if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == okButton) {
            	    // User clicked OK, delete the selected row
            	    String hostelName = (String) selectedHostel[0];
            	    hosteldb.deleteHostelInfo(hostelName);
            	    hostelTable.getItems().remove(selectedHostel);

            	    
            	}

            } else {
                // No row selected, show a warning
               Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a hostel to delete.");
               // Set the icon image for the alert
           	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
           	alertStage.getIcons().add(new Image("fi.png"));

           	// Show the alert
           	alert.showAndWait();
            }
        });

        addhostelButton.setOnAction(event -> {
            // Open the addhostel page
            openAddHostelPage();
        });

        viewRoomsButton.setOnAction(event -> {
        	
        	openViewRoomPage();
        	
        });
        
        showHostelSummary.setOnAction(event -> {
            // Open the hostel summary page
            openHostelSummaryPage();
        });

       

        return footButtons;
    }
    public void setupHostelsSearch() {
        searchTextField.setPromptText("");
        // Clear and hide search fields
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Hostels)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Hostel Name", "Number of Floors");
    }
    public void clearFields() {
      	 // Optionally, clear the prompt text
          searchComboBox.setPromptText("");
          searchComboBox.setValue(null); // Set to null to clear the selection
          searchTextField.clear();
      }
      public void showSearchFields(boolean show) {
          searchComboBox.setVisible(show);
          searchTextField.setVisible(show);
      }

}