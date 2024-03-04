package com.redfin.sitemapgenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds a sitemap for Google Video search.  To configure options, use {@link #builder(URL, File)}
 * @author Dan Fabulich
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/video-sitemaps">Google Developer: Video Sitemaps</a>
 */
public class GoogleVideoSitemapGenerator extends SitemapGenerator<GoogleVideoSitemapUrl,GoogleVideoSitemapGenerator> {

	/** Configures a builder so you can specify sitemap generator options
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @return a builder; call .build() on it to make a sitemap generator
	 */
	public static SitemapGeneratorBuilder<GoogleVideoSitemapGenerator> builder(URL baseUrl, File baseDir) {
		return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleVideoSitemapGenerator.class);
	}
	
	/** Configures a builder so you can specify sitemap generator options
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @return a builder; call .build() on it to make a sitemap generator
	 */
	public static SitemapGeneratorBuilder<GoogleVideoSitemapGenerator> builder(String baseUrl, File baseDir) throws MalformedURLException {
		return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleVideoSitemapGenerator.class);
	}

	GoogleVideoSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
		super(options, new Renderer());
	}
	
	/**Configures the generator with a base URL and directory to write the sitemap files.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @throws MalformedURLException In case the given baseUrl is invalid
	 */
	public GoogleVideoSitemapGenerator(String baseUrl, File baseDir)
			throws MalformedURLException {
		this(new SitemapGeneratorOptions(baseUrl, baseDir));
	}

	/**Configures the generator with a base URL and directory to write the sitemap files.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 */
	public GoogleVideoSitemapGenerator(URL baseUrl, File baseDir) {
		this(new SitemapGeneratorOptions(baseUrl, baseDir));
	}
	
	/**Configures the generator with a base URL and a null directory. The object constructed
	 * is not intended to be used to write to files. Rather, it is intended to be used to obtain
	 * XML-formatted strings that represent sitemaps.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 */
	public GoogleVideoSitemapGenerator(String baseUrl) throws MalformedURLException {
		this(new SitemapGeneratorOptions(new URL(baseUrl)));
	}
	
	/**Configures the generator with a base URL and a null directory. The object constructed
	 * is not intended to be used to write to files. Rather, it is intended to be used to obtain
	 * XML-formatted strings that represent sitemaps.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 */
	public GoogleVideoSitemapGenerator(URL baseUrl) {
		this(new SitemapGeneratorOptions(baseUrl));
	}

	private static class Renderer extends AbstractSitemapUrlRenderer<GoogleVideoSitemapUrl> implements ISitemapUrlRenderer<GoogleVideoSitemapUrl> {

		private static final String VIDEO_NS = "video";
		
		public Class<GoogleVideoSitemapUrl> getUrlClass() {
			return GoogleVideoSitemapUrl.class;
		}
		
		public String getXmlNamespaces() {
			return "xmlns:video=\"https://www.google.com/schemas/sitemap-video/1.1\"";
		}

		public void render(GoogleVideoSitemapUrl url, StringBuilder sb, W3CDateFormat dateFormat) {
			StringBuilder tagSb = new StringBuilder();
			tagSb.append("    <").append(VIDEO_NS).append(":video>\n");
			renderTag(tagSb, VIDEO_NS, "content_loc", url.getContentUrl());
			if (url.getPlayerUrl() != null) {
				tagSb.append("      <").append(VIDEO_NS).append(":player_loc allow_embed=\"").append(url.getAllowEmbed()).append("\">");
				tagSb.append(url.getPlayerUrl());
				tagSb.append("</").append(VIDEO_NS).append(":player_loc>\n");
			}
			renderTag(tagSb, VIDEO_NS, "thumbnail_loc", url.getThumbnailUrl());
			renderTag(tagSb, VIDEO_NS, "title", url.getTitle());
			renderTag(tagSb, VIDEO_NS, "description", url.getDescription());
			renderTag(tagSb, VIDEO_NS, "rating", url.getRating());
			renderTag(tagSb, VIDEO_NS, "view_count", url.getViewCount());
			if (url.getPublicationDate() != null) {
				renderTag(tagSb, VIDEO_NS, "publication_date", dateFormat.format(url.getPublicationDate()));
			}
			if (url.getTags() != null) {
				for (String tag : url.getTags()) {
					renderTag(tagSb, VIDEO_NS, "tag", tag);
				}
			}
			renderTag(tagSb, VIDEO_NS, "category", url.getCategory());
			renderTag(tagSb, VIDEO_NS, "family_friendly", url.getFamilyFriendly());
			renderTag(tagSb, VIDEO_NS, "duration", url.getDurationInSeconds());
			tagSb.append("    </").append(VIDEO_NS).append(":video>\n");
			super.render(url, sb, dateFormat, tagSb.toString());
		}
		
	}
	
}
