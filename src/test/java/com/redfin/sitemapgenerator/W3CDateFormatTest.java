package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static com.redfin.sitemapgenerator.W3CDateFormat.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class W3CDateFormatTest {
	@Test
	void testFormatEpoch() {
		OffsetDateTime epoch = TestUtil.getEpochOffsetDateTime();
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
		OffsetDateTime date = TestUtil.getEpochOffsetDateTime();
		verifyPatternFormat(date, AUTO, "1970-01-01");
		date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:00:00.001Z");
		date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1000), ZoneOffset.UTC);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:00:01Z");
		date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(60000), ZoneOffset.UTC);
		verifyPatternFormat(date, AUTO, "1970-01-01T00:01Z");
		date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(60000 * 60 * 24), ZoneOffset.UTC);
		verifyPatternFormat(date, AUTO, "1970-01-02");
	}
	
	@Test
	void testFormatTimeZone() {
		OffsetDateTime epoch = TestUtil.getEpochOffsetDateTime();
		ZoneId tz = TimeZone.getTimeZone("PST").toZoneId();
		verifyPatternFormat(epoch, MILLISECOND.withZone(tz), "1969-12-31T16:00:00.000-08:00", tz);
		verifyPatternFormat(epoch, AUTO.withZone(tz), "1969-12-31T16:00-08:00", tz);
	}
	
	@Test
	void testParseEpoch() throws ParseException {
		OffsetDateTime date = TestUtil.getEpochOffsetDateTime();
		verifyPatternParse("1970-01-01T00:00:00.000Z", MILLISECOND, date);
		verifyPatternParse("1970-01-01T00:00:00Z", SECOND, date);
		verifyPatternParse("1970-01-01T00:00Z", MINUTE, date);
		verifyPatternParse("1970-01-01", DAY, date);
		verifyPatternParse("1970-01", MONTH, date);
		verifyPatternParse("1970", YEAR, date);
	}
	
	
	@Test
	void testAutoParse() throws ParseException {
		OffsetDateTime date = TestUtil.getEpochOffsetDateTime();
		verifyPatternParse("1970-01-01T00:00:00.000Z", AUTO, date);
		verifyPatternParse("1970-01-01T00:00:00Z", AUTO, date);
		verifyPatternParse("1970-01-01T00:00Z", AUTO, date);
		verifyPatternParse("1970-01-01", AUTO, date);
		verifyPatternParse("1970-01", AUTO, date);
		verifyPatternParse("1970", AUTO, date);
	}
	
	@Test
	void testParseTimeZone() throws ParseException {
		OffsetDateTime epoch = TestUtil.getEpochOffsetDateTime();
		verifyPatternParse("1969-12-31T16:00:00.000-08:00", MILLISECOND, epoch);
		verifyPatternParse("1969-12-31T16:00:00.000-08:00", AUTO, epoch);
	}
	
	private void verifyPatternFormat(OffsetDateTime date, W3CDateFormat pattern, String expected) {
		verifyPatternFormat(date, pattern, expected, ZoneOffset.UTC);
	}
	
	private void verifyPatternFormat(OffsetDateTime date, W3CDateFormat pattern, String expected, ZoneId tz) {

		assertEquals(expected, pattern.format(date), date.toString() + " " + pattern);
	}
	
	private void verifyPatternParse(String source, W3CDateFormat pattern, OffsetDateTime expected) throws ParseException {
		verifyPatternParse(source, pattern, expected, ZoneOffset.UTC);
	}
	
	private void verifyPatternParse(String source, W3CDateFormat pattern, OffsetDateTime expected, ZoneId tz) throws ParseException {
		OffsetDateTime actual = pattern.parse(source).atZoneSameInstant(tz).toOffsetDateTime();
		assertEquals(expected, actual, source + " " + pattern);
	}
	
}
