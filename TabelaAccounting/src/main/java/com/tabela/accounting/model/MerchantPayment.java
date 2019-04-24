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
@Table(name = "MerchantPayment")
public class MerchantPayment extends AbstractPojo implements Serializable {
	
	@JoinColumn(name = "MerchantId")
	private Merchant merchant;
	
	@Column(name = "PaymentDate")
	private Timestamp paymentDate;
	
	@Column(name = "Amount")
	private double amount;
	
	@Transient
	private String custName;
	
	@Transient
	private String strPaymentDate;
	
	@JoinColumn(name = "BranchId")
	private Branch branch;

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Merchant getMerchant() {
		return this.merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getCustName() {
		return this.merchant.getMerchantName();
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getStrPaymentDate() {
		return new SimpleDateFormat("dd-MMMMMM-yyyy").format(this.paymentDate);
	}

	public Timestamp getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
