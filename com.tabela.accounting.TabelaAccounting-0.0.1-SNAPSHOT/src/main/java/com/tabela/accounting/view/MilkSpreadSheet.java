package com.tabela.accounting.view;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.controllers.MainController;
import com.tabela.accounting.controllers.MilkCustomerController;
import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.enums.TransactionSource;
import com.tabela.accounting.enums.TransactionType;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MilkSpreadSheet extends VBox {
	SpreadsheetView sheet;
	DatePicker dt;
	DatePicker dt2;
	ProgressIndicator pi;
	Stage stage;
	List<CustomerMilk> milks;
	
	List<List<CustomerMilk>> sheetMilks = new ArrayList<>();

	public MilkSpreadSheet() {
		setFillWidth(true);
		setSpacing(10.0D);
		setPadding(new Insets(10.0D));
		init();
	}

	public void init() {
		HBox hBox = new HBox();
		hBox.setSpacing(20.0D);
		hBox.setAlignment(Pos.CENTER);
		hBox.setPrefHeight(70.0D);

		dt = new DatePicker(LocalDate.now().minusDays(6L));
		hBox.getChildren().add(dt);

		dt2 = new DatePicker(LocalDate.now());
		hBox.getChildren().add(dt2);
		
		ComboBox<CustomerType> cmbType = new ComboBox<>();
		hBox.getChildren().add(cmbType);
		
		cmbType.setItems(FXCollections.observableArrayList(CustomerType.values()));
		cmbType.setValue(CustomerType.SALE);
		cmbType.setConverter(new StringConverter<CustomerType>() {
			
			@Override
			public String toString(CustomerType object) {
				// TODO Auto-generated method stub
				return object.toString();
			}
			
			@Override
			public CustomerType fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Button btnShowSheet = new Button("Show Milk");
		btnShowSheet.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if(cmbType.getValue() == null){
					DialogFactory.showErrorDialog("Please select the customer type", stage);
					return;
				}
				
				String queryStr = "Select c from CustomerMilk as cm join cm.customer as c "
						+ "where cm.milkDate between :fromDate and :toDate "
						+ "AND c.customerType = :customerType";
				
				Map<String, Object> parameters = new HashMap();
				parameters.put("fromDate",new Timestamp(AppUtil.toUtilDate((LocalDate) dt.getValue()).getTime()));
				parameters.put("toDate",new Timestamp(AppUtil.toUtilDate((LocalDate) dt2.getValue()).getTime()));
				parameters.put("customerType", cmbType.getValue());
				
				milks = FacadeFactory.getFacade().list(queryStr, parameters);
				
				initSpreadsheet(cmbType.getValue());
			}
		});
		hBox.getChildren().add(btnShowSheet);

		Button btnSave = new Button("Save Milk");
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Platform.runLater(new Runnable() {
					public void run() {
						saveSheet();
					}
				});
			}
		});
		hBox.getChildren().add(btnSave);
		
		pi = new ProgressIndicator(0.0D);
		pi.setProgress(0.0D);
		
		dt.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				DatePicker datePicker = (DatePicker)event.getSource();
				if(datePicker.getValue().isAfter(LocalDate.now())){
					datePicker.setValue(LocalDate.now().minusDays(6L));
					dt2.setValue(datePicker.getValue().plusDays(6));
					return;
				}
				dt2.setValue(datePicker.getValue().plusDays(6));
			}
		});

		dt2.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				DatePicker datePicker = (DatePicker)event.getSource();
				if(datePicker.getValue().isAfter(LocalDate.now())){
					dt.setValue(LocalDate.now().minusDays(6L));
					datePicker.setValue(LocalDate.now());
					return;
				}
				dt.setValue(datePicker.getValue().minusDays(6L));
			}
		});
		dt.setValue(LocalDate.now().minusDays(6L));
		getChildren().add(hBox);
	}

	public void initSpreadsheet(CustomerType customerType) {
		getChildren().remove(sheet);

		Stage stage = getStage();
		stage.show();
		
		List<MilkCustomer> list = MilkCustomerController.getCustomers(false, customerType);
		int days = Period.between((LocalDate) dt.getValue(), (LocalDate) dt2.getValue()).getDays();
		int columnCount = 5 + days * 2;
		int rowCount = list.size() + 3;

		GridBase grid = new GridBase(rowCount, columnCount);
		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
		for (int row = 0; row < grid.getRowCount(); row++) {
			final int rowIndex = row;
			ObservableList<SpreadsheetCell> cols = FXCollections.observableArrayList();
			List<CustomerMilk> rowMiks = new ArrayList<>();
			/*for (int column = 0; column < columnCount; column++) {
				SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "Income");
		        cell.setEditable(true);
		        cell.getStyleClass().add("first-cell");
				cols.add(cell);
			}
			rows.add(cols);*/
			
			if (row == 0) {
				for (int column = 0; column < columnCount; column++) {
					SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "");
					cell.setEditable(false);
					if (column >= 3) {
						cell.getStyleClass().add("value-cell");
					}
					cols.add(cell);
				}
			} else if (row == 1) {
				for (int column = 0; column < columnCount; column++) {
					if (column == 0) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "ID");
						cell.setEditable(false);
						cols.add(cell);
					} else if (column == 1) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
								"CUSTOMER NAME");
						cell.setEditable(false);
						cols.add(cell);
					} else if (column == 2) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "MILK RATE");
						cell.setEditable(false);
						cols.add(cell);
					} else if ((column <= days + 3) && (column >= 3) && (column <= 9)) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 2,
								new SimpleDateFormat("dd-MMM-yyyy").format(
										AppUtil.toUtilDate(((LocalDate) this.dt.getValue()).plusDays(column - 3))));
						cell.setEditable(false);
						cell.getStyleClass().add("date-cell");
						cols.add(cell);
					} else {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "");
						cell.getStyleClass().add("empty-cell");
						cell.setEditable(false);
						cols.add(cell);
					}
				}
			} else if (row == 2) {
				for (int column = 0; column < columnCount; column++) {
					if (column >= 3) {
						if (column % 2 == 0) {
							SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "Evening");
							cell.setEditable(false);
							cell.getStyleClass().add("value-cell");
							cols.add(cell);
						} else {
							SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "Morning");
							cell.setEditable(false);
							cell.getStyleClass().add("value-cell");
							cols.add(cell);
						}
					} else {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "");
						cell.setEditable(false);
						cols.add(cell);
					}
				}
			} else {
				MilkCustomer customer = (MilkCustomer) list.get(row - 3);

				int day = 0;
				for (int column = 0; column < grid.getColumnCount(); column++) {
					final int columnIndex = column;
					CustomerMilk milk = null;
					if (column == 0) {
						SpreadsheetCell cell = SpreadsheetCellType.INTEGER.createCell(row, column, 1, 1,Integer.valueOf(customer.getId().intValue()));
						cell.setEditable(false);
						cols.add(cell);
					} else if (column == 1) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
								customer.getCustomerName());
						cell.setEditable(false);
						cols.add(cell);
					} else if (column == 2) {
						SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
								customer.getMilkRate()+"");
						cell.setEditable(false);
						cols.add(cell);
					} else {
						final SpreadsheetCell cell = SpreadsheetCellType.DOUBLE.createCell(row, column, 1, 1, null);
						cell.getStyleClass().add("value-cell");
						cols.add(cell);
						//int r = row;
						//Label lbl = new Label();
						if (column % 2 != 0) {
							day = column / 2 - 1;
							System.out.println("Morning day" + day);
							milk = getCustomerMilk(((LocalDate) this.dt.getValue()).plusDays(day), customer);
							if (milk != null) {
								cell.setItem(milk.getMorningMilk() > 0.0D ? milk.getMorningMilk() : null);
								milk.setMilkTime(CustomerMilk.MilkTime.Morning);
							} else {
								milk = new CustomerMilk();
								milk.setMilkDate(new Timestamp(
										AppUtil.toUtilDate(((LocalDate) this.dt.getValue()).plusDays(day)).getTime()));
								milk.setCustomer(customer);
								milk.setMilkRate(customer.getMilkRate());
								milk.setMilkTime(CustomerMilk.MilkTime.Morning);
							}
							//lbl.setUserData(milk);
							//cell.setGraphic(lbl);
						} else {
							day = column / 2 - 2;
							System.out.println("Evening day" + day);
							milk = getCustomerMilk(((LocalDate) this.dt.getValue()).plusDays(day), customer);
							if (milk != null) {
								cell.setItem(milk.getEveningMilk() > 0.0D ? milk.getEveningMilk() : null);
								milk.setMilkTime(CustomerMilk.MilkTime.Evening);
							} else {
								milk = new CustomerMilk();
								milk.setMilkDate(new Timestamp(
										AppUtil.toUtilDate(((LocalDate) this.dt.getValue()).plusDays(day)).getTime()));
								milk.setCustomer(customer);
								milk.setMilkRate(customer.getMilkRate());
								milk.setMilkTime(CustomerMilk.MilkTime.Evening);
							}
							//lbl.setUserData(milk);
							//cell.setGraphic(lbl);
						}
						
						cell.textProperty().addListener(new ChangeListener<String>() {

							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
								// TODO Auto-generated method stub
								if (newValue.length() == 0) {
									return;
								}
								try {
									System.out.println(oldValue + "-" + newValue);
									CustomerMilk milk = sheetMilks.get(rowIndex).get(columnIndex);
									MilkCustomer customer = milk.getCustomer();
									LocalDate dt = AppUtil.toLocalDate(milk.getMilkDate());
									CustomerMilk milk2 = getCustomerMilk(dt, customer);
									//System.err.println(milk2.getId()+" => "+milk2.getMilkDate());
									if (milk2 == null) {
										milk2 = milk;
									}
									System.out.println(milk.getMilkDate());
									System.out.println(milk.getMilkTime());
									if (milk.getMilkTime() == CustomerMilk.MilkTime.Morning) {
										milk2.setMorningMilk(Double.valueOf(Double.parseDouble(newValue)));
									} else {
										milk2.setEveningMilk(Double.valueOf(Double.parseDouble(newValue)));
									}
									milk2.setMilkRate(customer.getMilkRate());
									FacadeFactory.getFacade().store(milk2);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
					rowMiks.add(columnIndex, milk);
				}
			}
			
			sheetMilks.add(rowIndex, rowMiks);
			rows.add(cols);
		}
		grid.setRows(rows);

		Rectangle2D screen = TabelaAccounting.getScreen();
		
		sheet = new SpreadsheetView(grid);
		sheet.setPadding(new Insets(10.0D));
		sheet.setMinHeight(screen.getHeight() - 150);
		getChildren().add(sheet);
		
		sheet.getColumns().get(0).setFixed(true);
		sheet.getColumns().get(1).setFixed(true);
		sheet.getColumns().get(2).setFixed(true);

		stage.close();
	}

	public void setSparedsheetData() {
		Stage stage = getStage();
		stage.show();
		new Thread(new Runnable() {
			CustomerMilk milk = null;
			Grid grid = sheet.getGrid();

			public void run() {
				for (int row = 3; row < grid.getRowCount(); row++) {
					final int r = row;
					try {
						Thread.sleep(new Random().nextInt(1000));
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					final double progress = row / grid.getRowCount();

					Platform.runLater(new Runnable() {
						public void run() {
							MilkCustomer customer = (MilkCustomer) FacadeFactory.getFacade().find(MilkCustomer.class,
									Long.valueOf(Long.parseLong(
											((SpreadsheetCell) ((ObservableList) grid.getRows().get(r)).get(0))
													.getText())));
							for (int column = 3; column < grid.getColumnCount(); column++) {
								final SpreadsheetCell cell = (SpreadsheetCell) ((ObservableList) sheet.getGrid()
										.getRows().get(r)).get(column);
								if (column % 2 != 0) {
									int day = column / 2 - 1;
									System.out.println("Morning day" + day);
									CustomerMilk milk = getCustomerMilk(((LocalDate) dt.getValue()).plusDays(day),
											customer);
									if (milk != null) {
										cell.setItem(milk.getMorningMilk() > 0.0D ? milk.getMorningMilk() : null);
										milk.setMilkTime(CustomerMilk.MilkTime.Morning);
										Label lbl = new Label();
										lbl.setUserData(milk);
										((SpreadsheetCell) ((ObservableList) sheet.getGrid().getRows().get(r))
												.get(column)).setGraphic(lbl);
									} else {
										milk = new CustomerMilk();
										milk.setMilkDate(new Timestamp(AppUtil
												.toUtilDate(((LocalDate) dt.getValue()).plusDays(day)).getTime()));
										milk.setCustomer(customer);
										milk.setMilkRate(customer.getMilkRate());
										milk.setMilkTime(CustomerMilk.MilkTime.Morning);
										Label lbl = new Label();
										lbl.setUserData(milk);
										((SpreadsheetCell) ((ObservableList) sheet.getGrid().getRows().get(r))
												.get(column)).setGraphic(lbl);
									}
								} else {
									int day = column / 2 - 2;
									System.out.println("Evening day" + day);
									CustomerMilk milk = getCustomerMilk(((LocalDate) dt.getValue()).plusDays(day),
											customer);
									if (milk != null) {
										cell.setItem(milk.getEveningMilk() > 0.0D ? milk.getEveningMilk() : null);
										Label lbl = new Label();
										lbl.setUserData(milk);
										milk.setMilkTime(CustomerMilk.MilkTime.Evening);
										((SpreadsheetCell) ((ObservableList) sheet.getGrid().getRows().get(r))
												.get(column)).setGraphic(lbl);
									} else {
										milk = new CustomerMilk();
										milk.setMilkDate(new Timestamp(AppUtil
												.toUtilDate(((LocalDate) dt.getValue()).plusDays(day)).getTime()));
										milk.setCustomer(customer);
										milk.setMilkRate(customer.getMilkRate());
										milk.setMilkTime(CustomerMilk.MilkTime.Evening);
										Label lbl = new Label();
										lbl.setUserData(milk);
										((SpreadsheetCell) ((ObservableList) sheet.getGrid().getRows().get(r))
												.get(column)).setGraphic(lbl);
									}
								}
								cell.textProperty().addListener(new ChangeListener<String>() {
									public void changed(ObservableValue<? extends String> arg0, String oldValue,
											String newValue) {
										try {
											System.out.println(oldValue + "-" + newValue);
											CustomerMilk milk = (CustomerMilk) ((Label) cell.getGraphic())
													.getUserData();
											MilkCustomer customer = milk.getCustomer();
											LocalDate dt = AppUtil.toLocalDate(milk.getMilkDate());
											CustomerMilk milk2 = getCustomerMilk(dt, customer);
											if (milk2 == null) {
												milk2 = milk;
											}
											System.out.println(milk.getMilkDate());
											System.out.println(milk.getMilkTime());
											if (milk.getMilkTime() == CustomerMilk.MilkTime.Morning) {
												milk2.setMorningMilk(Double.valueOf(Double.parseDouble(newValue)));
											} else {
												milk2.setEveningMilk(Double.valueOf(Double.parseDouble(newValue)));
											}
											milk2.setMilkRate(customer.getMilkRate());
											System.out.println(milk2.getCustName());
											FacadeFactory.getFacade().store(milk2);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
							pi.setProgress(progress);
						}
					});
				}
			}
		}).start();
		stage.close();
	}

	public CustomerMilk getCustomerMilk(LocalDate dt, MilkCustomer customer) {
		List<CustomerMilk> milks = getMilks();
		for (CustomerMilk milk : milks) {
			if ((milk.getCustomer().getId() == customer.getId())
					&& (AppUtil.toUtilDate(dt).getTime() == milk.getMilkDate().getTime())) {
				return milk;
			}
		}
		return null;
	}
	
	public List<CustomerMilk> getMilks(){
		String queryStr = "Select c from CustomerMilk as c where c.milkDate between :fromDate and :toDate";
		Map<String, Object> parameters = new HashMap();
		parameters.put("fromDate",new Timestamp(AppUtil.toUtilDate(dt.getValue()).getTime()));
		parameters.put("toDate",new Timestamp(AppUtil.toUtilDate(dt2.getValue()).getTime()));
		return FacadeFactory.getFacade().list(queryStr, parameters);
	}

	public void saveSheet() {
		Stage stage = getStage();
		stage.show();
		ObservableList<ObservableList<SpreadsheetCell>> rows = sheet.getGrid().getRows();
		for (Integer row = Integer.valueOf(3); row.intValue() < rows.size(); row = Integer
				.valueOf(row.intValue() + 1)) {
			ObservableList<SpreadsheetCell> columns = (ObservableList) rows.get(row.intValue());

			Long id = Long.valueOf(Long.parseLong(((SpreadsheetCell) columns.get(0)).getText()));
			MilkCustomer customer = (MilkCustomer) FacadeFactory.getFacade().find(MilkCustomer.class, id);
			CustomerMilk milk = null;
			for (int column = 3; column < columns.size(); column++) {
				SpreadsheetCell cell = (SpreadsheetCell) columns.get(column);
				double value = cell.getText().length() > 0 ? Double.parseDouble(cell.getText()) : 0.0D;
				if (column % 2 != 0) {
					int day = column / 2 - 1;
					milk = getCustomerMilk(((LocalDate) dt.getValue()).plusDays(day), customer);
					if (milk == null) {
						milk = new CustomerMilk();
						milk.setCustomer(customer);
						milk.setBranch(TabelaAccounting.getBranch());
						milk.setMilkDate(new Timestamp(AppUtil.toUtilDate(((LocalDate) dt.getValue()).plusDays(day)).getTime()));
						milk.setMilkRate(customer.getMilkRate());
					}
					milk.setMorningMilk(Double.valueOf(value));
				} else {
					int day = column / 2 - 2;
					milk.setEveningMilk(Double.valueOf(value));
					FacadeFactory.getFacade().store(milk);
				}
			}
		}
		stage.close();
	}

	public Stage getStage() {
		Stage stage = new Stage();
		HBox box = new HBox();
		Label lblMsg = new Label("Please wait ........");
		box.getChildren().add(lblMsg);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(10.0D));
		Scene scene = new Scene(box, 300.0D, 50.0D);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Progress");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(TabelaAccounting.getPrimaryStage());
		return stage;
	}
}
