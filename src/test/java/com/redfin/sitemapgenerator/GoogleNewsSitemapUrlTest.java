package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoogleNewsSitemapUrlTest {
	
	File dir;
	GoogleNewsSitemapGenerator wsg;
	
	@BeforeEach
	public void setUp() throws Exception {
		dir = File.createTempFile(GoogleNewsSitemapUrlTest.class.getSimpleName(), "");
		dir.delete();
		dir.mkdir();
		dir.deleteOnExit();
	}
	
	@AfterEach
	public void tearDown() {
		wsg = null;
		for (File file : dir.listFiles()) {
			file.deleteOnExit();
			file.delete();
		}
		dir.delete();
		dir = null;
	}
	
	@Test
	void testSimpleUrl() throws Exception {
		wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en");
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" + 
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"    </news:news>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	@Test
	void testKeywords() throws Exception {
		wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en")
			.keywords("Klaatu", "Barrata", "Nicto")
			.build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" + 
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"      <news:keywords>Klaatu, Barrata, Nicto</news:keywords>\n" +
			"    </news:news>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}

	@Test
	void testGenres() throws Exception {
		wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en")
			.genres("persbericht")
			.build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" +
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" +
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:genres>persbericht</news:genres>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"    </news:news>\n" +
			"  </url>\n" +
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	private String writeSingleSiteMap(GoogleNewsSitemapGenerator wsg) {
		List<File> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
		return TestUtil.slurpFileAndDelete(files.get(0));
	}
}
