package application;

import java.net.MalformedURLException;

import DatabaseClasses.AdminDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane borderPane;
    private viewHostel viewhostel;
    private InventoryView inventoryview;
    private SupplierView supplierview;
    private RequestView requestview;
    private DashBoard homePage;
   private Report report;
   private SettingsPane user;
  // private SettingsPane settingsPane; // Change the type to SettingsPane
    private categoryview categoryview;
    // Declare searchComboBox as a field
    private ComboBox<String> searchComboBox;
    private TextField searchTextField;
    
    boolean alertShown = false;
    
    public Main(String email,String password) {
        borderPane = new BorderPane();
        // Initialize searchComboBox and searchTextField before creating viewHostel
        report=new Report();
        searchComboBox = new ComboBox<>();
        searchTextField = new TextField();
        categoryview=new categoryview(borderPane);
        viewhostel = new viewHostel(this, borderPane);
        inventoryview = new InventoryView(borderPane);
        supplierview = new SupplierView(borderPane);
        requestview = new RequestView();
        new AdminDB();
        // Initialize the SettingsPane with email and password
        //settingsPane = new SettingsPane(email, password);
        user = new SettingsPane(email, password);
        homePage = new DashBoard(borderPane,email, password);
        
    }

    @Override
    public void start(Stage primaryStage) throws MalformedURLException, ClassNotFoundException {
        primaryStage.setTitle("UET Inventory Management System");
        Image icon = new Image("fi.png");

        // Set the icon image for the stage
        primaryStage.getIcons().add(icon);

        // Top Navigation Bar
        HBox topNavigationBar = createTopNavigationBar();

        // Left Navigation Pane
        VBox leftNavigationPane = createLeftNavigationPane();

        // Main Layout - BorderPane

        borderPane.setTop(homePage.createTopNavigationBar(true)); // Set the modified header
        borderPane.setLeft(leftNavigationPane);
        borderPane.setCenter(homePage.createBarChart());
        borderPane.setRight(homePage.createPieChart());
        borderPane.setBottom(null);

        // Scene
        Scene scene = new Scene(borderPane, 800, 600);
        // Set the stage to be initially maximized
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private HBox createTopNavigationBar() throws MalformedURLException {
        HBox topNavigationBar = new HBox(15);
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
                //System.out.println("searchTextField is null!");
            } else {
                String selectedSearchType = searchComboBox.getValue();
                if ("Hostel Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Hostel Name");
                } else if ("Number of Floors".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Number of Floors");
                } else if ("Item Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Name");
                } else if ("Item Category".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Category");
                } else if ("Supplier Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Supplier Name");
                } else if ("Room Number".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Room Number");
                } else if ("Hostel".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Hostel");
                } else if ("Room No.".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Room No.");
                } else if ("Items".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Name");
                } else if ("Category".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Category");
                } else if ("Status".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Status");
                }
                else if ("Categories".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Category");
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
                    viewhostel.searchByHostelName(newValue); // Call the method in viewhostel to update the table
                } else if ("Number of Floors".equals(searchType)) {
                    viewhostel.searchByNumberOfFloors(newValue); // Call the method in viewhostel to update the table
                } else if ("Item Name".equals(searchType)) {
                    inventoryview.searchByItemName(newValue); // Call the method in inventoryview to update the table
                } else if ("Item Category".equals(searchType)) {
                    inventoryview.searchByItemCategory(newValue); // Call the method in inventoryview to update the table
                }
                else if ("Supplier Name".equals(searchType)) {
                	supplierview.searchBySupplierName(newValue); // Call the method in supplierview to update the table
                }
                else if ("Hostel".equals(searchType)) {
                	requestview.searchByHostel(newValue); // Call the method in viewhostel to update the table
                }
                else if ("Room No.".equals(searchType)) {
                	requestview.searchByRoomNo(newValue); // Call the method in imanViewRooms to update the table
                }
                else if ("Items".equals(searchType)) {
                    requestview.searchByItems(newValue); // Call the method in inventoryview to update the table
                }
                else if ("Category".equals(searchType)) {
                	requestview.searchByItemCategory(newValue); // Call the method in inventoryview to update the table
                }
                else if ("Status".equals(searchType)) {
                    requestview.searchByStatus(newValue); // Call the method in inventoryview to update the table
                } 
                else if ("Categories".equals(searchType)) {
                    categoryview.searchByCategory(newValue); // Call the method in inventoryview to update the table
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
    // Getter method for searchComboBox
    public ComboBox<String> getSearchComboBox() {
        return searchComboBox;
    }

    // Getter method for searchTextField
    public TextField getSearchTextField() {
        return searchTextField;
    }

    // Setter method for searchComboBox
    public void setSearchComboBox(ComboBox<String> searchComboBox) {
        this.searchComboBox = searchComboBox;
    }

    // Setter method for searchTextField
    public void setSearchTextField(TextField searchTextField) {
        this.searchTextField = searchTextField;
    }                    
    
    public void clearFields() {
        searchComboBox.setValue(null); // Set to null to clear the selection
        // Optionally, clear the prompt text
        searchComboBox.setPromptText("");
        searchTextField.clear();
    }
    public void showSearchFields(boolean show) {
        searchComboBox.setVisible(show);
        searchTextField.setVisible(show);
    }


    private VBox createLeftNavigationPane() {
    	ImageView homeImageView = new ImageView(new String("hi.png"));
    	ImageView SupplierImageView = new ImageView(new String("si.png"));
    	ImageView HostelImageView = new ImageView(new String("hoi.png"));
    	ImageView CategoryImageView = new ImageView(new String("ci.png"));
    	ImageView InventoryImageView = new ImageView(new String("ii.png"));
    	ImageView RequestImageView = new ImageView(new String("ri.png"));
    	ImageView LogoutImageView = new ImageView(new String("li.png"));
    	
    	// Set the size of the image icons
    	homeImageView.setFitWidth(20);
    	homeImageView.setFitHeight(20);
    	CategoryImageView.setFitWidth(20);
    	CategoryImageView.setFitHeight(20);
    	InventoryImageView.setFitWidth(20);
    	InventoryImageView.setFitHeight(20);
    	SupplierImageView.setFitWidth(20);
    	SupplierImageView.setFitHeight(20);
    	HostelImageView.setFitWidth(20);
    	HostelImageView.setFitHeight(20);
    	RequestImageView.setFitWidth(20);
    	RequestImageView.setFitHeight(20);
    	
    	LogoutImageView.setFitWidth(20);
    	LogoutImageView.setFitHeight(20);
    	
    	 VBox leftNavigationPane = new VBox(15);
         leftNavigationPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 10;");
         leftNavigationPane.setMaxSize(200, 2000);
         // Menu buttons
         Button HomeButton = new Button("Home",homeImageView);
         
         HomeButton.setMinSize(100, 30);
         Button CategoryButton =  new Button("Category",CategoryImageView);
         CategoryButton.setMinSize(100, 30);
         Button InventoryButton =  new Button("Inventory",InventoryImageView);
         InventoryButton.setMinSize(100, 30);
         Button SuppliersButton = new Button("Suppliers",SupplierImageView);
         SuppliersButton.setMinSize(100, 30);
         Button HostelsButton = new Button("Hostels",HostelImageView);
         HostelsButton.setMinSize(100, 30);
         Button RequestsButton = new Button("Requisition",RequestImageView);
         RequestsButton.setMinSize(100, 30);
         
         Button gButton=new Button("Generate Report");
         gButton.setMinSize(100, 30);
        // Add the Settings button
       // Button settingsButton = new Button("Settings");
        Button LogoutButton = new Button("Log out",LogoutImageView);
        LogoutButton.setMinSize(100, 30);
        
        VBox.setMargin(LogoutButton, new Insets(100, 0, 0, 0)); // Add some margin
        // Add navigation items
        leftNavigationPane.getChildren().addAll(
                HomeButton,
                CategoryButton,
                InventoryButton,
                SuppliersButton,
                HostelsButton,
                RequestsButton,
                gButton,
               
                LogoutButton
        );
      
        CategoryButton.setOnAction(event -> {
         
       	 // Clear and hide search fields
           clearFields();
           showSearchFields(false); // Set both the ComboBox and TextField to be invisible
       	// Remove previous center and bottom components
           user.resetSettingsPane();
           borderPane.setCenter(null);
           borderPane.setBottom(null);
           borderPane.setRight(null);
           borderPane.setTop(null);
           setupCategoriesSearch();
           // Add the new components to the BorderPane
           try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           setupCategoriesSearch();
           searchComboBox.setPromptText("Select search type");
           // Add the new components to the BorderPane
           
           borderPane.setCenter(categoryview.createCategoryTable());
           borderPane.setBottom(categoryview.createFooterButtons());
           
       });

        gButton.setOnAction(event -> {
        	
        	user.resetSettingsPane();
            // Open the new page for generating a report
            // You can replace the following line with your actual logic to open the new page
            report = new Report();
            report.start(new Stage()); // This line is important to actually show the stage
        });
        HomeButton.setOnAction(event -> {
        	 user.resetSettingsPane();
            // Clear and hide search fields
            clearFields();
            showSearchFields(false); // Set both the ComboBox and TextField to be invisible
           
            // Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setRight(null);
            borderPane.setTop(null);
            // Add the new components to the BorderPane
            try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // Add the new components to the BorderPane
            // Add the new components to the BorderPane
            try {
				borderPane.setTop(homePage.createTopNavigationBar(true));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
                borderPane.setCenter(homePage.createBarChart());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                borderPane.setRight(homePage.createPieChart());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //borderPane.setBottom(new Label("Total: 1000"));
        });

    

        
        SuppliersButton.setOnAction(event->{
        	
        	// Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setRight(null);
            borderPane.setTop(null);
            setupSuppliersSearch(); 
            user.resetSettingsPane();
            // Add the new components to the BorderPane
            try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            setupSuppliersSearch(); 
            searchComboBox.setPromptText("Select search type");

            // Add the new components to the BorderPane
            borderPane.setCenter(supplierview.createSupplierTable());
            borderPane.setBottom(supplierview.createFooterButtons());
        });
        
        HostelsButton.setOnAction(event -> {
        	user.resetSettingsPane();

            // Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setRight(null);
            borderPane.setTop(null);
           //for search
            setupHostelsSearch();
            // Add the new components to the BorderPane
            try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            setupHostelsSearch();
            searchComboBox.setPromptText("Select search type");
         
            // Add the new components to the BorderPane
            borderPane.setCenter(viewhostel.createHostelTable());
            borderPane.setBottom(viewhostel.createFooterButtons());
        });

        InventoryButton.setOnAction(event -> {
        	user.resetSettingsPane();

            // Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setRight(null);
            borderPane.setTop(null);
            setupInventorySearch();
         // Add the new components to the BorderPane
            try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            setupInventorySearch();
            searchComboBox.setPromptText("Select search type");
            
         // Create a Label instance
            Label selectedCountLabel = new Label();

            // Add the TableView and Label to the BorderPane
            borderPane.setCenter(inventoryview.createInventoryTable(selectedCountLabel));
            borderPane.setBottom(inventoryview.createFooterButtons());
        });



        
        RequestsButton.setOnAction(event->{
        	user.resetSettingsPane();
        	// Remove previous center and bottom components
            borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setRight(null);
            borderPane.setTop(null);
            
            setupRequestsSearch();
            
            // Add the new components to the BorderPane
            try {
				borderPane.setTop(createTopNavigationBar());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            setupRequestsSearch();
            searchComboBox.setPromptText("Select search type");
            borderPane.setCenter(requestview.createRequestTable());
            borderPane.setBottom(requestview.createFooterButtons());
        });
        

        LogoutButton.setOnAction(event -> {
        	user.resetSettingsPane();
            // Create a confirmation dialog
            Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Logout");
            confirmationDialog.setContentText("Are you sure you want to logout?");

            // Customize the buttons in the confirmation dialog
            ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            confirmationDialog.getButtonTypes().setAll(okButton, cancelButton);
            // Set the icon image for the alert
            Stage alertStage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("fi.png"));
            // Show the confirmation dialog and handle the result
            confirmationDialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == okButton) {
                    // User clicked OK, close the application
                    closeCurrentStage();
                    Platform.exit();
                    
                }
            });
        });

       
        return leftNavigationPane;
    }
    
    private void closeCurrentStage() {
        Stage currentStage = (Stage) borderPane.getScene().getWindow();
        currentStage.close();
  
    }    
    public static void main(String[] args) {
        launch(args);
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
    private void setupHostelsSearch() {
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
    private void setupInventorySearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for inventory
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Inventory)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Item Name", "Item Category");
    }

    private void setupSuppliersSearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for suppliers
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Suppliers)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Supplier Name");
    }

    private void setupRequestsSearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for requests
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Requests)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Hostel", "Room No.", "Items", "Category", "Status");
    }
    private void setupCategoriesSearch() {
        searchTextField.setPromptText("");
        
        // Clear and show search fields for requests
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Requests)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Categories");
    }
    
   
}
   