package com.tabela.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tabela.accounting.persistence.model.AbstractPojo;

@Entity
@Table(name = "Expense")
public class Expense extends AbstractPojo implements Serializable {
	
	@Column(name = "ExpenseDate")
	private Timestamp expenseDate;

	@Column(name = "Amount")
	private double amount;
	
	@Column(name = "ExpenseType")
	private String expenseType;
	
	@Column(name = "Description")
	private String description;
	
	@Transient
	private String strExpenseDate;

	public Timestamp getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(Timestamp expenseDate) {
		this.expenseDate = expenseDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStrExpenseDate() {
		return new SimpleDateFormat("dd-MMMMMM-yyyy").format(expenseDate);
	}

	public void setStrExpenseDate(String strMilkDate) {
		this.strExpenseDate = strMilkDate;
	}
	
}
