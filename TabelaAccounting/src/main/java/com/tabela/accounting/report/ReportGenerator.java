package com.tabela.accounting.report;

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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.persistence.JPAFacade;

public class ReportGenerator {

	Document document;
	
	Font headerFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 15, Font.BOLD, BaseColor.BLACK);
	Font noteFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 12, Font.BOLD, BaseColor.BLACK);
	Font header2Font = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 12, Font.NORMAL, BaseColor.BLACK);
	Font header2BoldFont = FontFactory.getFont(FontFactory.getFont("Arial").getFamilyname(), 12, Font.BOLD, BaseColor.BLACK);
	
	List<MilkCustomer> customers;
	Date fromDate;
	Date toDate;
	
	public void generate(List<MilkCustomer> customers, Date fromDate, Date toDate) {

		this.customers = customers;
		this.fromDate = fromDate;
		this.toDate = toDate;
		
		document = new Document();
		try {
			document.setMargins(15, 15, 10, 15);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/home/zaheer/HelloWorld.pdf"));
			document.open();
			int count = 1;
			for(MilkCustomer customer : customers){
				process(customer);
				if(count % 2 == 0){
					document.newPage();
				}
				count++;
			}
			document.close();
			writer.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void process(MilkCustomer customer){
		Paragraph preface = new Paragraph("Milk Invoice", headerFont); 
		preface.setAlignment(Element.ALIGN_CENTER);
		try {
			document.add(preface);

			PdfPTable table = new PdfPTable(2);
			table.setSpacingBefore(10);
			table.setSpacingAfter(8);
			table.setWidthPercentage(100);
			table.addCell(getCell("M/S "+customer.getCustomerName(), PdfPCell.ALIGN_LEFT));
			table.addCell(getCell("Bill Date : "+new SimpleDateFormat("dd-MMM-yyyy").format(new Date()), PdfPCell.ALIGN_RIGHT));
			document.add(table);

			table = new PdfPTable(1);
			//table.setSpacingAfter(10);
			table.setWidthPercentage(100);
			table.addCell(getCell("Address : "+customer.getCustomerAddress(), PdfPCell.ALIGN_LEFT));
			document.add(table);
			
			table = new PdfPTable(5);
			table.setSpacingBefore(10);
			table.setSpacingAfter(11);
			table.setWidthPercentage(100);
			table.addCell(getHeaderCell("  Milk Date", PdfPCell.ALIGN_LEFT, true));
			table.addCell(getHeaderCell("  Milk Rate", PdfPCell.ALIGN_LEFT, true));
			table.addCell(getHeaderCell("Morning", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Evening", PdfPCell.ALIGN_CENTER, true));
			table.addCell(getHeaderCell("Total", PdfPCell.ALIGN_CENTER, true));
			
			double prevMilkSell = getPrevMilkSellTillDate(customer);
			System.out.println("Prev milk sell "+prevMilkSell);
			double prevPaymentReceived = getPrevPaymentReceivedTillDate(customer);
			System.out.println("prev payment received "+prevPaymentReceived);
			double prevBalance = customer.getPendingBillAmount() + prevMilkSell - prevPaymentReceived;
			
			double totalMilk = 0;
			double totalMilkPrice = 0;
			List<CustomerMilk> milks = getMilks(customer);
			for(CustomerMilk milk : milks){
				table.addCell(getHeaderCell(" "+new SimpleDateFormat("dd-MMM-yyyy").format(milk.getMilkDate()), PdfPCell.ALIGN_LEFT, false));
				table.addCell(getHeaderCell(" "+milk.getMilkRate()+"", PdfPCell.ALIGN_LEFT, false));
				table.addCell(getHeaderCell(milk.getMorningMilk()+"", PdfPCell.ALIGN_CENTER, false));
				table.addCell(getHeaderCell(milk.getEveningMilk()+"", PdfPCell.ALIGN_CENTER, false));
				double dayTotal = (milk.getMorningMilk()+milk.getEveningMilk());
				totalMilk += dayTotal;
				totalMilkPrice += (milk.getMorningMilk()*milk.getMilkRate()) + (milk.getEveningMilk()*milk.getMilkRate());
				table.addCell(getHeaderCell(dayTotal+"", PdfPCell.ALIGN_CENTER, false));
			}
			
			document.add(table);
			
			double grandTotal = totalMilkPrice + prevBalance;
			
			table = new PdfPTable(1);
			table.setSpacingAfter(5);
			table.setWidthPercentage(100);
			table.addCell(getCell("Current Total : "+totalMilkPrice, PdfPCell.ALIGN_RIGHT));
			document.add(table);
			
			table = new PdfPTable(1);
			table.setSpacingAfter(5);
			table.setWidthPercentage(100);
			table.addCell(getCell("Prev Balance : "+prevBalance, PdfPCell.ALIGN_RIGHT));
			document.add(table);
			
			table = new PdfPTable(1);
			table.setSpacingAfter(20);
			table.setWidthPercentage(100);
			table.addCell(getCell("Grand Total : "+grandTotal, PdfPCell.ALIGN_RIGHT));
			document.add(table);
			
			preface = new Paragraph("This is computer generated receipt that is why it does not require any signature", noteFont); 
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);
			document.add(new Paragraph("    "));
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
		return list != null && list.size() > 0 ? Double.parseDouble(list.get(0).toString()) : 0;
	}
	
	public double getPrevPaymentReceivedTillDate(MilkCustomer customer){
		String date = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);
		String queryStr = "SELECT sum(Amount) FROM MilkPayment "
				+ "WHERE CustomerId = "+customer.getId()+" and PaymentDate < '"+date+"'";
		System.out.println("query : "+queryStr);
		EntityManager em = JPAFacade.getEntityManager();
		Query query = em.createNativeQuery(queryStr);
		List list = query.getResultList();
		return list != null && list.size() > 0 ? Double.parseDouble(list.get(0).toString()) : 0;
	}
	
}