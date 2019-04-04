package com.tabela.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tabela.accounting.persistence.model.AbstractPojo;

@Entity
@Table(name = "CustomerMilk")
public class CustomerMilk extends AbstractPojo implements Serializable {
	
	@JoinColumn(name = "CustomerId")
	private MilkCustomer customer;
	
	@Column(name = "MilkDate")
	private Timestamp milkDate;
	
	@Column(name = "MorningMilk")
	private double morningMilk;
	
	@Column(name = "EveningMilk")
	private double eveningMilk;
	
	@Column(name = "MilkRate")
	private double milkRate;
	
	@Transient
	private String custName;
	
	@Transient
	private String strMilkDate;
	
	@Transient
	private MilkTime milkTime;
	
	@Transient
	private Double totalMilk;
	
	@JoinColumn(name = "BranchId")
	private Branch branch;

	public static enum MilkTime {
		Morning, Evening;
	}

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public MilkCustomer getCustomer() {
		return this.customer;
	}

	public void setCustomer(MilkCustomer customer) {
		this.customer = customer;
	}

	public Timestamp getMilkDate() {
		return this.milkDate;
	}

	public void setMilkDate(Timestamp milkDate) {
		this.milkDate = milkDate;
	}

	public String getCustName() {
		return this.customer.getCustomerName();
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getStrMilkDate() {
		return new SimpleDateFormat("dd-MMMMMM-yyyy").format(this.milkDate);
	}

	public void setStrMilkDate(String strMilkDate) {
		this.strMilkDate = strMilkDate;
	}

	public Double getTotalMilk() {
		return eveningMilk + morningMilk;
	}

	public void setTotalMilk(Double totalMilk) {
		this.totalMilk = totalMilk;
	}

	public MilkTime getMilkTime() {
		return this.milkTime;
	}

	public void setMilkTime(MilkTime milkTime) {
		this.milkTime = milkTime;
	}

	public double getMorningMilk() {
		return morningMilk;
	}

	public void setMorningMilk(double morningMilk) {
		this.morningMilk = morningMilk;
	}

	public double getEveningMilk() {
		return eveningMilk;
	}

	public void setEveningMilk(double eveningMilk) {
		this.eveningMilk = eveningMilk;
	}

	public double getMilkRate() {
		return milkRate;
	}

	public void setMilkRate(double milkRate) {
		this.milkRate = milkRate;
	}
	
	
}
