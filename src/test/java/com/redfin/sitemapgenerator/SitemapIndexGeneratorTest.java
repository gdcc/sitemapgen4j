package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SitemapIndexGeneratorTest {

	private static final String INDEX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap1.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap2.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap3.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap4.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap5.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap6.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap7.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap8.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap9.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap10.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"</sitemapindex>";
	
	private static final String EXAMPLE = "https://www.example.com/";
	File outFile;
	SitemapIndexGenerator sig;
	
	@BeforeEach
	public void setUp() throws Exception {
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
		assertThrows(RuntimeException.class, () -> sig.addUrl("https://www.example.com/just-one-more"), "too many URLs allowed");
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
				String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
				"</sitemapindex>";
		String actual = TestUtil.slurpFileAndDelete(outFile);
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testMaxUrls() throws Exception {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).autoValidate(true)
			.maxUrls(10).defaultLastMod(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)).dateFormat(new W3CDateFormat()).build();
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
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).dateFormat(new W3CDateFormat()).autoValidate(true).build();
		SitemapIndexUrl url = new SitemapIndexUrl(EXAMPLE+"index.html", OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC));
		sig.addUrl(url);
		sig.write();
		String actual = TestUtil.slurpFileAndDelete(outFile);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
				"  <sitemap>\n" + 
				"    <loc>https://www.example.com/index.html</loc>\n" + 
				"    <lastmod>1970-01-01</lastmod>\n" + 
				"  </sitemap>\n" + 
				"</sitemapindex>";
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testAddByPrefix() throws MalformedURLException {
		sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).autoValidate(true)
			.defaultLastMod(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)).dateFormat(new W3CDateFormat()).build();
		sig.addUrls("sitemap", ".xml", 10);
		sig.write();
		String actual = TestUtil.slurpFileAndDelete(outFile);
		assertEquals(INDEX, actual);
		assertEquals(INDEX, sig.writeAsString());
	}
	
}
