package com.redfin.sitemapgenerator;

import com.redfin.sitemapgenerator.W3CDateFormat.Pattern;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.AUTO;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.DAY;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.MILLISECOND;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.MINUTE;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.MONTH;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.SECOND;
import static com.redfin.sitemapgenerator.W3CDateFormat.Pattern.YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class W3CDateFormatTest {
	@Test
	void testFormatEpoch() {
		Date epoch = new Date(0);
		verifyPatternFormat(epoch, MILLISECOND, "1970-01-01T00:00:00.000Z");
		verifyPatternFormat(epoch, SECOND, "1970-01-01T00:00:00Z");
		verifyPatternFormat(epoch, MINUTE, "1970-01-01T00:00Z");
		verifyPatternFormat(epoch, DAY, "1970-01-01");
		verifyPatternFormat(epoch, MONTH, "1970-01");
		verifyPatternFormat(epoch, YEAR, "1970");
		verifyPatternFormat(epoch, AUTO, "1970-01-01");
	}
	
	@Test
	void testAutoFormat() {
		Date date = new Date(0);
		verifyPatternFormat(date, AUTO, "1970-01-01");
		date = new Date(1);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:00:00.001Z");
		date = new Date(1000);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:00:01Z");
		date = new Date(60000);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:01Z");
		date = new Date(60000 * 60 * 24);
		verifyPatternFormat(date, AUTO, "1970-01-02");
	}
	
	@Test
	void testFormatTimeZone() {
		Date epoch = new Date(0);
		TimeZone tz = TimeZone.getTimeZone("PST");
		verifyPatternFormat(epoch, MILLISECOND, "1969-12-31T16:00:00.000-08:00", tz);
		verifyPatternFormat(epoch, AUTO, "1969-12-31T16:00-08:00", tz);
	}
	
	@Test
	void testParseEpoch() throws ParseException {
		Date date = new Date(0);
		verifyPatternParse("1970-01-01T00:00:00.000Z", MILLISECOND, date);
		verifyPatternParse("1970-01-01T00:00:00Z", SECOND, date);
		verifyPatternParse("1970-01-01T00:00Z", MINUTE, date);
		verifyPatternParse("1970-01-01", DAY, date);
		verifyPatternParse("1970-01", MONTH, date);
		verifyPatternParse("1970", YEAR, date);
	}
	
	
	@Test
	void testAutoParse() throws ParseException {
		Date date = new Date(0);
		verifyPatternParse("1970-01-01T00:00:00.000Z", AUTO, date);
		verifyPatternParse("1970-01-01T00:00:00Z", AUTO, date);
		verifyPatternParse("1970-01-01T00:00Z", AUTO, date);
		verifyPatternParse("1970-01-01", AUTO, date);
		verifyPatternParse("1970-01", AUTO, date);
		verifyPatternParse("1970", AUTO, date);
	}
	
	@Test
	void testParseTimeZone() throws ParseException {
		Date epoch = new Date(0);
		verifyPatternParse("1969-12-31T16:00:00.000-08:00", MILLISECOND, epoch);
		verifyPatternParse("1969-12-31T16:00:00.000-08:00", AUTO, epoch);
	}
	
	private void verifyPatternFormat(Date date, Pattern pattern, String expected) {
		verifyPatternFormat(date, pattern, expected, W3CDateFormat.ZULU);
	}
	
	private void verifyPatternFormat(Date date, Pattern pattern, String expected, TimeZone tz) {
		W3CDateFormat format = new W3CDateFormat(pattern);
		format.setTimeZone(tz);
		assertEquals(expected, format.format(date), date.toString() + " " + pattern);
	}
	
	private void verifyPatternParse(String source, Pattern pattern, Date expected) throws ParseException {
		verifyPatternParse(source, pattern, expected, W3CDateFormat.ZULU);
	}
	
	private void verifyPatternParse(String source, Pattern pattern, Date expected, TimeZone tz) throws ParseException {
		W3CDateFormat format = new W3CDateFormat(pattern);
		format.setTimeZone(tz);
		Date actual = format.parse(source);
		assertEquals(expected, actual, source + " " + pattern);
	}
	
}
