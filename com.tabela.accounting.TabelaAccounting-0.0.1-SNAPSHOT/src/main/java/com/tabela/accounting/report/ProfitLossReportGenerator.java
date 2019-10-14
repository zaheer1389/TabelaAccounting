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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Chunk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.JPAFacade;

public class ProfitLossReportGenerator {

	Document document;
	PdfWriter writer;
	
	Font headerFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLDITALIC, BaseColor.BLACK);
	Font noteFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	Font header2Font = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.NORMAL, BaseColor.BLACK);
	Font header2BoldFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	
	Font figureCaptionFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLDITALIC, BaseColor.BLACK);
	Font figureFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 10, Font.ITALIC, BaseColor.BLACK);
	
	private String expenseType [] = {"Tabela General", "Salvage Exp", "Medical Exp", "Construction Work", 
    		"Electricity Exp", "Society Payment", "Buffalo Purchased", "Freight Paid", "Heil Charges", "Dairy Exp", "Conveyance/Travelling", 
    		"Printing/Stationary", "Telephone/Mobile Exp", "Transportation Exp", "Travelling Exp", "Diwali", "Other"};
	
	Date fromDate;
	Date toDate;
	
	double openingBal = 0, closingBal = 0;
	double totalIncome = 0, totalExpense = 0;
	
	public File generate(Date fromDate, Date toDate) {

		this.fromDate = fromDate;
		this.toDate = toDate;
		
		document = new Document();
		try {
			document.setMargins(15, 15, 10, 15);
			writer = PdfWriter.getInstance(document, new FileOutputStream(System.getProperty("user.home")+"/Tabela Accounting/ProfitLossReport.pdf"));
			document.open();
			process();
			document.close();
			writer.close();
			
			return new File(System.getProperty("user.home")+"/Tabela Accounting/ProfitLossReport.pdf");
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public void process(){
		try {

			Paragraph preface = new Paragraph("Profit/Loss Report", headerFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			preface = new Paragraph("Manasiya Dairy \n Unit 18 Aarey Milk Colony\n Goregaon(E) Mumbai-65", headerFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			preface = new Paragraph(new SimpleDateFormat("dd-MMM-yyyy").format(fromDate)+" to "
											+new SimpleDateFormat("dd-MMM-yyyy").format(toDate), header2Font); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			PdfContentByte contentByte = writer.getDirectContent();
	        contentByte.setLineWidth(1);
	        contentByte.moveTo(document.getPageSize().getWidth()/2, 80);
	        contentByte.lineTo(document.getPageSize().getWidth()/2, document.getPageSize().getHeight()-130);
	        contentByte.stroke();
	        
	        openingBal += getTotalMilkPaymentBeforeDate(fromDate);
	        openingBal -= getTotalMerchantPaymentBeforeDate(fromDate);
	        
	        for(String expense : expenseType){
	        	openingBal -= getTotalExpenseBeforeDate(expense, fromDate);
	        }
	        
	        document.add(new Paragraph("\n"));
	        document.add(new Paragraph("Opening Balance :- Rs. "+openingBal, figureCaptionFont));
			
	        closingBal = openingBal;
	        
			PdfPTable table = new PdfPTable(2);
			table.setSpacingBefore(10);
			table.setSpacingAfter(11);
			table.setWidthPercentage(100);
			
			PdfPCell cellh1 = new PdfPCell(new Phrase("Income", headerFont));
			cellh1.setFixedHeight(30);
			cellh1.setNoWrap(false);
			cellh1.setPadding(1);
			cellh1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellh1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cellh1.setBorder(PdfPCell.NO_BORDER);
			//cellh1.setCellEvent(new DottedCell(PdfPCell.RIGHT));
			//cellh1.setBackgroundColor(BaseColor.GRAY);
		    
		    PdfPCell cellh2 = new PdfPCell(new Phrase("Expense", headerFont));
		    cellh2.setFixedHeight(30);
		    cellh2.setNoWrap(false);
		    cellh2.setPadding(1);
		    cellh2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		    cellh2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		    cellh2.setBorder(PdfPCell.NO_BORDER);
			//cellh1.setCellEvent(new DottedCell(PdfPCell.RIGHT));
			//cellh2.setBackgroundColor(BaseColor.GRAY);
	        
			PdfPCell cell = new PdfPCell(getIncomeTable());
		    cell.setNoWrap(false);
		    cell.setPadding(1);
		    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
		    cell.setBorderColor(BaseColor.BLACK);
		    cell.setBorder(PdfPCell.NO_BORDER);
		    
		    PdfPCell cell2 = new PdfPCell(getExpenseTable());
		    cell2.setNoWrap(false);
		    cell2.setPadding(1);
		    cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		    cell2.setBorderColor(BaseColor.BLACK);
		    cell2.setBorder(PdfPCell.NO_BORDER);
		    
		    table.addCell(cellh1);
		    table.addCell(cellh2);
		    
		    table.addCell(cell);
		    table.addCell(cell2);
		    
		    PdfPCell cell22 = new PdfPCell(new Paragraph("\n"));
		    cell22.setNoWrap(false);
		    cell22.setPadding(1);
		    cell22.setVerticalAlignment(PdfPCell.ALIGN_TOP);
		    cell22.setBorderColor(BaseColor.BLACK);
		    cell22.setBorder(PdfPCell.NO_BORDER);
		    
		    table.addCell(cell22);
		    table.addCell(cell22);
		    
		    PdfPCell cell23 = new PdfPCell(getGrandTotalTable("Grand Total", totalIncome+""));
		    cell23.setNoWrap(false);
		    cell23.setPadding(1);
		    cell23.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		    cell23.setBorderColor(BaseColor.BLACK);
		    cell23.setBorder(PdfPCell.NO_BORDER);
		    
		    PdfPCell cell24 = new PdfPCell(getGrandTotalTable("Grand Total", totalExpense+""));
		    cell24.setNoWrap(false);
		    cell24.setPadding(1);
		    cell24.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		    cell24.setBorderColor(BaseColor.BLACK);
		    cell24.setBorder(PdfPCell.NO_BORDER);
		    
		    table.addCell(cell23);
		    table.addCell(cell24);
		    
		    document.add(table);
		    
		    document.add(new Paragraph("\n"));
	        document.add(new Paragraph("Closing Balance :- Rs. "+closingBal, figureCaptionFont));
	        
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PdfPTable getIncomeTable(){
		PdfPTable incomeTable = new PdfPTable(2);
		incomeTable.setSpacingBefore(10);
		incomeTable.setSpacingAfter(11);
		incomeTable.setWidthPercentage(100);
		try {
			incomeTable.setWidths(new int[]{220, 120});
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double milkSell = getTotalMilkPaymentForDateRange(fromDate, toDate);
		totalIncome = milkSell;
		closingBal += milkSell;
		incomeTable.addCell(getItemCell("    Milk Sell", figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
		incomeTable.addCell(getItemCell("Rs. "+milkSell, figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
	    
		return incomeTable;
	}
	
	public PdfPTable getExpenseTable(){
		PdfPTable incomeTable = new PdfPTable(2);
		incomeTable.setSpacingBefore(10);
		incomeTable.setSpacingAfter(11);
		incomeTable.setWidthPercentage(100);
		try {
			incomeTable.setWidths(new int[]{220, 120});
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double merchantPayment = getTotalMerchantPaymentForDateRange(fromDate, toDate);
		totalExpense += merchantPayment;
		closingBal -= merchantPayment;
		incomeTable.addCell(getItemCell("    Merchant Payment ", figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
		incomeTable.addCell(getItemCell("Rs. "+merchantPayment, figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
		
		for(String expense : expenseType){
			double expenseMade = getTotalExpenseForDateRange(expense, fromDate, toDate);
			totalExpense += expenseMade;
			closingBal -= expenseMade;
			incomeTable.addCell(getItemCell("    "+expense+" ", figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
			incomeTable.addCell(getItemCell("Rs. "+expenseMade, figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
			
        }
        
	    
		return incomeTable;
	}
	
	public PdfPTable getGrandTotalTable(String caption, String value){
		PdfPTable incomeTable = new PdfPTable(2);
		incomeTable.setSpacingBefore(10);
		incomeTable.setSpacingAfter(11);
		incomeTable.setWidthPercentage(100);
		try {
			incomeTable.setWidths(new int[]{220, 120});
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		incomeTable.addCell(getItemCell("    "+caption, figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
		incomeTable.addCell(getItemCell("Rs. "+value, figureCaptionFont, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_TOP));
	    
		return incomeTable;
	}
	
	public PdfPCell getItemCell(String text, Font font, int horizontalAlign, int verticalAlign) {
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(20);
	    cell.setNoWrap(false);
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(horizontalAlign);
	    cell.setVerticalAlignment(verticalAlign);
	    cell.setBorder(PdfPCell.NO_BORDER);

	    return cell;
	}
	
	public PdfPCell getCell(String text, int alignment) {
		Phrase phrase = new Phrase(text, header2Font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    return cell;
	}
	
	public PdfPCell getHeaderCell(String text, int alignment, boolean headerCell) {
		Font font = headerCell ? header2BoldFont : header2Font;
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(20);
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    //cell.setBorder(1);
	    cell.setBorderColor(BaseColor.BLACK);
	    return cell;
	}
	
	public double getTotalMilkPaymentBeforeDate(Date fromDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MilkPayment "
				+ "WHERE PaymentDate < "+fromDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getTotalMilkPaymentForDateRange(Date fromDate, Date toDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MilkPayment "
				+ "WHERE PaymentDate BETWEEN "+fromDate.getTime()+" and "+toDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getTotalMerchantPaymentBeforeDate(Date fromDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MerchantPayment "
				+ "WHERE PaymentDate < "+fromDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getTotalMerchantPaymentForDateRange(Date fromDate, Date toDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MerchantPayment "
				+ "WHERE PaymentDate BETWEEN "+fromDate.getTime()+" and "+toDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getTotalExpenseBeforeDate(String expenseType, Date fromDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM Expense "
				+ "WHERE ExpenseType='"+expenseType+"' AND ExpenseDate < "+fromDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
	public double getTotalExpenseForDateRange(String expenseType, Date fromDate, Date toDate){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM Expense "
				+ "WHERE ExpenseType='"+expenseType+"' AND ExpenseDate BETWEEN "+fromDate.getTime()+" and "+toDate.getTime()+"";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}

}
