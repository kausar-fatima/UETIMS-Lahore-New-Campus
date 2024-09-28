package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import DatabaseClasses.generateReport;
import application.HostelSummaryPage.HostelSummaryData;

public class GenerateReport extends Application {
    private final generateReport reportGenerator;
    Object selectedIndex = null;
    Integer selectedSupplierId;
    private int yearValue;
    private int monthValue;
    private String cat = null;
    public GenerateReport(int yearValue, int monthValue) {
    	 this.yearValue = yearValue;
         this.monthValue = monthValue;
		reportGenerator = new generateReport(yearValue, monthValue);
		
	}

	public void start(Stage stage) {
    	
    	//System.out.println(reportGenerator.getReportDetails());
    	ObservableList<ItemDetails> supplierData = FXCollections.observableArrayList();
    	ObservableList<ItemDetails> InventoryData = FXCollections.observableArrayList();
        // Create supplier TableView
        TableView<SupplierDetails> supplierTableView = createSupplierTableView();

        // Create category TableView
        TableView<CategoryDetails> categoryTableView = createCategoryTableView();

        // Create item TableView
        TableView<ItemDetails> itemTableView = createItemTableView();
        
       // Create Inventory item TableView
        TableView<ItemDetails> InventoryItemTableView = createInventoryItemTableView();
 
        // Set a listener to show categories when a supplier row is selected
        supplierTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                	Platform.runLater(() -> {
                		supplierData.clear();
                		InventoryData.clear();
                		itemTableView.setItems(supplierData);
                		InventoryItemTableView.setItems(InventoryData);
                	});
                   cat = null;
                   selectedIndex = null;
                   selectedIndex = supplierTableView.getSelectionModel().getSelectedIndex();
                   showCategories(newValue, categoryTableView);     
                }
        );

        // Set a listener to show items when a category row is selected
        categoryTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                	if(selectedIndex!=null) {           
                		cat = null;
                		showAllItems(newValue, itemTableView);
                		showInventoryItems(newValue, InventoryItemTableView);                		
                	}else {
                		// If no supplier is selected, show an alert
                        showAlert("Select Supplier", "Please select a supplier before proceeding.");
                    
                	}
                }
        );
        
        // Create a BorderPane as the root node
        BorderPane root = new BorderPane();

        // Create a label for the title
        Label titleLabel = new Label("Monthly Report of Month: " + monthValue +" of Year: " + yearValue);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setMargin(titleLabel, new Insets(10));

        // Set the title label at the top
        root.setTop(titleLabel);
        
        // Create HBox to hold all three tables
        VBox vBox = new VBox(supplierTableView, categoryTableView, itemTableView, InventoryItemTableView);
        BorderPane.setMargin(vBox, new Insets(10));
        root.setCenter(vBox);

        // Set the title of the stage with the year and month
        stage.setTitle("Monthly Report of Month: " + monthValue +" of Year: " + yearValue);
        Image icon = new Image("fi.png");


        // Set the icon image for the stage
        stage.getIcons().add(icon);
        
        ImageView downloadImageView = new ImageView("dd.png");
        // Set the size of the image icon
        downloadImageView.setFitWidth(20);
        downloadImageView.setFitHeight(20); // Set the height of the image icon
        
        // Create a download button with the image icon
        Button downloadButton = new Button("", downloadImageView);


           downloadButton.setOnAction(e -> {
               try {
                   downloadAsCSV(supplierTableView,categoryTableView,itemTableView,InventoryItemTableView);
               } catch (IOException ex) {
                   ex.printStackTrace();
               }
           });

           // Add the download button in a HBox and position it in the top right corner
           HBox buttonContainer = new HBox(downloadButton);
           buttonContainer.setAlignment(Pos.TOP_RIGHT);
           BorderPane.setMargin(buttonContainer, new Insets(10));
           root.setTop(buttonContainer);

           // Create the scene
           Scene scene = new Scene(root, 800, 600);

           // Display the scene
           stage.setScene(scene);
           stage.show();
    }
	
	private void downloadAsCSV(TableView<SupplierDetails> SupptableView,TableView<CategoryDetails> CattableView,TableView<ItemDetails> ItemtableView,TableView<ItemDetails> InventtableView) throws IOException {
        // Create a file chooser dialog to select the save location
        FileChooser fileChooser = new FileChooser();
        String sname;
        if(selectedSupplierId!=null && cat != null) {
        	sname = reportGenerator.getSuppName(selectedSupplierId);
            fileChooser.setInitialFileName(monthValue+"-"+yearValue+" "+sname+" "+cat+" "+" report"+".csv");
        	
        }else if(selectedSupplierId!=null) {
        	
        	sname = reportGenerator.getSuppName(selectedSupplierId);
            fileChooser.setInitialFileName(monthValue+"-"+yearValue+" "+sname+" report"+".csv");
        }else {
        	fileChooser.setInitialFileName(monthValue+"-"+yearValue+" report"+".csv");
        }
        File file = fileChooser.showSaveDialog(SupptableView.getScene().getWindow());

        if (file != null) {
            // Write the CSV data to the selected file
            try (PrintWriter writer = new PrintWriter(file)) {
                

                if(selectedSupplierId==null) {
                	// Write the header row
                    writer.println("Supplier Name)");
                	// Write the data rows
                    for (SupplierDetails supp : SupptableView.getItems()) {
                        writer.println(String.format("%s",
                                supp.getSupplierName()
                        )
                     );
                   }
                }
                
                if(cat==null) {
                	writer.println("\nCategory");
                    for (CategoryDetails cat : CattableView.getItems()) {
                        writer.println(String.format("%s",
                                cat.getCategory()
                        )
                     );
                   }
                }
                writer.println("\nItem Name, Item Quantity, Total Price");
                for (ItemDetails itm : ItemtableView.getItems()) {
                    writer.println(String.format("%s,%d,%.2f",
                            itm.getItemName(),
                            itm.getTotalItemCount(),
                            itm.getTotalPrice()
                    )
                 );
               }
                writer.println("\nInventory Item Name,Inventory Item Quantity");
                for (ItemDetails IntItm : InventtableView.getItems()) {
                    writer.println(String.format("%s,%d",
                            IntItm.getItemName(),
                            IntItm.getTotalItemCount()
                    )
                 );
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


    private TableView<SupplierDetails> createSupplierTableView() {
        TableView<SupplierDetails> supplierTableView = new TableView<>();
        supplierTableView.setEditable(false);

        TableColumn<SupplierDetails, String> supplierColumn = new TableColumn<>("Supplier Name");
        supplierColumn.setCellValueFactory(param -> param.getValue().supplierNameProperty());
        supplierColumn.setPrefWidth(170);
        ObservableList<SupplierDetails> supplierData = FXCollections.observableArrayList();
        Map<Integer, Map<String, Map<String, Map<String, Object>>>> supplierDetails = reportGenerator.getReportDetails();
        supplierDetails.forEach((supplierId, nameMap) ->
        {
        	nameMap.forEach((supplierName, categoryMap) -> {
        		supplierData.add(new SupplierDetails(supplierName));
        	}
          );
        }
               
        );
         
        supplierTableView.setItems(supplierData);

        supplierTableView.getColumns().add(supplierColumn);
        
     // Set the preferred width for the supplierTableView
        supplierTableView.setPrefWidth(210); // Adjust this value as needed

        return supplierTableView;
    }

    private TableView<CategoryDetails> createCategoryTableView() {
        TableView<CategoryDetails> categoryTableView = new TableView<>();
        categoryTableView.setEditable(false);

        TableColumn<CategoryDetails, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(param -> param.getValue().categoryProperty());
        categoryColumn.setPrefWidth(170);
        ObservableList<CategoryDetails> categoryData = FXCollections.observableArrayList();

        ObservableList<Object> Details = reportGenerator.getCategories();

       // System.out.println("----------------"+Details+"------------------");
        if(!Details.isEmpty()) {
        	
                // Set the item data to the itemTableView
                for (int i = 0; i < Details.size(); i++) {
                	String catname = (String) Details.get(i);

                    // Create ItemDetails object for each item and add to itemData list
                    CategoryDetails itemDetail = new CategoryDetails(catname);
                    // System.out.println(itemDetail.getItemName() + " | " + itemDetail.getTotalItemCount() + " | " + itemDetail.getTotalPrice());

                    //categoryData.clear();
                    categoryData.add(itemDetail);
                    categoryTableView.setItems(categoryData);
                }
            
        }else {
           // System.out.println("categoryDetailsMap is null or empty.");
            
            Platform.runLater(() -> {
            	categoryData.clear();
            	
            	categoryTableView.setItems(categoryData);
            });
        }

        
        categoryTableView.getColumns().add(categoryColumn);
        
        // Set the preferred width for the categoryTableView
        categoryTableView.setPrefWidth(210); // Adjust this value as needed


        return categoryTableView;
    }


    private TableView<ItemDetails> createItemTableView() {
        TableView<ItemDetails> itemTableView = new TableView<>();
        itemTableView.setEditable(false);

        TableColumn<ItemDetails, String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(param -> param.getValue().itemNameProperty());
        itemNameColumn.setPrefWidth(170);
        TableColumn<ItemDetails, Integer> totalItemCountColumn = new TableColumn<>("Total Items");
        totalItemCountColumn.setCellValueFactory(param -> param.getValue().totalItemCountProperty().asObject());

        TableColumn<ItemDetails, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(param -> param.getValue().totalPriceProperty().asObject());

        ObservableList<ItemDetails> itemData = FXCollections.observableArrayList();
        ObservableList<Object> Details = reportGenerator.getItems();

       // System.out.println("----------------"+Details+"------------------");
        if(!Details.isEmpty()) {
        	
                // Set the item data to the itemTableView
                for (int i = 0; i < Details.size(); i++) {
                	String Itemname = (String) Details.get(i);
                    i++;
                    int Itemcount = (int) Details.get(i);
                    i++;
                    double totalprice = (double) Details.get(i);
                    // Create ItemDetails object for each item and add to itemData list
                    ItemDetails itemDetail = new ItemDetails(Itemname,Itemcount,totalprice);
                    // System.out.println(itemDetail.getItemName() + " | " + itemDetail.getTotalItemCount() + " | " + itemDetail.getTotalPrice());

                    //categoryData.clear();
                    itemData.add(itemDetail);
                    itemTableView.setItems(itemData);
                }
            
        }else {
           // System.out.println("categoryDetailsMap is null or empty.");
            
            Platform.runLater(() -> {
            	itemData.clear();
            	
            	itemTableView.setItems(itemData);
            });
        }
        itemTableView.getColumns().addAll(itemNameColumn, totalItemCountColumn, totalPriceColumn);

        // Set the preferred width for the itemsTableView
        itemTableView.setPrefWidth(420); // Adjust this value as needed

        return itemTableView;
    }
    
    private TableView<ItemDetails> createInventoryItemTableView() {
        TableView<ItemDetails> itemTableView = new TableView<>();
        itemTableView.setEditable(false);

        TableColumn<ItemDetails, String> itemNameColumn = new TableColumn<>("Inventory Item Name");
        itemNameColumn.setCellValueFactory(param -> param.getValue().itemNameProperty());
        itemNameColumn.setPrefWidth(170);
        TableColumn<ItemDetails, Integer> totalItemCountColumn = new TableColumn<>("Total Items");
        totalItemCountColumn.setCellValueFactory(param -> param.getValue().totalItemCountProperty().asObject());

        ObservableList<ItemDetails> itemData = FXCollections.observableArrayList();
        List<Object> itemDetailsList = reportGenerator.getInventoryItems();
        
        for(int i =0; i<itemDetailsList.size(); i++) {
        	String name = (String) itemDetailsList.get(i);
        	i++;
            
            int totalItemCount = ((Number) itemDetailsList.get(i)).intValue();

             //Create ItemDetails object for each item and add to itemData list
            ItemDetails itemDetail = new ItemDetails(name, totalItemCount);
            //System.out.println(itemDetail.getItemName() + " | " + itemDetail.getTotalItemCount() + " | " + itemDetail.getTotalPrice());

            itemData.add(itemDetail);

            itemTableView.setItems(itemData);
        }
        
        itemTableView.getColumns().addAll(itemNameColumn, totalItemCountColumn);

        // Set the preferred width for the itemsTableView
        itemTableView.setPrefWidth(380); // Adjust this value as needed

        return itemTableView;
    }

    private void showCategories(SupplierDetails supplierDetails, TableView<CategoryDetails> categoryTableView) {
        if (supplierDetails != null) {
            // Retrieve the selected supplier index using the selectedIndex
            int selectedSupplierIndex = (int) selectedIndex;

            ObservableList<CategoryDetails> categoryData = FXCollections.observableArrayList();

         // Corrected key set retrieval
            Set<Integer> supplierIds = reportGenerator.getReportDetails().keySet();
            List<Integer> supplierIdList = new ArrayList<>(supplierIds);

            if (selectedSupplierIndex >= 0 && selectedSupplierIndex < supplierIdList.size()) {
                selectedSupplierId = supplierIdList.get(selectedSupplierIndex);
               // System.out.println("------------"+selectedSupplierId+"------------");
                Map<String, Map<String, Map<String, Object>>> idDetails = reportGenerator.getReportDetails().get(selectedSupplierId);
                //System.out.println(nameDetails);
                if (idDetails != null) {
                    idDetails.forEach((suppname, category) ->{
                    	//System.out.println(suppname);
                    	category.forEach((categoryDetails, itemDetails) -> {
                    		categoryData.add(new CategoryDetails(categoryDetails));
                    		//System.out.println(categoryDetails);
                    	});
                    }
                  );
                }
                categoryTableView.setItems(categoryData);
            }

        }
    }
    
    private void showAllItems(CategoryDetails category, TableView<ItemDetails> itemTableView) {
    	ObservableList<ItemDetails> itemData = FXCollections.observableArrayList();
        if (category != null) {
        	ObservableList<Object> Details = reportGenerator.getdesireItems(selectedSupplierId, category.getCategory());
            cat = category.getCategory();
           // System.out.println("----------------"+Details+"------------------");
            if(!Details.isEmpty()) {
            	Platform.runLater(() -> {
                    // Set the item data to the itemTableView
                    for (int i = 0; i < Details.size(); i++) {
                    	String itmname = (String) Details.get(i);
                    	i++;
                        int totalItemCount = ((Number) Details.get(i)).intValue();
                        i++;
                        double totalPrice = ((Number) Details.get(i)).doubleValue();

                        // Create ItemDetails object for each item and add to itemData list
                        ItemDetails itemDetail = new ItemDetails(itmname, totalItemCount,totalPrice);
                        // System.out.println(itemDetail.getItemName() + " | " + itemDetail.getTotalItemCount() + " | " + itemDetail.getTotalPrice());

                        
                        itemData.add(itemDetail);
                        itemTableView.setItems(itemData);
                    }
                });
            }else {
               // System.out.println("categoryDetailsMap is null or empty.");
                
                Platform.runLater(() -> {
                	itemData.clear();
                	
                    itemTableView.setItems(itemData);
                });
            }
                    
                } else {
                   // System.out.println("category is null or empty.");
                    
                    Platform.runLater(() -> {
                    	itemData.clear();
                    	
                        itemTableView.setItems(itemData);
                    });
                }
        }
    
    
    // Get Inventory Items
    private void showInventoryItems(CategoryDetails category,TableView<ItemDetails> InventoryItemTableView) {

        ObservableList<ItemDetails> itemData = FXCollections.observableArrayList();

        if (category!= null) {
                ObservableList<Object> Details = reportGenerator.getdesireInventory(selectedSupplierId, category.getCategory());

                //System.out.println("----------------"+Details+"------------------");
                if(!Details.isEmpty()) {
                	Platform.runLater(() -> {
                        // Set the item data to the itemTableView
                        for (int i = 0; i < Details.size(); i++) {
                        	String itmname = (String) Details.get(i);
                        	i++;
                            int totalItemCount = ((Number) Details.get(i)).intValue();

                            // Create ItemDetails object for each item and add to itemData list
                            ItemDetails itemDetail = new ItemDetails(itmname, totalItemCount);
                            // System.out.println(itemDetail.getItemName() + " | " + itemDetail.getTotalItemCount() + " | " + itemDetail.getTotalPrice());
 
                            //itemData.clear();
                            itemData.add(itemDetail);
                            InventoryItemTableView.setItems(itemData);
                        }
                    });
                }else {
                   // System.out.println("categoryDetailsMap is null or empty.");
                    
                    Platform.runLater(() -> {
                    	itemData.clear();
                    	
                        InventoryItemTableView.setItems(itemData);
                    });
                }
                        
                    } else {
                      //  System.out.println("category is null or empty.");
                        
                        Platform.runLater(() -> {
                        	itemData.clear();
                        	
                            InventoryItemTableView.setItems(itemData);
                        });
                    }
                } 
            
        
    
    // Method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class SupplierDetails {
        private final StringProperty supplierName;

        public SupplierDetails(String supplierName) {
            this.supplierName = new SimpleStringProperty(supplierName);
        }

        public String getSupplierName() {
            return supplierName.get();
        }

        public StringProperty supplierNameProperty() {
            return supplierName;
        }
    }

    public static class CategoryDetails {
        private final StringProperty category;

        public CategoryDetails(String category) {
            this.category = new SimpleStringProperty(category);
        }

        public CategoryDetails(String catname, int totalCount) {
        	this.category = new SimpleStringProperty(catname);
		}

		public String getCategory() {
            return category.get();
        }

        public StringProperty categoryProperty() {
            return category;
        }
    }

    public static class ItemDetails {
        private final StringProperty itemName;
        private final IntegerProperty totalItemCount;
        private final DoubleProperty totalPrice;

        public ItemDetails(String itemName, int totalItemCount, double totalPrice) {
            this.itemName = new SimpleStringProperty(itemName);
            this.totalItemCount = new SimpleIntegerProperty(totalItemCount);
            this.totalPrice = new SimpleDoubleProperty(totalPrice);
        }

        public ItemDetails(String name, int totalItemCount2) {
        	this.itemName = new SimpleStringProperty(name);
            this.totalItemCount = new SimpleIntegerProperty(totalItemCount2);
			this.totalPrice = new SimpleDoubleProperty();
		}

		public ItemDetails(String name) {
			this.itemName = new SimpleStringProperty(name);
            this.totalItemCount = new SimpleIntegerProperty();
			this.totalPrice = new SimpleDoubleProperty();
		}

		public String getItemName() {
            return itemName.get();
        }

        public StringProperty itemNameProperty() {
            return itemName;
        }

        public int getTotalItemCount() {
            return totalItemCount.get();
        }

        public IntegerProperty totalItemCountProperty() {
            return totalItemCount;
        }

        public double getTotalPrice() {
            return totalPrice.get();
        }

        public DoubleProperty totalPriceProperty() {
            return totalPrice;
        }

        // Add these update methods to allow for direct property updates
        public void setTotalItemCount(int totalItemCount) {
            this.totalItemCount.set(totalItemCount);
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice.set(totalPrice);
        }
    }
}


