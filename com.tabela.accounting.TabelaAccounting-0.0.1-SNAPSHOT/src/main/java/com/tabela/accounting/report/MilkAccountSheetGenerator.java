package com.tabela.accounting.report;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.JPAFacade;

public class MilkAccountSheetGenerator {

	Document document;
	
	Font headerFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLDITALIC, BaseColor.BLACK);
	Font noteFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	Font header2Font = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.NORMAL, BaseColor.BLACK);
	Font header2BoldFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	
	List<MilkCustomer> customers;
	Date fromDate;
	Date toDate;
	
	public File generate(List<MilkCustomer> customers, Date fromDate, Date toDate) {

		this.customers = customers;
		this.fromDate = fromDate;
		this.toDate = toDate;
		
		document = new Document();
		try {
			document.setMargins(15, 15, 10, 15);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(System.getProperty("user.home")+"/Tabela Accounting/MilkAccountSheet.pdf"));
			document.open();
			process();
			document.close();
			writer.close();
			
			return new File(System.getProperty("user.home")+"/Tabela Accounting/MilkAccountSheet.pdf");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public void process(){
		
		try {
			Paragraph preface = new Paragraph("Manasiya Dairy \n Unit 18 Aarey Milk Colony\n Goregaon(E) Mumbai-65", headerFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			preface = new Paragraph(new SimpleDateFormat("dd-MMM-yyyy").format(fromDate)+" to "+new SimpleDateFormat("dd-MMM-yyyy").format(toDate), header2Font); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);

			PdfPTable table = new PdfPTable(6);
			table.setSpacingBefore(10);
			table.setSpacingAfter(11);
			table.setWidthPercentage(100);
			table.addCell(getHeaderCell("Customer", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Current Bill", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Prev Balance", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Total", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Payment Received", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Received Date", PdfPCell.ALIGN_CENTER, true));
			
			for(MilkCustomer customer : customers){
				double prevMilkSell = getPrevMilkSellTillDate(customer);
				double prevPaymentReceived = getPrevPaymentReceivedTillDate(customer);
				double prevBalance = customer.getPendingBillAmount() + prevMilkSell - prevPaymentReceived;
				double currBill = getCurrentBillAmount(customer);
				double total = currBill + prevBalance;
				
				table.addCell(getItemCell(customer.getCustomerName(), PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(currBill+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(prevBalance+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(total+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell("", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell("", PdfPCell.ALIGN_CENTER, false, 0));
				
			}
			
			
			document.add(table);
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PdfPCell getCell(String text, int alignment) {
		Phrase phrase = new Phrase(text, header2Font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    return cell;
	}
	
	public PdfPCell getItemCell(String text, int alignment, boolean headerCell, int border) {
		Font font = headerCell ? header2BoldFont : header2Font;
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(headerCell ? 30 : 20);
	    cell.setNoWrap(false);
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    
	    if(border == 1){
	    	cell.setBorder(PdfPCell.NO_BORDER);
	    }
	    cell.setBorderColor(BaseColor.BLACK);
	    return cell;
	}
	
	public PdfPCell getHeaderCell(String text, int alignment, boolean headerCell) {
		Font font = headerCell ? header2BoldFont : header2Font;
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(25);
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    //cell.setBorder(1);
	    cell.setBorderColor(BaseColor.BLACK);
	    return cell;
	}
	
	public double getPrevMilkSellTillDate(MilkCustomer customer){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "select sum(EveningMilk*MilkRate+ MorningMilk*MilkRate) from CustomerMilk "
				+ "WHERE CustomerId = "+customer.getId()+" and MilkDate < "+fromDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getPrevPaymentReceivedTillDate(MilkCustomer customer){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MilkPayment "
				+ "WHERE CustomerId = "+customer.getId()+" and PaymentDate < "+fromDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getCurrentBillAmount(MilkCustomer customer){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String date2 = new SimpleDateFormat("yyyy-MM-dd").format(toDate);
		String queryStr = "select (sum(EveningMilk*MilkRate)+SUM(MorningMilk*MilkRate)) FROM CustomerMilk "
				+ "WHERE CustomerId = "+customer.getId()+" AND MilkDate BETWEEN "+fromDate.getTime()+" and "+toDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
}
