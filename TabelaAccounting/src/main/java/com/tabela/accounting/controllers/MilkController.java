package com.tabela.accounting.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.DialogType;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MilkController implements Initializable {

	@FXML
    private ComboBox<MilkCustomer> cmbCustomer;

    @FXML
    private DatePicker txtMilkDate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnClearSearch;

    @FXML
    private Button btnAddCustomer;

    @FXML
    private VBox content;

    @FXML
    private TableView<CustomerMilk> tableMilk;

    @FXML
    private TableColumn colCustomerName;

    @FXML
    private TableColumn colMilkDate;

    @FXML
    private TableColumn colMorningMilk;

    @FXML
    private TableColumn colEveningMilk;

    @FXML
    private TableColumn colMilkRate;

    @FXML
    private TableColumn colEdit;

    @FXML
    private TableColumn colDelete;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Rectangle2D screen = TabelaAccounting.getScreen();
    	tableMilk.setMinHeight(screen.getHeight() - 150);
		
    	setTable(getMilks());
    }
    
    public void setTable(List<CustomerMilk> milks) {
    	
    	cmbCustomer.setItems(FXCollections.observableArrayList(MilkCustomerController.getCustomers()));
		cmbCustomer.setConverter(new StringConverter<MilkCustomer>() {
			
			@Override
			public String toString(MilkCustomer object) {
				// TODO Auto-generated method stub
				return object.getCustomerName();
			}
			
			@Override
			public MilkCustomer fromString(String string) {
				// TODO Auto-generated method stub
				return cmbCustomer.getItems().stream().filter(customer -> 
								customer.getCustomerName().equals(string)).findFirst().orElse(null);
			}
		});
		
		colCustomerName.setCellValueFactory(new PropertyValueFactory("custName"));
		colMilkDate.setCellValueFactory(new PropertyValueFactory("strMilkDate"));
		colMorningMilk.setCellValueFactory(new PropertyValueFactory("morningMilk"));
		colEveningMilk.setCellValueFactory(new PropertyValueFactory("eveningMilk"));
		colMilkRate.setCellValueFactory(new PropertyValueFactory("milkRate"));
		

        Callback<TableColumn<CustomerMilk, Void>, TableCell<CustomerMilk, Void>> cellFactoryEditButton = new Callback<TableColumn<CustomerMilk, Void>, TableCell<CustomerMilk, Void>>() {
            @Override
            public TableCell<CustomerMilk, Void> call(final TableColumn<CustomerMilk, Void> param) {
                final TableCell<CustomerMilk, Void> cell = new TableCell<CustomerMilk, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		CustomerMilk milkCustomer = getTableView().getItems().get(getIndex());
                            showForm(milkCustomer);
                        });
                    	button.setMinWidth(50);
                    	button.getStyleClass().setAll("btnEdit");
                    	
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
        
        Callback<TableColumn<CustomerMilk, Void>, TableCell<CustomerMilk, Void>> cellFactoryDeleteButton = new Callback<TableColumn<CustomerMilk, Void>, TableCell<CustomerMilk, Void>>() {
            @Override
            public TableCell<CustomerMilk, Void> call(final TableColumn<CustomerMilk, Void> param) {
                final TableCell<CustomerMilk, Void> cell = new TableCell<CustomerMilk, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to delete this milk?", DialogType.YESNOCANCEL, null);
                    		if (response == FXOptionPane.Response.YES){
                    			CustomerMilk data = getTableView().getItems().get(getIndex());
                                FacadeFactory.getFacade().delete(data);
                                setTable(getMilks());
                    		}
                        });
                    	button.setMinWidth(50);
                    	button.getStyleClass().setAll("btnDelete");
                    	
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
 
        colEdit.setCellFactory(cellFactoryEditButton);
        colDelete.setCellFactory(cellFactoryDeleteButton);
        
        tableMilk.setItems(FXCollections.observableArrayList(milks));
	}

    @FXML
	void addMilk(ActionEvent event) {
		// New window (Stage)
		showForm(new CustomerMilk());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		cmbCustomer.setValue(null);
		txtMilkDate.setValue(null);
		setTable(getMilks());
	}

	@FXML
	void search(ActionEvent event) {
		if(cmbCustomer.getValue() == null){
			DialogFactory.showErrorDialog("Please select customer", TabelaAccounting.stage);
			return;
		}
		if(txtMilkDate.getValue() == null){
			DialogFactory.showErrorDialog("Please select milk date", TabelaAccounting.stage);
			return;
		}
		List<CustomerMilk> milks = getMilks(cmbCustomer.getValue(), txtMilkDate.getValue());
		if(milks == null || milks.size() == 0){
			DialogFactory.showInformationDialog("No milk founds for selected customer and date", TabelaAccounting.stage);
			return;
		}
		setTable(milks);
	}
    
    public void showForm(CustomerMilk milk){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMilkForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddMilkFormController controller = (AddMilkFormController)loader.getController();
	        controller.setCustomerMilk(milk);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Milk");
	        stage.initStyle(StageStyle.UTILITY);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initOwner(TabelaAccounting.stage);
	        stage.setResizable(false);
	        stage.setScene(scene);
	        //stage.setX(150);
	        //stage.setY(150);
	        stage.show();
	       
	        controller.setStage(stage);
		       
	        stage.setOnHidden(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					setTable(getMilks());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public static List<CustomerMilk> getMilks() {
		return FacadeFactory.getFacade().list(CustomerMilk.class);
	}

	public static List<CustomerMilk> getMilks(MilkCustomer customer, LocalDate localDate) {
		 String queryStr = "Select m From CustomerMilk m where m.customer = :cid and m.milkDate = :dt";
		 Map<String, Object> parameters = new HashMap();
		 parameters.put("cid", customer);
		 parameters.put("dt", new Timestamp(AppUtil.toUtilDate(localDate).getTime()));
		 return FacadeFactory.getFacade().list(queryStr, parameters);
	}

}