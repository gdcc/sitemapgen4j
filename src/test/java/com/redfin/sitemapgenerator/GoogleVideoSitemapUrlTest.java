package com.redfin.sitemapgenerator;

import com.redfin.sitemapgenerator.GoogleVideoSitemapUrl.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoogleVideoSitemapUrlTest {
	
	private static final URL LANDING_URL = newURL("https://www.example.com/index.html");
	private static final URL CONTENT_URL = newURL("https://www.example.com/index.flv");
	File dir;
	GoogleVideoSitemapGenerator wsg;
	
	private static URL newURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {}
		return null;
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		dir = File.createTempFile(GoogleVideoSitemapUrlTest.class.getSimpleName(), "");
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
		wsg = new GoogleVideoSitemapGenerator("https://www.example.com", dir);
		GoogleVideoSitemapUrl url = new GoogleVideoSitemapUrl(LANDING_URL, CONTENT_URL);
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_VIDEO_NS, SitemapConstants.GOOGLE_VIDEO_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <video:video>\n" + 
			"      <video:content_loc>https://www.example.com/index.flv</video:content_loc>\n" +
			"    </video:video>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}

	@Test
	void testOptions() throws Exception {
		wsg = GoogleVideoSitemapGenerator.builder("https://www.example.com", dir)
			.dateFormat(W3CDateFormat.DAY).build();
		GoogleVideoSitemapUrl url = new Options(LANDING_URL, CONTENT_URL)
			.playerUrl(new URL("https://www.example.com/index.swf"), true)
			.thumbnailUrl(new URL("https://www.example.com/thumbnail.jpg"))
			.title("This is a video!").description("A great video about dinosaurs")
			.rating(5.0).viewCount(500000).publicationDate(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)).tags("dinosaurs", "example", "awesome")
			.category("example").familyFriendly(false).durationInSeconds(60*30)
			.build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_VIDEO_NS, SitemapConstants.GOOGLE_VIDEO_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <video:video>\n" + 
			"      <video:content_loc>https://www.example.com/index.flv</video:content_loc>\n" +
			"      <video:player_loc allow_embed=\"Yes\">https://www.example.com/index.swf</video:player_loc>\n" +
			"      <video:thumbnail_loc>https://www.example.com/thumbnail.jpg</video:thumbnail_loc>\n" +
			"      <video:title>This is a video!</video:title>\n" + 
			"      <video:description>A great video about dinosaurs</video:description>\n" + 
			"      <video:rating>5.0</video:rating>\n" + 
			"      <video:view_count>500000</video:view_count>\n" + 
			"      <video:publication_date>1970-01-01</video:publication_date>\n" + 
			"      <video:tag>dinosaurs</video:tag>\n" + 
			"      <video:tag>example</video:tag>\n" + 
			"      <video:tag>awesome</video:tag>\n" + 
			"      <video:category>example</video:category>\n" + 
			"      <video:family_friendly>No</video:family_friendly>\n" + 
			"      <video:duration>1800</video:duration>\n" + 
			"    </video:video>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	@Test
	void testLongTitle() {
		var sut = new Options(LANDING_URL, CONTENT_URL);
		assertThrows(RuntimeException.class, () -> sut.title(
			"Unfortunately, this title is far longer than 100 characters" +
			" by virtue of having a great deal to say but not much content."),
			"Long title inappropriately allowed");
	}
	
	@Test
	void testLongDescription() {
        String description = "x".repeat(2049);
		
		var sut = new Options(LANDING_URL, CONTENT_URL);
		assertThrows(RuntimeException.class, () -> sut.description(description),
			"Long description inappropriately allowed");
	}
	
	@Test
	void testWrongRating() {
		Options o = new Options(LANDING_URL, CONTENT_URL);
		assertThrows(RuntimeException.class, () -> o.rating(-1.0), "Negative rating allowed");
		assertThrows(RuntimeException.class, () -> o.rating(10.0),">5 rating allowed");
	}
	
	@Test
	void testTooManyTags() {
		int maxTags = 32;
		String[] tags = new String[maxTags+1];
		for (int i = 0; i < maxTags+1; i++) {
			tags[i] = "tag" + i;
		}
		
		var sut = new Options(LANDING_URL, CONTENT_URL).tags(tags);
		assertThrows(RuntimeException.class, sut::build,"Too many tags allowed");
	}
	
	@Test
	void testLongCategory() {
        String category = "x".repeat(257);
		
		var sut = new Options(LANDING_URL, CONTENT_URL);
		assertThrows(RuntimeException.class, () -> sut.category(category), "Long category inappropriately allowed");
	}
	
	@Test
	void testWrongDuration() {
		Options o = new Options(LANDING_URL, CONTENT_URL);
		assertThrows(RuntimeException.class, () -> o.durationInSeconds(-1), "Negative duration allowed");
		assertThrows(RuntimeException.class, () -> o.durationInSeconds(Integer.MAX_VALUE), ">8hr duration allowed");
	}
	
	private String writeSingleSiteMap(GoogleVideoSitemapGenerator wsg) {
		List<File> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
		return TestUtil.slurpFileAndDelete(files.get(0));
	}
}
