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
import com.tabela.accounting.model.Expense;
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

public class ExpenseController implements Initializable {

	@FXML
    private DatePicker txtExpenseDate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnClearSearch;

    @FXML
    private Button btnAddExpense;

    @FXML
    private VBox content;

    @FXML
    private TableView<Expense> tableExpense;

    @FXML
    private TableColumn colExpenseDate;

    @FXML
    private TableColumn colAmount;

    @FXML
    private TableColumn colExpenseType;

    @FXML
    private TableColumn colView;

    @FXML
    private TableColumn colEdit;

    @FXML
    private TableColumn colDelete;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Rectangle2D screen = TabelaAccounting.getScreen();
    	tableExpense.setMinHeight(screen.getHeight() - 150);
		
    	setTable(getExpenses());
    }
    
    public void setTable(List<Expense> expenses) {
    	
    	colExpenseDate.setCellValueFactory(new PropertyValueFactory("strExpenseDate"));
    	colAmount.setCellValueFactory(new PropertyValueFactory("amount"));
    	colExpenseType.setCellValueFactory(new PropertyValueFactory("expenseType"));
		
    	Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactoryViewButton = new Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>>() {
            @Override
            public TableCell<Expense, Void> call(final TableColumn<Expense, Void> param) {
                final TableCell<Expense, Void> cell = new TableCell<Expense, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		Expense milkCustomer = getTableView().getItems().get(getIndex());
                            showForm(milkCustomer, true);
                        });
                    	button.setMinWidth(50);
                    	button.getStyleClass().setAll("btnView");
                    	
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
        
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactoryEditButton = new Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>>() {
            @Override
            public TableCell<Expense, Void> call(final TableColumn<Expense, Void> param) {
                final TableCell<Expense, Void> cell = new TableCell<Expense, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		Expense milkCustomer = getTableView().getItems().get(getIndex());
                            showForm(milkCustomer, false);
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
        
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactoryDeleteButton = new Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>>() {
            @Override
            public TableCell<Expense, Void> call(final TableColumn<Expense, Void> param) {
                final TableCell<Expense, Void> cell = new TableCell<Expense, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		FXOptionPane.Response response = DialogFactory.showConfirmationDialog("Do you really want to delete this Expense?", DialogType.YESNOCANCEL, null);
                    		if (response == FXOptionPane.Response.YES){
                    			Expense data = getTableView().getItems().get(getIndex());
                                FacadeFactory.getFacade().delete(data);
                                setTable(getExpenses());
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
 
        colView.setCellFactory(cellFactoryViewButton);
        colEdit.setCellFactory(cellFactoryEditButton);
        colDelete.setCellFactory(cellFactoryDeleteButton);
        
        tableExpense.setItems(FXCollections.observableArrayList(expenses));
	}

    @FXML
	void addExpense(ActionEvent event) {
		// New window (Stage)
		showForm(new Expense(), false);
	}

	@FXML
	void clearSearch(ActionEvent event) {
		txtExpenseDate.setValue(null);
		setTable(getExpenses());
	}

	@FXML
	void search(ActionEvent event) {
		if(txtExpenseDate.getValue() == null){
			DialogFactory.showErrorDialog("Please select Expense date", TabelaAccounting.stage);
			return;
		}
		List<Expense> milks = getExpenses(txtExpenseDate.getValue());
		if(milks == null || milks.size() == 0){
			DialogFactory.showInformationDialog("No expense founds for selected date", TabelaAccounting.stage);
			return;
		}
		setTable(milks);
	}
    
    public void showForm(Expense expense, boolean readOnly){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddExpenseForm.fxml"));
			Parent root = (Parent)loader.load();
			

			AddExpenseFormController controller = (AddExpenseFormController)loader.getController();
	        controller.setExpense(expense);
	        controller.setReadOnly(readOnly);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Expense");
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
					setTable(getExpenses());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public static List<Expense> getExpenses() {
		return FacadeFactory.getFacade().list(Expense.class);
	}

	public static List<Expense> getExpenses(LocalDate localDate) {
		 String queryStr = "Select m From Expense m where m.expenseDate = :dt";
		 Map<String, Object> parameters = new HashMap();
		 parameters.put("dt", new Timestamp(AppUtil.toUtilDate(localDate).getTime()));
		 return FacadeFactory.getFacade().list(queryStr, parameters);
	}

}