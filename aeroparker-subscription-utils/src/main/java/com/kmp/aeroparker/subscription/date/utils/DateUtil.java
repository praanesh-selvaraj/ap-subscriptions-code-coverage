package com.kmp.aeroparker.subscription.date.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtil
{
	private DateUtil()
	{
	}

	public static LocalDate nowLocalDate(final String timeZone)
	{
		return nowLocalDateTime(timeZone).toLocalDate();
	}

	public static LocalDateTime nowLocalDateTime(final String timeZone)
	{
		return ZonedDateTime.now(getValidTimeZone(timeZone))
				.toLocalDateTime();
	}

	public static Date nowDate(final String timeZone)
	{
		return Date.valueOf(nowLocalDate(timeZone));
	}

	public static ZoneId getValidTimeZone(final String timeZone)
	{
		if (isValidTimeZone(timeZone))
		{
			return ZoneId.of(timeZone);
		}
		else
		{
			return ZoneId.systemDefault();
		}
	}

	public static boolean isValidTimeZone(final String timeZone)
	{
		boolean valid = true;
		if (StringUtil.isEmpty(timeZone))
		{
			return false;
		}
		if (timeZone.trim()
				.length() == 0)
		{
			return false;
		}
		try
		{
			ZoneId.of(timeZone);
		}
		catch (DateTimeException e)
		{
			log.debug("Didn't recognise the timezone : " + timeZone);
			valid = false;
		}
		return valid;
	}

	/**
	 * Convert a date string to a LocalDate using the format.
	 * 
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static LocalDate strToLocalDate(final String dateString, final String format)
	{
		LocalDate localDate = LocalDate.now();
		if (!StringUtil.isEmpty(dateString) && !StringUtil.isEmpty(format))
		{
			try
			{
				localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
			}
			catch (DateTimeParseException | IllegalArgumentException e)
			{
				log.error("Error parsing date string to local date {}", e.getMessage(), e);
			}
		}
		return localDate;
	}

	/**
	 * Convert a LocalDate to a String in the format provided.
	 * 
	 * @param dateTime
	 * @param format
	 * @return
	 */
	public static String localDateToString(final LocalDate dateTime, final String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return dateTime.format(formatter);
	}

	/**
	 * Convert the LocalDateTime object to an SQL Timestamp object.
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime)
	{
		if (localDateTime == null)
		{
			localDateTime = LocalDateTime.now();
		}
		return Timestamp.valueOf(localDateTime);
	}

	public static Date localDateToDate(final LocalDate localDate)
	{
		return Date.valueOf(localDate);
	}

	public static Calendar localDateToCalendar(final LocalDate localDate)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
		return calendar;
	}

	public static Date timestampToDate(final Timestamp created)
	{
		return new Date(created.getTime());
	}

	/**
	 * Convert a Java Calendar object to a LocalDate
	 * 
	 * @param calendar
	 * @return LocalDate
	 */
	public static LocalDate calendarToLocalDate(Calendar calendar)
	{
		if (calendar == null)
		{
			calendar = Calendar.getInstance();
		}
		return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static LocalDate dateToLocalDate(final Date date)
	{
		if (date != null)
		{
			return date.toLocalDate();
		}
		return nowLocalDate("");
	}

	public static Date calendarToDate(final Calendar calendar)
	{
		LocalDate localDate = nowLocalDate("");
		if (calendar != null)
		{
			localDate = calendarToLocalDate(calendar);
		}
		return localDateToDate(localDate);
	}

	public static LocalDateTime timestampToLocalDateTime(final Timestamp timestamp)
	{
		if (timestamp != null)
		{
			return timestamp.toLocalDateTime();
		}
		return nowLocalDateTime("");
	}

	public static Calendar localDateTimeToCalendar(final LocalDateTime localDateTime)
	{
		Calendar calendar = Calendar.getInstance();
		if (localDateTime != null)
		{
			calendar.clear();
			calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(), localDateTime.getHour(),
					localDateTime.getMinute(), localDateTime.getSecond());
		}
		else
		{
			calendar = nowCalendar("");
		}
		return calendar;
	}

	public static Calendar nowCalendar(final String timeZone)
	{
		return localDateTimeToCalendar(nowLocalDateTime(timeZone));
	}

	public static Calendar timestampToCalendar(final Timestamp timestamp)
	{
		return localDateTimeToCalendar(timestampToLocalDateTime(timestamp));
	}

	/**
	 * Convert a Date object to a Calendar
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar dateToCalendar(final Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Timestamp nowTimestamp()
	{
		return dateToTimestamp(new java.util.Date());
	}
	
	public static Timestamp dateToTimestamp(java.util.Date date)
	{
		return new Timestamp(date.getTime());
	}
	
	/**
	 * Get timestamp from epoch seconds
	 * 
	 * @param seconds
	 * @return Timestamp object
	 */
	public static Timestamp epochSecondsToTimestamp(long seconds, String timezone)
	{
		Timestamp time = null;
		if (seconds == 0)
		{
			time = nowTimestamp();
		}
		else
		{
			Instant instant = Instant.ofEpochSecond(seconds);
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, getValidTimeZone(timezone));
			time = Timestamp.valueOf(localDateTime);
		}
		
		return time;
	}
}