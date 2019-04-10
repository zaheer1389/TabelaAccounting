package com.tabela.accounting.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.report.ReportGenerator;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;
import com.tabela.accounting.view.MilkSpreadSheet;
import com.tabela.accounting.view.ReportLayout;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {

	
	@FXML
    private VBox root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu menuFile;

    @FXML
    private Menu menuLanguage;

    @FXML
    private MenuItem menuILanguage_MenuItemEnglish;

    @FXML
    private MenuItem menuILanguage_MenuItemGujarati;

    @FXML
    private Menu menuMilk;

    @FXML
    private MenuItem menuMilk_MenuItemCustomers;

    @FXML
    private Menu menuMilk_MenuIMilk;

    @FXML
    private MenuItem menuMilk_MenuItemMilkSheet;

    @FXML
    private MenuItem menuMilk_MenuItemMilkForm;
    
    @FXML
    private MenuItem menuMilk_MenuItemPayment;
    
    @FXML
    private Menu menuMilk_MenuPrintBill;

    @FXML
    private MenuItem menuMilk_MenuItemPrintBillCustomerWise;

    @FXML
    private MenuItem menuMilk_MenuItemPrintBillAllCustomer;

    @FXML
    private Menu menuHelp;

    @FXML
    private Label lblPageTitle;

    @FXML
    private Label lblDate;

    @FXML
    private VBox content;
    
    Stage window;
    
    private HostServices hostServices ;

    public HostServices getHostServices() {
        return hostServices ;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices ;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        window = TabelaAccounting.stage;
        root.prefWidthProperty().bind(window.widthProperty().multiply(0.80));
        
        lblDate.setText(new Date()+"");
        lblPageTitle.setText("");
        
    }

    @FXML
    void changeLangToEnglish(ActionEvent event) {

    }

    @FXML
    void changeLangToGujarati(ActionEvent event) {

    }

    @FXML
    void showMilkCustomersView(ActionEvent event) {
    	lblPageTitle.setText("Milk Customers");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/MilkCustomer.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void milkForm(ActionEvent event) {
    	lblPageTitle.setText("Customers Milk");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/MilkView.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void milkSheet(ActionEvent event) {
    	lblPageTitle.setText("Customer Milk Sheet");
    	MilkSpreadSheet sheet = new MilkSpreadSheet();
    	try {
			content.getChildren().clear();
			content.getChildren().add(sheet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void milkPayment(ActionEvent event) {
    	lblPageTitle.setText("Milk Payments");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/MilkPayment.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void printAllCustomerBill(ActionEvent event) {
		ReportLayout root = new ReportLayout();

		final ListView<MilkCustomer> listView = getMilkCustomerListView();
		root.getChildren().add(2, listView);

		root.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<MilkCustomer> customers = listView.getSelectionModel().getSelectedItems();
					File report = new ReportGenerator().generate(customers, AppUtil.toUtilDate((LocalDate) root.getFromDate().getValue()),
							AppUtil.toUtilDate((LocalDate) root.getToDate().getValue()));
					if(report != null){
						hostServices.showDocument(report.getAbsolutePath());
					}
				} catch (Exception e) {
					DialogFactory.showExceptionDialog(e, null);
				}
			}
		});
		Stage stage = new Stage();
		stage.setTitle("Print Bill");
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(TabelaAccounting.getPrimaryStage());

		Scene scene = new Scene(root, 400.0D, 400.0D);
		stage.setScene(scene);

		stage.setX(100.0D);
		stage.setY(100.0D);
		stage.show();
    }

    @FXML
    void printCustomerWiseBill(ActionEvent event) {

    }
    
	public ListView<MilkCustomer> getMilkCustomerListView() {
		ListView<MilkCustomer> listView = new ListView();
		listView.getItems().addAll(FXCollections.observableArrayList(FacadeFactory.getFacade().list(MilkCustomer.class)));
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listView.setCellFactory(customer -> new ListCell<MilkCustomer>() {
		    @Override
		    protected void updateItem(MilkCustomer item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getCustomerName() == null) {
		            setText(null);
		        } else {
		            setText(item.getCustomerName());
		        }
		    }
		});
		return listView;
	}
   
}
