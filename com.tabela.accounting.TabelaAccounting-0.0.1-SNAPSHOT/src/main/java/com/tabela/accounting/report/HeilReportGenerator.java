package com.tabela.accounting.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tabela.accounting.controllers.TempoController;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.model.Tempo;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.persistence.JPAFacade;

public class HeilReportGenerator {

	Document document;
	
	Font headerFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLDITALIC, BaseColor.BLACK);
	Font noteFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	Font header2Font = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.NORMAL, BaseColor.BLACK);
	Font header2BoldFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 11, Font.BOLD, BaseColor.BLACK);
	
	List<Tempo> tempos;
	Date fromDate;
	Date toDate;
	
	boolean dataAvailable = false;
	
	public File generate(List<Tempo> tempos, Date fromDate, Date toDate) {

		this.tempos = tempos;
		this.fromDate = fromDate;
		this.toDate = toDate;
		
		document = new Document();
		try {
			document.setMargins(15, 15, 10, 15);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(System.getProperty("user.home")+"/Tabela Accounting/HeilReport.pdf"));
			document.open();
			
			
			Paragraph preface = new Paragraph("Heil Charges Report", headerFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			preface = new Paragraph("Manasiya Dairy \n Unit 18 Aarey Milk Colony\n Goregaon(E) Mumbai-65", headerFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			preface = new Paragraph(new SimpleDateFormat("dd-MMM-yyyy").format(fromDate)+" to "+new SimpleDateFormat("dd-MMM-yyyy").format(toDate), header2Font); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			
			PdfPTable table = new PdfPTable(5);
			table.setSpacingBefore(10);
			table.setSpacingAfter(11);
			table.setWidthPercentage(100);
			
			table.addCell(getHeaderCell("  Tempo", PdfPCell.ALIGN_LEFT, true, 0));
			table.addCell(getHeaderCell("Morning Milk(ltrs)", PdfPCell.ALIGN_CENTER, true, 0));
			table.addCell(getHeaderCell("Evening Milk(ltrs)", PdfPCell.ALIGN_CENTER, true, 0));
			table.addCell(getHeaderCell("Total Milk(ltrs)", PdfPCell.ALIGN_CENTER, true, 0));
			table.addCell(getHeaderCell("Heil Amount", PdfPCell.ALIGN_CENTER, true, 0));
			
			for(Tempo tempo : tempos){
				double eveningMilk = 0, morningMilk = 0;
				double totalMilk = 0;
				List<MilkCustomer> milkCustomers = TempoController.getTempoCustomers(tempo);
				for(MilkCustomer milkCustomer : milkCustomers){
					List<CustomerMilk> milks = getMilks(milkCustomer);
					if(milks != null && milks.size() > 0){
						dataAvailable = true;
						for(CustomerMilk customerMilk : milks){
							eveningMilk += customerMilk.getEveningMilk();
							morningMilk += customerMilk.getMorningMilk();
						}
					}
				}
				
				totalMilk = eveningMilk + morningMilk;
				
				table.addCell(getItemCell(" "+tempo.getTempoName(), PdfPCell.ALIGN_LEFT, false, 0));
				table.addCell(getItemCell(eveningMilk+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(morningMilk+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(totalMilk+"", PdfPCell.ALIGN_CENTER, false, 0));
				table.addCell(getItemCell(totalMilk+"", PdfPCell.ALIGN_CENTER, false, 0));
				
			}
			
			if(!dataAvailable){
				document.add(new Paragraph("No milk data found to create bill"));
			}
			
			document.add(table);
			
			document.close();
			writer.close();
			
			return new File(System.getProperty("user.home")+"/Tabela Accounting/HeilReport.pdf");
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
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
	
	public PdfPCell getHeaderCell(String text, int alignment, boolean headerCell, int border) {
		Font font = headerCell ? header2BoldFont : header2Font;
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(headerCell ? 30 : 20);
	    cell.setNoWrap(false);
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    cell.setBackgroundColor(BaseColor.GRAY);
	    
	    if(border == 1){
	    	cell.setBorder(PdfPCell.NO_BORDER);
	    }
	    cell.setBorderColor(BaseColor.BLACK);
	    return cell;
	}
	
	public PdfPCell getFooterCell(String text, int alignment, boolean headerCell, int border) {
		Font font = headerCell ? header2BoldFont : header2Font;
		Phrase phrase = new Phrase(text, font);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setFixedHeight(20);
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
	
	public List<CustomerMilk> getMilks(MilkCustomer customer){
		String queryStr = "Select c from CustomerMilk as c where c.customer = :customer and c.milkDate between :fromDate and :toDate";
		Map<String, Object> parameters = new HashMap();
		parameters.put("customer", customer);
		parameters.put("fromDate", fromDate);
		parameters.put("toDate", toDate);
		return FacadeFactory.getFacade().list(queryStr, parameters);
	}
	
	public double getPrevMilkSellTillDate(MilkCustomer customer){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "select sum(EveningMilk*MilkRate+ MorningMilk*MilkRate) from CustomerMilk "
				+ "WHERE CustomerId = "+customer.getId()+" and MilkDate < '"+date+"'";
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
				+ "WHERE CustomerId = "+customer.getId()+" and PaymentDate < '"+date+"'";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		Object value = list != null && list.size() > 0 ? list.get(0) : 0;
		return value != null ? Double.parseDouble(value+"") : 0;
	}
	
}
