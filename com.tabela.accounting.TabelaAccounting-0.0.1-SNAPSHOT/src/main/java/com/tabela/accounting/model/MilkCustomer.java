package com.tabela.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.persistence.model.AbstractPojo;

@Entity
@Table(name = "MilkCustomer")
public class MilkCustomer extends AbstractPojo implements Serializable {
	
	@Column(name = "CustomerName")
	private String customerName;
	
	@Column(name = "CustomerAddress")
	private String customerAddress;
	
	@Column(name = "BillAmount")
	private Double billAmount;
	
	@Column(name = "PendingBillAmount")
	private Double pendingBillAmount;
	
	@Column(name = "AddedDate")
	private Timestamp addedDate;
	
	@Column(name = "CreatedDate")
	private Timestamp createdDate;
	
	@Column(name = "Active")
	private boolean active;

	@JoinColumn(name = "BranchId")
	private Branch branch;
	
	@Column(name = "MilkRateId")
	private Long milkRateId;
	
	@Column(name = "MilkRate")
	private double milkRate;
	
	@Transient
	private double billAmt;
	
	@Transient
	private String strType;
	
	@Transient
	private String strJoinDate;
	
	@JoinColumn(name="Tempo")
	private Tempo tempo;

	public Double getPendingBillAmount() {
		return Double.valueOf(this.pendingBillAmount != null ? this.pendingBillAmount.doubleValue() : 0.0D);
	}

	public void setPendingBillAmount(Double pendingBillAmount) {
		this.pendingBillAmount = pendingBillAmount;
	}

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return this.customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public Double getBillAmount() {
		return Double.valueOf(this.billAmount != null ? this.billAmount.doubleValue() : 0.0D);
	}

	public void setBillAmount(Double billAmount) {
		this.billAmount = billAmount;
	}

	public Timestamp getAddedDate() {
		return this.addedDate;
	}

	public void setAddedDate(Timestamp addedDate) {
		this.addedDate = addedDate;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public double getBillAmt() {
		return (this.billAmount != null ? this.billAmount.doubleValue() : 0.0D)
				+ (this.pendingBillAmount != null ? this.pendingBillAmount.doubleValue() : 0.0D);
	}

	public void setBillAmt(double billAmt) {
		this.billAmt = billAmt;
	}

	public Long getMilkRateId() {
		return this.milkRateId;
	}

	public void setMilkRateId(Long milkRateId) {
		this.milkRateId = milkRateId;
	}

	public double getMilkRate() {
		return this.milkRate;
	}

	public void setMilkRate(double milkRate) {
		this.milkRate = milkRate;
	}

	public String getStrJoinDate() {
		return new SimpleDateFormat("dd-MMM-yyyy").format(this.addedDate);
	}

	public void setStrJoinDate(String strJoinDate) {
		this.strJoinDate = strJoinDate;
	}

	public Tempo getTempo() {
		return tempo;
	}

	public void setTempo(Tempo tempo) {
		this.tempo = tempo;
	}
	
	
}
