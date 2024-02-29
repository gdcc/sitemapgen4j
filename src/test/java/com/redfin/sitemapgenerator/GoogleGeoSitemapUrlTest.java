package com.redfin.sitemapgenerator;

import com.redfin.sitemapgenerator.GoogleGeoSitemapUrl.Format;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoogleGeoSitemapUrlTest {
	
	File dir;
	GoogleGeoSitemapGenerator wsg;
	
	@BeforeEach
	void setUp() throws Exception {
		dir = File.createTempFile(GoogleGeoSitemapUrlTest.class.getSimpleName(), "");
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
		wsg = new GoogleGeoSitemapGenerator("http://www.example.com", dir);
		GoogleGeoSitemapUrl url = new GoogleGeoSitemapUrl("http://www.example.com/index.html", Format.KML);
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
			"xmlns:geo=\"http://www.google.com/geo/schemas/sitemap/1.0\" >\n" + 
			"  <url>\n" + 
			"    <loc>http://www.example.com/index.html</loc>\n" +
			"    <geo:geo>\n" +
			"      <geo:format>kml</geo:format>\n" +
			"    </geo:geo>\n" +
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	private String writeSingleSiteMap(GoogleGeoSitemapGenerator wsg) {
		List<File> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
		return TestUtil.slurpFileAndDelete(files.get(0));
	}
}
