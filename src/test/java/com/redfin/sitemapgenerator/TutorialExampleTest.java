package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.OffsetDateTime;

public class TutorialExampleTest {
	
	File myDir;
	File myFile;
	
	@BeforeEach
	public void setUp() throws Exception {
		myDir = File.createTempFile(TutorialExampleTest.class.getSimpleName(), "");
		myDir.delete();
		myDir.mkdir();
		myDir.deleteOnExit();
		myFile = new File(myDir, "sitemap_index.xml");
	}
	
	@AfterEach
	public void tearDown() {
		for (File file : myDir.listFiles()) {
			file.deleteOnExit();
			file.delete();
		}
		myDir.delete();
		myDir = null;
	}
	
	@Test
	void testGettingStarted() throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
		wsg.addUrl("https://www.example.com/index.html"); // repeat multiple times
		wsg.write();
	}
	
	@Test
	void testConfiguringWsgOptions() throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
			.gzip(true).build(); // enable gzipped output
		wsg.addUrl("https://www.example.com/index.html");
		wsg.write();
	}
	
	@Test
	void testConfiguringUrlOptions() throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
		WebSitemapUrl url = new WebSitemapUrl.Options("https://www.example.com/index.html")
			.lastMod(OffsetDateTime.now()).priority(1.0).changeFreq(ChangeFreq.HOURLY).build();
		// this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly 
		wsg.addUrl(url);
		wsg.write();
	}
	
	@Test
	void testConfiguringDateFormat() throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
			.dateFormat(W3CDateFormat.DAY).build(); // actually use the configured dateFormat
		wsg.addUrl("https://www.example.com/index.html");
		wsg.write();
	}
	
	@Test
	void testLotsOfUrlsWsg() throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
		for (int i = 0; i < 60000; i++) wsg.addUrl("https://www.example.com/index.html");
		wsg.write();
		wsg.writeSitemapsWithIndex(); // generate the sitemap_index.xml
	}
	
	@Test
	void testLotsOfUrlsSig() throws Exception {
		WebSitemapGenerator wsg;
		// generate foo sitemap
		wsg = WebSitemapGenerator.builder("https://www.example.com", myDir).fileNamePrefix("foo").build();
		for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/foo"+i+".html");
		wsg.write();
		// generate bar sitemap
		wsg = WebSitemapGenerator.builder("https://www.example.com", myDir).fileNamePrefix("bar").build();
		for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/bar"+i+".html");
		wsg.write();
		// generate sitemap index for foo + bar 
		SitemapIndexGenerator sig = new SitemapIndexGenerator("https://www.example.com", myFile);
		sig.addUrl("https://www.example.com/foo.html");
		sig.addUrl("https://www.example.com/bar.html");
		sig.write();
	}
	
	@Test
	void testAutoValidate() throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
			.autoValidate(true).build(); // validate the sitemap after writing
		wsg.addUrl("https://www.example.com/index.html");
		wsg.write();
	} 
}
