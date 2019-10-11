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
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.model.MerchantPayment;
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.model.MerchantPayment;
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

public class MerchantPaymentController implements Initializable {

	@FXML
    private ComboBox<Merchant> cmbMerchants;

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
    private TableView<MerchantPayment> tablePayment;

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
		
		setTable(getMerchantPayments());
	}

	public void setTable(List<MerchantPayment> milkPayments) {
		cmbMerchants.setItems(FXCollections.observableArrayList(MerchantController.getMerchants()));
		cmbMerchants.setConverter(new StringConverter<Merchant>() {
			
			@Override
			public String toString(Merchant object) {
				// TODO Auto-generated method stub
				return object.getMerchantName();
			}
			
			@Override
			public Merchant fromString(String string) {
				// TODO Auto-generated method stub
				return cmbMerchants.getItems().stream().filter(customer -> 
								customer.getMerchantName().equals(string)).findFirst().orElse(null);
			}
		});
		
		colCustomerName.setCellValueFactory(new PropertyValueFactory("custName"));
		colPaymentDate.setCellValueFactory(new PropertyValueFactory("strPaymentDate"));
		colAmount.setCellValueFactory(new PropertyValueFactory("amount"));

        Callback<TableColumn<MerchantPayment, Void>, TableCell<MerchantPayment, Void>> cellFactoryEditButton = new Callback<TableColumn<MerchantPayment, Void>, TableCell<MerchantPayment, Void>>() {
            @Override
            public TableCell<MerchantPayment, Void> call(final TableColumn<MerchantPayment, Void> param) {
                final TableCell<MerchantPayment, Void> cell = new TableCell<MerchantPayment, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		MerchantPayment milkPayment = getTableView().getItems().get(getIndex());
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
        
        Callback<TableColumn<MerchantPayment, Void>, TableCell<MerchantPayment, Void>> cellFactoryDeleteButton = new Callback<TableColumn<MerchantPayment, Void>, TableCell<MerchantPayment, Void>>() {
            @Override
            public TableCell<MerchantPayment, Void> call(final TableColumn<MerchantPayment, Void> param) {
                final TableCell<MerchantPayment, Void> cell = new TableCell<MerchantPayment, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to delete this merchant payment?", DialogType.YESNOCANCEL, null);
                    		if (response == FXOptionPane.Response.YES){
                    			MerchantPayment data = getTableView().getItems().get(getIndex());
                                FacadeFactory.getFacade().delete(data);
                                setTable(getMerchantPayments());
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
		showForm(new MerchantPayment());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		cmbMerchants.setValue(null);
		txtPaymentDate.setValue(null);
		setTable(getMerchantPayments());
	}

	@FXML
	void search(ActionEvent event) {
		if(cmbMerchants.getValue() == null){
			DialogFactory.showErrorDialog("Please select merchant", TabelaAccounting.stage);
			return;
		}
		if(txtPaymentDate.getValue() == null){
			DialogFactory.showErrorDialog("Please select merchant payment date", TabelaAccounting.stage);
			return;
		}
		List<MerchantPayment> milkPayments = getMerchantPayments(cmbMerchants.getValue(), txtPaymentDate.getValue());
		if(milkPayments == null || milkPayments.size() == 0){
			DialogFactory.showInformationDialog("No merchant payments found for selected merchant and date", TabelaAccounting.stage);
			return;
		}
		setTable(milkPayments);
	}

	
	public void showForm(MerchantPayment milkPayment){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMerchantPaymentForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddMerchantPaymentFormController controller = (AddMerchantPaymentFormController)loader.getController();
	        controller.setMerchantPayment(milkPayment);
	        
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
					setTable(getMerchantPayments());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<MerchantPayment> getMerchantPayments() {
		return FacadeFactory.getFacade().list(MerchantPayment.class);
	}

	public static List<MerchantPayment> getMerchantPayments(Merchant customer, LocalDate localDate) {
		 String queryStr = "Select m From MerchantPayment m where m.merchant = :customer and m.paymentDate = :paymentDate";
		 Map<String, Object> parameters = new HashMap();
		 parameters.put("customer", customer);
		 parameters.put("paymentDate", new Timestamp(AppUtil.toUtilDate(localDate).getTime()));
		 return FacadeFactory.getFacade().list(queryStr, parameters);
	}

}