package com.kmp.aeroparker.subscription.date.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class DateUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(DateUtil.class);
	}

	@Test
	void testNowLocalDate()
	{
		// Null time zone
		LocalDate localDate = DateUtil.nowLocalDate(null);
		assertNotNull(localDate);
		System.out.println(localDate);
		// Empty time zone
		localDate = DateUtil.nowLocalDate("");
		assertNotNull(localDate);
		System.out.println(localDate);
		// Gibberish time zone
		localDate = DateUtil.nowLocalDate("x89*");
		assertNotNull(localDate);
		System.out.println(localDate);
		// Correct time zone
		localDate = DateUtil.nowLocalDate("Europe/London");
		assertNotNull(localDate);
		System.out.println(localDate);
		localDate = DateUtil.nowLocalDate("America/New_York");
		assertNotNull(localDate);
		System.out.println(localDate);
	}

	@Test
	void testNowDate()
	{
		// Null time zone
		Date dateObject = DateUtil.nowDate(null);
		assertNotNull(dateObject);
		System.out.println(dateObject);
		// Empty time zone
		dateObject = DateUtil.nowDate("");
		assertNotNull(dateObject);
		System.out.println(dateObject);
		// Gibberish time zone
		dateObject = DateUtil.nowDate("x89*");
		assertNotNull(dateObject);
		System.out.println(dateObject);
		// Correct time zone
		dateObject = DateUtil.nowDate("Europe/London");
		assertNotNull(dateObject);
		System.out.println(dateObject);
		dateObject = DateUtil.nowDate("America/New_York");
		assertNotNull(dateObject);
		System.out.println(dateObject);
	}

	@Test
	void testNowLocalDateTime()
	{
		// Null time zone
		LocalDateTime localDateTime = DateUtil.nowLocalDateTime(null);
		assertNotNull(localDateTime);
		System.out.println(localDateTime);
		// Empty time zone
		localDateTime = DateUtil.nowLocalDateTime("");
		assertNotNull(localDateTime);
		System.out.println(localDateTime);
		// Gibberish time zone
		localDateTime = DateUtil.nowLocalDateTime("x89*");
		assertNotNull(localDateTime);
		System.out.println(localDateTime);
		// Correct time zone
		localDateTime = DateUtil.nowLocalDateTime("Europe/London");
		assertNotNull(localDateTime);
		System.out.println(localDateTime);
		localDateTime = DateUtil.nowLocalDateTime("America/New_York");
		assertNotNull(localDateTime);
		System.out.println(localDateTime);
	}

	@Test
	void testIsValidTimeZone()
	{
		assertThat(DateUtil.isValidTimeZone("Europe/London")).isTrue();
	}

	@Test
	void testIsValidTimeZone_Invalid_TimeZone()
	{
		assertThat(DateUtil.isValidTimeZone("Europe/Londo")).isFalse();
	}

	@Test
	void ttestIsValidTimeZone_Empty_TimeZone()
	{
		assertThat(DateUtil.isValidTimeZone("")).isFalse();
	}

	@Test
	void testIsValidTimeZone_Invalid_Length_TimeZone()
	{
		assertThat(DateUtil.isValidTimeZone("      ")).isFalse();
	}

	@Test
	void tesGetValidTimeZone()
	{
		assertThat(DateUtil.getValidTimeZone("Europe/London")).isEqualTo(ZoneId.of("Europe/London"));
	}

	@Test
	void tesGetValidTimeZone_System_Default()
	{
		assertThat(DateUtil.getValidTimeZone("Europ/London")).isEqualTo(ZoneId.systemDefault());
	}

	@Test
	void testStrToLocalDate()
	{
		assertThat(DateUtil.strToLocalDate("28/11/2019", "dd/MM/yyyy"))
				.isEqualTo(LocalDate.parse("28/11/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	}

	@Test
	void testStrToLocalDate_DateTimeParseException()
	{
		assertThat(DateUtil.strToLocalDate("28/11/2019", "dd-MM-yyyy")).isEqualTo(LocalDate.now());
	}

	@Test
	void testStrToLocalDate_DateString_Empty()
	{
		assertThat(DateUtil.strToLocalDate("", "dd-MM-yyyy")).isEqualTo(LocalDate.now());
	}

	@Test
	void testStrToLocalDate_Format_Empty()
	{
		assertThat(DateUtil.strToLocalDate("28/11/2019", "")).isEqualTo(LocalDate.now());
	}

	@Test
	void testLocalDateToString()
	{
		assertThat(DateUtil.localDateToString(DateUtil.strToLocalDate("28/11/2019", "dd/MM/yyyy"), "MM-dd-yyyy")).isEqualTo("11-28-2019");
	}

	@Test
	void testLocalDateTimeToTimestamp()
	{
		LocalDateTime dateTime = LocalDateTime.now();
		assertThat(DateUtil.localDateTimeToTimestamp(dateTime)).isEqualTo(Timestamp.valueOf(dateTime));
	}

	@Test
	void testLocalDateTimeToTimestamp_LocalDateTime_Null()
	{
		assertThat(DateUtil.localDateTimeToTimestamp(null)).isNotNull();
	}

	@Test
	void testLocalDateToDate()
	{
		LocalDate dateTime = LocalDate.now();
		assertThat(DateUtil.localDateToDate(dateTime)).isEqualTo(Date.valueOf(dateTime));
	}

	@Test
	void testLocalDateToCalendar()
	{
		LocalDate dateTime = LocalDate.now();
		Calendar calendar = DateUtil.localDateToCalendar(dateTime);
		assertThat(calendar).isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(dateTime.getDayOfMonth());
	}

	@Test
	void testTimestampToDate() throws ParseException
	{
		assertThat(DateUtil.timestampToDate(new Timestamp(System.currentTimeMillis()))).isNotNull()
				.isInstanceOf(Date.class);
	}

	@Test
	void testCalendarToLocalDate() throws ParseException
	{
		assertThat(DateUtil.calendarToLocalDate(Calendar.getInstance())).isNotNull()
				.isInstanceOf(LocalDate.class);
	}

	@Test
	void testCalendarToLocalDate_Calendar_Null() throws ParseException
	{
		assertThat(DateUtil.calendarToLocalDate(null)).isNotNull()
				.isInstanceOf(LocalDate.class);
	}

	@Test
	void testDateToLocalDate() throws ParseException
	{
		assertThat(DateUtil.dateToLocalDate(DateUtil.localDateToDate(LocalDate.now()))).isNotNull()
				.isInstanceOf(LocalDate.class);
	}

	@Test
	void testDateToLocalDate_Null() throws ParseException
	{
		assertThat(DateUtil.dateToLocalDate(null)).isNotNull()
				.isInstanceOf(LocalDate.class);
	}

	@Test
	void testCalendarToDate() throws ParseException
	{
		assertThat(DateUtil.calendarToDate(Calendar.getInstance())).isNotNull()
				.isInstanceOf(Date.class);
	}

	@Test
	void testCalendarToDate_Calendar_Null() throws ParseException
	{
		assertThat(DateUtil.calendarToDate(null)).isNotNull()
				.isInstanceOf(Date.class);
	}

	@Test
	void testTimestampToLocalDateTime() throws ParseException
	{
		Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
		assertThat(DateUtil.timestampToLocalDateTime(nowTimestamp)).isNotNull()
				.isInstanceOf(LocalDateTime.class);
	}

	@Test
	void testTimestampToLocalDateTime_Null() throws ParseException
	{
		assertThat(DateUtil.timestampToLocalDateTime(null)).isNotNull()
				.isInstanceOf(LocalDateTime.class);
	}

	@Test
	void testLocalDateTimeToCalendar() throws ParseException
	{
		LocalDateTime now = LocalDateTime.now();
		Calendar calendar = DateUtil.localDateTimeToCalendar(now);
		assertThat(calendar).isNotNull()
				.isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.YEAR)).isEqualTo(now.getYear());
		assertThat(calendar.get(Calendar.MONTH) + 1).isEqualTo(now.getMonthValue());
	}

	@Test
	void testLocalDateTimeToCalendar_Null() throws ParseException
	{
		LocalDateTime now = LocalDateTime.now();
		Calendar calendar = DateUtil.localDateTimeToCalendar(null);
		assertThat(calendar).isNotNull()
				.isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.YEAR)).isEqualTo(now.getYear());
		assertThat(calendar.get(Calendar.MONTH) + 1).isEqualTo(now.getMonthValue());
	}

	@Test
	void testNowCalendar() throws ParseException
	{
		LocalDateTime now = DateUtil.nowLocalDateTime("Europe/London");
		Calendar calendar = DateUtil.nowCalendar("Europe/London");
		assertThat(calendar).isNotNull()
				.isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.YEAR)).isEqualTo(now.getYear());
		assertThat(calendar.get(Calendar.MONTH) + 1).isEqualTo(now.getMonthValue());
	}

	@Test
	void testTimestampToCalendar() throws ParseException
	{
		Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar calendar = DateUtil.timestampToCalendar(nowTimestamp);
		assertThat(calendar).isNotNull()
				.isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.YEAR)).isEqualTo(nowTimestamp.toLocalDateTime()
				.getYear());
		assertThat(calendar.get(Calendar.MONTH) + 1).isEqualTo(nowTimestamp.toLocalDateTime()
				.getMonthValue());
	}

	@Test
	void testDateToCalendar() throws ParseException
	{
		Date dateTime = DateUtil.localDateToDate(LocalDate.now());
		Calendar calendar = DateUtil.dateToCalendar(dateTime);
		assertThat(calendar).isNotNull()
				.isInstanceOf(Calendar.class);
		assertThat(calendar.get(Calendar.YEAR)).isEqualTo(dateTime.toLocalDate()
				.getYear());
	}
	
	@Test
	public void epochSecondsToTimestamp()
	{
		assertEquals(Timestamp.valueOf("2025-09-08 13:27:01.0"),
				DateUtil.epochSecondsToTimestamp(1757334421, "Europe/London"));
	}
}