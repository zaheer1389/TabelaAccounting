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
@Table(name = "Merchant")
public class Merchant extends AbstractPojo implements Serializable {
	
	@Column(name = "MerchantName")
	private String merchantName;
	
	@Column(name = "PendingBillAmount")
	private Double pendingBillAmount;
	
	@Column(name = "AddedDate")
	private Timestamp addedDate;
	
	@Column(name = "CreatedDate")
	private Timestamp createdDate;

	@JoinColumn(name = "BranchId")
	private Branch branch;
	
	@Transient
	private double billAmt;
	
	@Transient
	private String strType;
	
	@Transient
	private String strJoinDate;

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

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Timestamp getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Timestamp addedDate) {
		this.addedDate = addedDate;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public double getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(double billAmt) {
		this.billAmt = billAmt;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrJoinDate() {
		return strJoinDate;
	}

	public void setStrJoinDate(String strJoinDate) {
		this.strJoinDate = strJoinDate;
	}

	
}
