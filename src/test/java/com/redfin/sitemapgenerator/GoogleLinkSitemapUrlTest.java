package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoogleLinkSitemapUrlTest {

	File dir;
	GoogleLinkSitemapGenerator wsg;

	@BeforeEach
	public void setUp() throws Exception {

		dir = File.createTempFile(GoogleLinkSitemapUrlTest.class.getSimpleName(), "");
		dir.delete();
		dir.mkdir();
		dir.deleteOnExit();
	}

	@AfterEach
	public void tearDown() {

		wsg = null;
		for (final File file : dir.listFiles()) {
			file.deleteOnExit();
			file.delete();
		}
		dir.delete();
		dir = null;
	}

	@Test
	void testSimpleUrlWithHrefLang() throws Exception {

		wsg = new GoogleLinkSitemapGenerator("https://www.example.com", dir);
		final Map<String, Map<String, String>> alternates = new LinkedHashMap<String, Map<String, String>>();
		alternates.put("https://www.example/en/index.html", Collections.singletonMap("hreflang", "en-GB"));
		alternates.put("https://www.example/fr/index.html", Collections.singletonMap("hreflang", "fr-FR"));
		alternates.put("https://www.example/es/index.html", Collections.singletonMap("hreflang", "es-ES"));

		final GoogleLinkSitemapUrl url = new GoogleLinkSitemapUrl("https://www.example.com/index.html", alternates);
		wsg.addUrl(url);
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ String.format("<urlset xmlns=\"%s\" xmlns:xhtml=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_LINK_NS_URI)
			+ "  <url>\n"
			+ "    <loc>https://www.example.com/index.html</loc>\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      hreflang=\"en-GB\"\n"
			+ "      href=\"https://www.example/en/index.html\"\n"
			+ "    />\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      hreflang=\"fr-FR\"\n"
			+ "      href=\"https://www.example/fr/index.html\"\n"
			+ "    />\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      hreflang=\"es-ES\"\n"
			+ "      href=\"https://www.example/es/index.html\"\n"
			+ "    />\n"
			+ "  </url>\n"
			+ "</urlset>";
		final String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}

	@Test
	void testSimpleUrlWithMedia() throws Exception {

		wsg = new GoogleLinkSitemapGenerator("https://www.example.com", dir);
		final Map<String, Map<String, String>> alternates = new LinkedHashMap<String, Map<String, String>>();
		alternates.put("https://www.example/en/index.html", Collections.singletonMap("media", "only screen and (max-width: 640px)"));
		alternates.put("https://www.example/fr/index.html", Collections.singletonMap("media", "only screen and (max-width: 640px)"));
		alternates.put("https://www.example/es/index.html", Collections.singletonMap("media", "only screen and (max-width: 640px)"));

		final GoogleLinkSitemapUrl url = new GoogleLinkSitemapUrl("https://www.example.com/index.html", alternates);
		wsg.addUrl(url);
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ String.format("<urlset xmlns=\"%s\" xmlns:xhtml=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_LINK_NS_URI)
			+ "  <url>\n"
			+ "    <loc>https://www.example.com/index.html</loc>\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      media=\"only screen and (max-width: 640px)\"\n"
			+ "      href=\"https://www.example/en/index.html\"\n"
			+ "    />\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      media=\"only screen and (max-width: 640px)\"\n"
			+ "      href=\"https://www.example/fr/index.html\"\n"
			+ "    />\n"
			+ "    <xhtml:link\n"
			+ "      rel=\"alternate\"\n"
			+ "      media=\"only screen and (max-width: 640px)\"\n"
			+ "      href=\"https://www.example/es/index.html\"\n"
			+ "    />\n"
			+ "  </url>\n"
			+ "</urlset>";
		final String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}

	private String writeSingleSiteMap(final GoogleLinkSitemapGenerator wsg) {

		final List<File> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
		return TestUtil.slurpFileAndDelete(files.get(0));
	}
}
