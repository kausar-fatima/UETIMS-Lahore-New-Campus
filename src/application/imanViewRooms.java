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
import DatabaseClasses.RoomDB;


public class imanViewRooms{
	
    public TableView<Object[]> roomTable;
    private RoomDB roomdb;
    private String hostelname;
    private int roomId;
    private viewHostel viewHostels;

    private static ComboBox<String> searchComboBox;
    private static TextField searchTextField;
    static boolean alertShown = false;
    
	private BorderPane borderPane;
    public imanViewRooms(viewHostel viewHostel,String selectedHostel,BorderPane borderPane) {
    	this.hostelname = selectedHostel;
    	roomdb = new RoomDB();
    	this.viewHostels = viewHostel;
    	this.borderPane = borderPane;
    	 searchComboBox = new ComboBox<>();
         searchTextField = new TextField();
	}
    
    @SuppressWarnings("unchecked")
    public TableView<Object[]> createRoomTable() {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);

        // Columns
        TableColumn<Object[], String> roomIdColumn = new TableColumn<>("Room ID");
        roomIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        roomIdColumn.setVisible(false); // Set the column to be invisible

        TableColumn<Object[], String> roomNoColumn = new TableColumn<>("Room No");
        roomNoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));

        TableColumn<Object[], String> floorNoColumn = new TableColumn<>("Floor No");
        floorNoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        floorNoColumn.setPrefWidth(170);
        TableColumn<Object[], String> categoryColumn = new TableColumn<>("Room Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        categoryColumn.setPrefWidth(170);
        TableColumn<Object[], String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 4)));
        // Columns
        TableColumn<Object[], String> nameColumn = new TableColumn<>("Hostel Id");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 5)));
        nameColumn.setVisible(false); // Make the SupplierId column non-visible
        
        // Add all columns to the table
        table.getColumns().addAll(roomIdColumn, roomNoColumn, floorNoColumn, categoryColumn, quantityColumn,nameColumn);
        ObservableList<Object[]> hostelData = FXCollections.observableArrayList(roomdb.RoomInfo(hostelname));
        table.getItems().setAll(hostelData);
        roomTable = table;
        return table;
    }
    private static <T> String getProperty(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index].toString();
        } else {
            return ""; // or handle this case in a way that makes sense for your application
        }
    }
    public void searchByRoomNo(String roomNo) {
        // Implement logic to filter the table by roomNo
        String trimmedRoomNo = roomNo.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(roomdb.searchByRoomNo(hostelname, trimmedRoomNo));
        roomTable.getItems().setAll(filteredData);
    }

    public void searchByRoomC(String roomCategory) {
        // Implement logic to filter the table by roomCategory
        String trimmedRoomCategory = roomCategory.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(roomdb.searchByRoomC(hostelname, trimmedRoomCategory));
        roomTable.getItems().setAll(filteredData);
    }

    public void searchByRoomF(String floorNo) {
        // Implement logic to filter the table by floorNo
        String trimmedFloorNo = floorNo.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(roomdb.searchByRoomF(hostelname, trimmedFloorNo));
        roomTable.getItems().setAll(filteredData);
    }

    
    public HBox createTopNavigationBar()  {
        HBox topNavigationBar = new HBox(15);
       // topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080) -fx-padding: 20;");
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
                if ("Room Number".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Room Number");
                } else if ("Room Category".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Room Category");
                }
                else if ("Floor".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Floor");
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
              
              if ("Room Number".equals(searchType)) {
                	searchByRoomNo(newValue); // Call the method in viewhostel to update the table
              } else if ("Room Category".equals(searchType)) {
              	searchByRoomC(newValue); // Call the method in viewhostel to update the table
            }
              else if ("Floor".equals(searchType)) {
                	searchByRoomF(newValue); // Call the method in viewhostel to update the table
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

    private void openAddroomPage() {
    	imanAddRooms addroom = new imanAddRooms(this::refreshRoomTable,roomdb,hostelname);
        Stage addroomStage = new Stage();
        addroom.start(addroomStage);

        // Add an event handler to refresh the table when the addroom stage is closed
        addroomStage.setOnCloseRequest(event -> refreshRoomTable());
    }
    
    public void refreshRoomTable() {
    	
    	roomTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> roomData = FXCollections.observableArrayList(roomdb.RoomInfo(hostelname));
        roomTable.getItems().setAll(roomData);
    }
    
    private void openEditroomPage(Object[] selectedRoom) {
        int roomId = (Integer) selectedRoom[0]; // Assuming Room ID is at index 0
        imanEditRooms editroom = new imanEditRooms(selectedRoom, roomId, this::refreshRoomTable, roomdb, hostelname);
        Stage editroomStage = new Stage();
        editroom.start(editroomStage);

        // Add an event handler to refresh the table when the editroom stage is closed
        editroomStage.setOnCloseRequest(event -> refreshRoomTable());
    }

    
    public HBox createFooterButtons() {
        Button editroomButton = new Button("Edit Room");
        Button deleteroomButton = new Button("Delete Room");
        Button viewItemsButton = new Button("View Items");
        Button addroomButton = new Button("Add Room");
        Button viewhostelButton = new Button("View Hostels");

        HBox footerButtons = new HBox(10, editroomButton, deleteroomButton,viewItemsButton, addroomButton, viewhostelButton);
        footerButtons.setAlignment(Pos.CENTER_RIGHT);
        footerButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom

        
     // Event handler for Delete room button
        deleteroomButton.setOnAction(event -> {
            // Get the selected room
            Object[] selectedRoom = roomTable.getSelectionModel().getSelectedItem();

            if (selectedRoom != null) {
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            	confirmationAlert.setTitle("Confirmation");
            	confirmationAlert.setHeaderText("Confirmation");
            	// Set the icon image for the alert
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	
            	if((Integer) selectedRoom[4] == 0) {
            		confirmationAlert.getDialogPane().setContentText("Are you sure you want to delete this room?\nThis action will delete allocated items as well.");
                	
            	}else {
            		confirmationAlert.getDialogPane().setContentText("Are you sure you want to delete this room?");
                	
            	}
            	
            	
            	// Set a custom size for the dialog pane
            	confirmationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            	ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            	ButtonType cancelButtonInAlert = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            	confirmationAlert.getButtonTypes().setAll(okButton, cancelButtonInAlert);

            	if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == okButton) {
               
                    // User clicked OK, delete the selected row
                    int roomId = (Integer) selectedRoom[0]; // Assuming Room ID is at index 0
                    roomdb.deleteRoomInfo(hostelname, roomId);
                    roomTable.getItems().remove(selectedRoom);

                    // Add your logic for deleting the room from the database or any other data source
                    // For now, I'll print a message
                    //System.out.println("Room deleted!");
                }
            } else {
                // No row selected, show a warning
                Alert alert=new Alert(Alert.AlertType.WARNING, "Please select a room to delete.");
             // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
            }
        });

        // Event handler for Edit room button
        editroomButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedRoom = roomTable.getSelectionModel().getSelectedItem();

            if (selectedRoom != null) {
                // Open the editroom page with selected data
                openEditroomPage(selectedRoom);
            } else {
                // No row selected, show a warning
               Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a room to edit.");
            // Set the icon image for the alert
           	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
           	alertStage.getIcons().add(new Image("fi.png"));

           	// Show the alert
           	alert.showAndWait();
            }
        });


        viewItemsButton.setOnAction(event -> {
        	// Get the selected item
            Object[] selectedroom = roomTable.getSelectionModel().getSelectedItem();

            if (selectedroom != null) {
                // Open the  page with selected data
            	roomId = (Integer) selectedroom[0];
            	//int itemquantity = (Integer) selectedroom[4];
            	
            		ViewAllocatedItems viewItems = new ViewAllocatedItems(this,hostelname,roomId, borderPane);
            		// Remove previous center and bottom components
                	borderPane.setTop(null);
                    borderPane.setCenter(null);
                    borderPane.setBottom(null);
                    
                    
                    
                    
                    // Add the new components to the BorderPane
                    borderPane.setTop(viewItems.createTopNavigationBar());
                    viewItems.setupaSearch();

                     // Add the new components to the BorderPane
                    Label selectedCountLabel = new Label();
                     borderPane.setCenter(viewItems.createItemsTable(selectedCountLabel));
                     borderPane.setBottom(viewItems.createFooterButtons());
            		
            	}
            else {
                // No row selected, show a warning
               Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a room to view items.");
             // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
            }
        });
        viewhostelButton.setOnAction(event -> {
        	// Back to hostels page

        	// Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setTop(null);
            
            // Add the new components to the BorderPane
            // Add the new components to the BorderPane
            borderPane.setTop(viewHostels.createTopNavigationBar());
            viewHostels.setupHostelsSearch();
            // Add the new components to the BorderPane
            borderPane.setCenter(viewHostels.createHostelTable());
            borderPane.setBottom(viewHostels.createFooterButtons());
        });

        addroomButton.setOnAction(event -> {
            // Open the addroom page
            openAddroomPage();
        });

        return footerButtons;
    }
    public void setupRoomSearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for requests
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Requests)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll( "Room Number", "Room Category", "Floor");
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