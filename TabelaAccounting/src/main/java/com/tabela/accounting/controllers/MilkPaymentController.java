package com.tabela.accounting.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.DialogType;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.model.MilkPayment;
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

public class MilkPaymentController implements Initializable {

	@FXML
    private ComboBox<MilkCustomer> cmbCustomer;

    @FXML
    private DatePicker txtPaymentDate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnClearSearch;

    @FXML
    private Button btnAddCustomer;

    @FXML
    private VBox content;

    @FXML
    private TableView<MilkPayment> tablePayment;

    @FXML
    private TableColumn colCustomerName;

    @FXML
    private TableColumn colPaymentDate;

    @FXML
    private TableColumn colAmount;

    @FXML
    private TableColumn colEdit;

    @FXML
    private TableColumn colDelete;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		tablePayment.setMinHeight(screen.getHeight() - 150);
		
		setTable(getMilkPayments());
	}

	public void setTable(List<MilkPayment> milkPayments) {
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
		colPaymentDate.setCellValueFactory(new PropertyValueFactory("strPaymentDate"));
		colAmount.setCellValueFactory(new PropertyValueFactory("amount"));

        Callback<TableColumn<MilkPayment, Void>, TableCell<MilkPayment, Void>> cellFactoryEditButton = new Callback<TableColumn<MilkPayment, Void>, TableCell<MilkPayment, Void>>() {
            @Override
            public TableCell<MilkPayment, Void> call(final TableColumn<MilkPayment, Void> param) {
                final TableCell<MilkPayment, Void> cell = new TableCell<MilkPayment, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		MilkPayment milkPayment = getTableView().getItems().get(getIndex());
                            showForm(milkPayment);
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
        
        Callback<TableColumn<MilkPayment, Void>, TableCell<MilkPayment, Void>> cellFactoryDeleteButton = new Callback<TableColumn<MilkPayment, Void>, TableCell<MilkPayment, Void>>() {
            @Override
            public TableCell<MilkPayment, Void> call(final TableColumn<MilkPayment, Void> param) {
                final TableCell<MilkPayment, Void> cell = new TableCell<MilkPayment, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to delete this milk payment?", DialogType.YESNOCANCEL, null);
                    		if (response == FXOptionPane.Response.YES){
                    			MilkPayment data = getTableView().getItems().get(getIndex());
                                FacadeFactory.getFacade().delete(data);
                                setTable(getMilkPayments());
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
        
        tablePayment.setItems(FXCollections.observableArrayList(milkPayments));
	}
	
	@FXML
	void addPayment(ActionEvent event) {
		// New window (Stage)
		showForm(new MilkPayment());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		cmbCustomer.setValue(null);
		txtPaymentDate.setValue(null);
		setTable(getMilkPayments());
	}

	@FXML
	void search(ActionEvent event) {
		if(cmbCustomer.getValue() == null){
			DialogFactory.showErrorDialog("Please select customer", TabelaAccounting.stage);
			return;
		}
		if(txtPaymentDate.getValue() == null){
			DialogFactory.showErrorDialog("Please select milk payment date", TabelaAccounting.stage);
			return;
		}
		List<MilkPayment> milkPayments = getMilkPayments(cmbCustomer.getValue(), txtPaymentDate.getValue());
		if(milkPayments == null || milkPayments.size() == 0){
			DialogFactory.showInformationDialog("No milk payments found for selected customer and date", TabelaAccounting.stage);
			return;
		}
		setTable(milkPayments);
	}

	
	public void showForm(MilkPayment milkPayment){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMilkPaymentForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddMilkPaymentFormController controller = (AddMilkPaymentFormController)loader.getController();
	        controller.setMilkPayment(milkPayment);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Payment");
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
					setTable(getMilkPayments());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<MilkPayment> getMilkPayments() {
		return FacadeFactory.getFacade().list(MilkPayment.class);
	}

	public static List<MilkPayment> getMilkPayments(MilkCustomer customer, LocalDate localDate) {
		 String queryStr = "Select m From MilkPayment m where m.customer = :customer and m.paymentDate = :paymentDate";
		 Map<String, Object> parameters = new HashMap();
		 parameters.put("customer", customer);
		 parameters.put("paymentDate", new Timestamp(AppUtil.toUtilDate(localDate).getTime()));
		 return FacadeFactory.getFacade().list(queryStr, parameters);
	}

}