package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SitemapIndexGeneratorTest {

	private static final String INDEX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap1.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap2.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap3.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap4.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap5.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap6.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap7.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap8.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap9.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>http://www.example.com/sitemap10.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"</sitemapindex>";
	
	private static final String EXAMPLE = "http://www.example.com/";
	private static final W3CDateFormat ZULU = new W3CDateFormat();
	File outFile;
	SitemapIndexGenerator sig;
	
	@BeforeEach
	public void setUp() throws Exception {
		ZULU.setTimeZone(W3CDateFormat.ZULU);
		outFile = File.createTempFile(SitemapGeneratorTest.class.getSimpleName(), ".xml");
		outFile.deleteOnExit();
	}
	
	@AfterEach
	public void tearDown() {
		sig = null;
		outFile.delete();
		outFile = null;
	}

	@Test
	void testTooManyUrls() throws Exception {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).maxUrls(10).autoValidate(true).build();
		for (int i = 0; i < 9; i++) {
			sig.addUrl(EXAMPLE+i);
		}
		sig.addUrl(EXAMPLE+"9");
		assertThrows(RuntimeException.class, () -> sig.addUrl("http://www.example.com/just-one-more"), "too many URLs allowed");
	}
	@Test
	void testNoUrls() throws Exception {
		sig = new SitemapIndexGenerator(EXAMPLE, outFile);
		assertThrows(RuntimeException.class, () -> sig.write(), "Allowed write with no URLs");
	}
	
	@Test
	void testNoUrlsEmptyIndexAllowed() throws Exception {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).allowEmptyIndex(true).build();
		sig.write();
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
				"</sitemapindex>";
		String actual = TestUtil.slurpFileAndDelete(outFile);
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testMaxUrls() throws Exception {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).autoValidate(true)
			.maxUrls(10).defaultLastMod(new Date(0)).dateFormat(ZULU).build();
		for (int i = 1; i <= 9; i++) {
			sig.addUrl(EXAMPLE+"sitemap"+i+".xml");
		}
		sig.addUrl(EXAMPLE+"sitemap10.xml");
		sig.write();
		String actual = TestUtil.slurpFileAndDelete(outFile);
		assertEquals(INDEX, actual);
		assertEquals(INDEX, sig.writeAsString());
	}
	
	@Test
	void testOneUrl() throws Exception {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).dateFormat(ZULU).autoValidate(true).build();
		SitemapIndexUrl url = new SitemapIndexUrl(EXAMPLE+"index.html", new Date(0));
		sig.addUrl(url);
		sig.write();
		String actual = TestUtil.slurpFileAndDelete(outFile);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" + 
				"  <sitemap>\n" + 
				"    <loc>http://www.example.com/index.html</loc>\n" + 
				"    <lastmod>1970-01-01</lastmod>\n" + 
				"  </sitemap>\n" + 
				"</sitemapindex>";
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testAddByPrefix() throws MalformedURLException {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).autoValidate(true)
			.defaultLastMod(new Date(0)).dateFormat(ZULU).build();
		sig.addUrls("sitemap", ".xml", 10);
		sig.write();
		String actual = TestUtil.slurpFileAndDelete(outFile);
		assertEquals(INDEX, actual);
		assertEquals(INDEX, sig.writeAsString());
	}
	
}
