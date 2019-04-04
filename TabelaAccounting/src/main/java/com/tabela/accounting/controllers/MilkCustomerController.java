package com.tabela.accounting.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.DialogType;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MilkCustomerController implements Initializable {

	@FXML
	private TextField txtSearchText;

	@FXML
	private Button btnSearch;

	@FXML
	private Button btnClearSearch;

	@FXML
	private Button btnAddCustomer;

	@FXML
	private VBox content;

	@FXML
	private TableView<MilkCustomer> customerTable;

	@FXML
	private TableColumn colCustomerName;

	@FXML
	private TableColumn colCreatedDate;

	@FXML
	private TableColumn colCustomerAddress;

	@FXML
	private TableColumn colMilkRate;

	@FXML
	private TableColumn colPendingBill;

	@FXML
	private TableColumn colActive;

	@FXML
	private TableColumn btnEdit;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		customerTable.setMinHeight(screen.getHeight() - 150);
		
		setTable(getCustomers());
	}

	@FXML
	void addCustomer(ActionEvent event) {
		// New window (Stage)
		showForm(new MilkCustomer());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		txtSearchText.setText("");
		setTable(getCustomers());
	}

	@FXML
	void search(ActionEvent event) {
		if(txtSearchText.getText().length() == 0){
			DialogFactory.showErrorDialog("Please enter search text", TabelaAccounting.stage);
			return;
		}
		
		setTable(getCustomers(txtSearchText.getText()));
	}

	public void setTable(List<MilkCustomer> customers) {
		colCustomerName.setCellValueFactory(new PropertyValueFactory("customerName"));
		colCreatedDate.setCellValueFactory(new PropertyValueFactory("strJoinDate"));
		colCustomerAddress.setCellValueFactory(new PropertyValueFactory("customerAddress"));
		colMilkRate.setCellValueFactory(new PropertyValueFactory("milkRate"));
		colPendingBill.setCellValueFactory(new PropertyValueFactory("billAmount"));
		
        Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>> cellFactory = new Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>>() {
            @Override
            public TableCell<MilkCustomer, Void> call(final TableColumn<MilkCustomer, Void> param) {
                final TableCell<MilkCustomer, Void> cell = new TableCell<MilkCustomer, Void>() {

                    private final CheckBox cb = new CheckBox();
                    {
                    	cb.setOnAction((ActionEvent event) -> {
                    		CheckBox chb = (CheckBox)event.getSource();
                    		if(chb.isSelected()){
                    			FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to deactivate this customr?", DialogType.YESNOCANCEL, null);
                        		if (response == FXOptionPane.Response.YES){
                        			MilkCustomer data = getTableView().getItems().get(getIndex());
                                    data.setActive(false);
                                    FacadeFactory.getFacade().store(data);
                        		}
                    		}
                    		else{
                    			FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to activate this customr?", DialogType.YESNOCANCEL, null);
                        		if (response == FXOptionPane.Response.YES){
                        			MilkCustomer data = getTableView().getItems().get(getIndex());
                                    data.setActive(true);
                                    FacadeFactory.getFacade().store(data);
                        		}
                    		}
                    		
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(cb);
                        }
                    }
                };
                return cell;
            }
        };
        
        Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>> cellFactoryEditButton = new Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>>() {
            @Override
            public TableCell<MilkCustomer, Void> call(final TableColumn<MilkCustomer, Void> param) {
                final TableCell<MilkCustomer, Void> cell = new TableCell<MilkCustomer, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		MilkCustomer milkCustomer = getTableView().getItems().get(getIndex());
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

        colCreatedDate.setVisible(false);
        
        colActive.setCellFactory(cellFactory);
        btnEdit.setCellFactory(cellFactoryEditButton);
        
        customerTable.setItems(FXCollections.observableArrayList(customers));
	}
	
	public void showForm(MilkCustomer customer){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMilkCustomerForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddMilkCustomerController controller = (AddMilkCustomerController)loader.getController();
			 controller.setCustomer(customer);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Customer");
	        stage.initStyle(StageStyle.UTILITY);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initOwner(TabelaAccounting.stage);
	        stage.setResizable(false);
	        stage.setScene(scene);
	        stage.show();
	       
	        controller.setStage(stage);
		       
	        stage.setOnHidden(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					setTable(getCustomers());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<MilkCustomer> getCustomers() {
		return FacadeFactory.getFacade().list(MilkCustomer.class);
	}

	public static List<MilkCustomer> getBranchCustomers() {
		return FacadeFactory.getFacade().list(MilkCustomer.class);
	}

	public static List<MilkCustomer> getCustomers(String name) {
		String queryStr = "Select c from MilkCustomer as c where c.customerName like :cname ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cname", "%" + name + "%");
		List<MilkCustomer> l = FacadeFactory.getFacade().list(queryStr, parameters);
		return l;
	}

}