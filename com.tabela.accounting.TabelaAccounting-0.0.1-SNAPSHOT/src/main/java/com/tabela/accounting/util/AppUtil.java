package com.tabela.accounting.util;


import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.util.StringConverter;

public class AppUtil {
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static java.sql.Date toSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.util.Date addMonths(int months) {
		java.util.Date d = new java.util.Date();

		Calendar c = Calendar.getInstance();

		c.setTime(d);

		c.set(10, 0);
		c.set(12, 0);
		c.set(13, 0);

		c.add(2, months);

		System.out.println("Loan End Date : " + c.getTime());

		return c.getTime();
	}

	public static java.util.Date getStartDate() {
		Calendar c = Calendar.getInstance();
		c.set(11, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);
		c.set(5, 1);

		System.out.println("Month : " + c.get(2));
		System.out.println("Start Date : " + c.getTime());

		return c.getTime();
	}

	public static java.util.Date getEndDate() {
		int[] days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		Calendar c2 = Calendar.getInstance();
		int month = c2.get(2);
		int lastDay = month == 1 ? 28 : c2.get(1) % 4 == 0 ? 29 : days[month];

		c2.set(11, 23);
		c2.set(12, 59);
		c2.set(13, 59);
		c2.set(14, 0);
		c2.set(5, lastDay);

		System.out.println("Month : " + c2.get(2));
		System.out.println("End Date : " + c2.getTime());

		return c2.getTime();
	}

	public static String getUserHomeDirectory() {
		return new File(".").getAbsolutePath();
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static java.util.Date toUtilDate(LocalDate localDate) {
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return java.util.Date.from(instant);
	}

	public static LocalDate toLocalDate(java.util.Date date) {
		Instant instant = date.toInstant();
		LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
		return localDate;
	}

	public static StringConverter<LocalDate> getDatePickerFormatter() {
		String pattern = "dd-MM-yyyy";
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			public String toString(LocalDate date) {
				if (date != null) {
					return this.dateFormatter.format(date);
				}
				return "";
			}

			public LocalDate fromString(String string) {
				if ((string != null) && (!string.isEmpty())) {
					return LocalDate.parse(string, this.dateFormatter);
				}
				return null;
			}
		};
		return converter;
	}

	public static String getDateFormat() {
		return "dd-MM-yyyy HH:mm:ss";
	}
}
