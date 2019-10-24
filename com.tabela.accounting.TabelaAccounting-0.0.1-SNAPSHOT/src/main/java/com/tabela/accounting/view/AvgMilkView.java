package com.tabela.accounting.view;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.persistence.JPAFacade;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class AvgMilkView extends VBox {

	DatePicker dt;
	DatePicker dt2;
	Stage stage;
	Label label;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");
	DecimalFormat decimalFormat = new DecimalFormat("#.##");

	VBox vBox;

	double avgSoldMilkRate, avgPurchaseMilkRate;
	int milkCounter;
	int purchaseMilkCounter;

	public AvgMilkView() {
		setFillWidth(true);
		setMinHeight(1000);
		setSpacing(10.0D);
		// setPadding(new Insets(10.0D));
		init();
	}

	public void init() {
		HBox hBox = new HBox();
		hBox.setSpacing(20.0D);
		hBox.setAlignment(Pos.CENTER);
		hBox.setPrefHeight(70.0D);
		getChildren().add(hBox);

		vBox = new VBox();
		vBox.setMinHeight(700);
		getChildren().add(vBox);

		dt = new DatePicker(LocalDate.now().minusDays(6L));
		hBox.getChildren().add(dt);

		dt2 = new DatePicker(LocalDate.now());
		hBox.getChildren().add(dt2);

		Button btnShowSheet = new Button("Show Data");
		btnShowSheet.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				avgSoldMilkRate = 0;
				avgPurchaseMilkRate = 0;
				milkCounter = 0;
				purchaseMilkCounter = 0;
				showChart();
			}
		});
		hBox.getChildren().add(btnShowSheet);

		label = new Label();
		hBox.getChildren().add(label);

	}

	public void showChart() {
		LocalDate fromDate = dt.getValue();
		LocalDate toDate = dt2.getValue();

		long days = Duration.between(fromDate.atStartOfDay(), toDate.atStartOfDay()).toDays();

		List<String> strDates = new ArrayList<>();

		for (int i = 0; i <= days; i++) {
			LocalDate localDate = fromDate.plusDays(i);
			strDates.add(simpleDateFormat.format(toUtilDate(localDate)));
		}

		// Defining the x axis
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setCategories(FXCollections.<String>observableArrayList(strDates));
		// xAxis.setLabel("Dates");

		// Defining the y axis
		NumberAxis yAxis = new NumberAxis();
		yAxis.setAutoRanging(false);
		yAxis.setUpperBound(90);
		//yAxis.setLowerBound(85);
		yAxis.setLabel("Avg Rate");

		// Creating the Bar chart
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setMinHeight(550);
		barChart.setTitle("Avg milk rate");

		XYChart.Series<String, Number> series1 = new XYChart.Series<>();
		series1.setName("Milk Sell");
		for (int i = 0; i <= days; i++) {
			LocalDate localDate = fromDate.plusDays(i);
			String dt = simpleDateFormat.format(toUtilDate(localDate));
			double res = getAvgMilk(CustomerType.SALE, localDate);
			if (res > 0) {
				milkCounter++;
			}
			avgSoldMilkRate += res;

			final XYChart.Data<String, Number> data = new XYChart.Data(dt, res);
			if (res > 0) {
				StackPane node = new StackPane();
		        Label label = new Label(res+"");
		        label.setTextFill(Color.web("#FFF"));
		        label.setRotate(-90);
		        Group group = new Group(label);
		        StackPane.setAlignment(group, Pos.CENTER);
		        StackPane.setMargin(group, new Insets(0, 0, 5, 0));
		        node.getChildren().add(group);
		        data.setNode(node);
			}
			

			series1.getData().add(data);
		}

		XYChart.Series<String, Number> series2 = new XYChart.Series<>();
		series2.setName("Milk Purchase");
		for (int i = 0; i <= days; i++) {
			LocalDate localDate = fromDate.plusDays(i);
			String dt = simpleDateFormat.format(toUtilDate(localDate));
			double res = getAvgMilk(CustomerType.PURCHASE, localDate);
			avgPurchaseMilkRate += res;
			if (res > 0) {
				purchaseMilkCounter++;
			}

			final XYChart.Data<String, Number> data = new XYChart.Data(dt, res);
			if (res > 0) {
				StackPane node = new StackPane();
		        Label label = new Label(res+"");
		        label.setRotate(-90);
		        label.setTextFill(Color.web("#000"));
		        Group group = new Group(label);
		        StackPane.setAlignment(group, Pos.CENTER);
		        StackPane.setMargin(group, new Insets(0, 0, 5, 0));
		        node.getChildren().add(group);
		        data.setNode(node);
			}
			
			
	        
			series2.getData().add(data);
		}

		// Setting the data to bar chart
		barChart.getData().addAll(series1, series2);

		vBox.getChildren().clear();
		vBox.getChildren().add(barChart);

		double avgSellRate = avgSoldMilkRate / milkCounter;
		double avgPurchaseRate = avgPurchaseMilkRate / purchaseMilkCounter;

		label.setText("Avg Sell Rate : " + decimalFormat.format((avgSellRate > 0 ? avgSellRate : 0))
				+ " , Avg Purchase Rate : " + decimalFormat.format((avgPurchaseRate > 0 ? avgPurchaseRate : 0)));

	}

	private void displayLabelForData(XYChart.Data<String, Number> data) {
		final Node node = data.getNode();
		final Text dataText = new Text(data.getYValue() + "");
		dataText.getTransforms().add(new Rotate(-45));
		node.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
				Group parentGroup = (Group) parent;
				parentGroup.getChildren().add(dataText);
			}
		});

		node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
				dataText.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
				dataText.setLayoutY(Math.round(bounds.getMinY() - dataText.prefHeight(-1) * 0.5));
			}
		});
	}

	public Date toUtilDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public double getAvgMilk(CustomerType customerType, LocalDate date) {
		String queryStr = "select  avg((EveningMilk*cm.MilkRate+MorningMilk*cm.MilkRate)/(EveningMilk+MorningMilk)) as milk from CustomerMilk as cm join MilkCustomer as c "
				+ "	where cm.CustomerId = c.Id AND c.CustomerType = '" + customerType.toString()
				+ "' AND cm.MilkDate = " + toUtilDate(date).getTime();

		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value + "") : 0;
	}
}
