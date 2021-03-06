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
import com.tabela.accounting.model.Tempo;
import com.tabela.accounting.report.HeilReportGenerator;
import com.tabela.accounting.report.MilkAccountSheetGenerator;
import com.tabela.accounting.report.MilkInvoiceGenerator;
import com.tabela.accounting.report.ProfitLossReportGenerator;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;
import com.tabela.accounting.view.AvgMilkView;
import com.tabela.accounting.view.DateRangeLayout;
import com.tabela.accounting.view.MilkInvoiceSelectionLayout;
import com.tabela.accounting.view.MilkSpreadSheet;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
    private Menu menuMerchants;

    @FXML
    private MenuItem menuMerchants_menuItemMerchants;

    @FXML
    private MenuItem menuMerchants_menuItemMerchantPayment;
    
    @FXML
    private Menu menuExpense;

    @FXML
    private MenuItem menuExpense_menuItemExpenses;

    @FXML
    private MenuItem menuExpense_menuItemExpenseReport;
    
    @FXML
    private Menu menuTempo;

    @FXML
    private MenuItem menuTempo_menuItemManageTempo;

    @FXML
    private MenuItem menuTempo_menuItemHeilReport;

    
    @FXML
    private Menu menuReport;

    @FXML
    private MenuItem menuReport_BillSheet;
    
    @FXML
    private MenuItem menuReport_ProfitLossReport;

    @FXML
    private Menu menuHelp;

    @FXML
    private Label lblPageTitle;

    @FXML
    private VBox content;
    
    @FXML
    private Button btnDashboard;
    
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
        
        lblPageTitle.setText("");
        
        content.getChildren().clear();
		content.getChildren().add(new AvgMilkView());
    }

    @FXML
    void changeLangToEnglish(ActionEvent event) {

    }

    @FXML
    void changeLangToGujarati(ActionEvent event) {

    }
    
    @FXML
    void dashboard(ActionEvent event) {
    	lblPageTitle.setText("Dashboard :: Avg Milk Rate");
        
        content.getChildren().clear();
		content.getChildren().add(new AvgMilkView());
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
    	MilkInvoiceSelectionLayout root = new MilkInvoiceSelectionLayout(false);

		root.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<MilkCustomer> customers = root.getListView().getSelectionModel().getSelectedItems();
					File report = new MilkInvoiceGenerator().generate(customers, AppUtil.toUtilDate((LocalDate) root.getFromDate().getValue()),
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

		//stage.setX(100.0D);
		//stage.setY(100.0D);
		stage.show();
    }

    @FXML
    void printCustomerWiseBill(ActionEvent event) {
    	MilkInvoiceSelectionLayout root = new MilkInvoiceSelectionLayout(false);

		root.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<MilkCustomer> customers = root.getListView().getSelectionModel().getSelectedItems();
					File report = new MilkInvoiceGenerator().generate(customers, AppUtil.toUtilDate((LocalDate) root.getFromDate().getValue()),
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

		//stage.setX(100.0D);
		//stage.setY(100.0D);
		stage.show();
    }
	
	@FXML
    void expense(ActionEvent event) {
		lblPageTitle.setText("Expenses");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/ExpenseView.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void expenseReport(ActionEvent event) {

    }
    
    @FXML
    void profitLossReport(ActionEvent event) {
    	DateRangeLayout dateRangeLayout = new DateRangeLayout();
    	dateRangeLayout.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<Tempo> customers = TempoController.getTempos();
					File report = new ProfitLossReportGenerator().generate(AppUtil.toUtilDate((LocalDate) dateRangeLayout.getFromDate().getValue()),
							AppUtil.toUtilDate((LocalDate) dateRangeLayout.getToDate().getValue()));
					if(report != null){
						hostServices.showDocument(report.getAbsolutePath());
					}
				} catch (Exception e) {
					DialogFactory.showExceptionDialog(e, null);
				}
			}
		});
		Stage stage = new Stage();
		stage.setTitle("Profit/Loss Report");
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(TabelaAccounting.getPrimaryStage());

		Scene scene = new Scene(dateRangeLayout, 400.0D, 400.0D);
		stage.setScene(scene);

		//stage.setX(100.0D);
		//stage.setY(100.0D);
		stage.show();
    }
    
    @FXML
    void merchant(ActionEvent event) {
    	lblPageTitle.setText("Merchants");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Merchant.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void merchantPayment(ActionEvent event) {
    	lblPageTitle.setText("Merchant Payments");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/MerchantPayment.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void heilReport(ActionEvent event) {
    	DateRangeLayout dateRangeLayout = new DateRangeLayout();
    	dateRangeLayout.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<Tempo> customers = TempoController.getTempos();
					File report = new HeilReportGenerator().generate(customers, AppUtil.toUtilDate((LocalDate) dateRangeLayout.getFromDate().getValue()),
							AppUtil.toUtilDate((LocalDate) dateRangeLayout.getToDate().getValue()));
					if(report != null){
						hostServices.showDocument(report.getAbsolutePath());
					}
				} catch (Exception e) {
					DialogFactory.showExceptionDialog(e, null);
				}
			}
		});
		Stage stage = new Stage();
		stage.setTitle("Print Heil Charges Report");
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(TabelaAccounting.getPrimaryStage());

		Scene scene = new Scene(dateRangeLayout, 400.0D, 400.0D);
		stage.setScene(scene);

		//stage.setX(100.0D);
		//stage.setY(100.0D);
		stage.show();
    }

    @FXML
    void manageTempo(ActionEvent event) {
    	lblPageTitle.setText("Tempos");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Tempo.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void printBillSheet(ActionEvent event) {
    	MilkInvoiceSelectionLayout dateRangeLayout = new MilkInvoiceSelectionLayout(true);
    	dateRangeLayout.getBtnReport().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					List<MilkCustomer> customers = MilkCustomerController.getCustomers(false, dateRangeLayout.getCmbType().getValue(), dateRangeLayout.getCmbCustomerType().getValue());
					File report = new MilkAccountSheetGenerator().generate(customers, AppUtil.toUtilDate((LocalDate) dateRangeLayout.getFromDate().getValue()),
							AppUtil.toUtilDate((LocalDate) dateRangeLayout.getToDate().getValue()));
					if(report != null){
						hostServices.showDocument(report.getAbsolutePath());
					}
				} catch (Exception e) {
					DialogFactory.showExceptionDialog(e, null);
				}
			}
		});
		Stage stage = new Stage();
		stage.setTitle("Print Bill Sheet");
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(TabelaAccounting.getPrimaryStage());

		Scene scene = new Scene(dateRangeLayout, 400.0D, 400.0D);
		stage.setScene(scene);

		//stage.setX(100.0D);
		//stage.setY(100.0D);
		stage.show();
    }
   
}
