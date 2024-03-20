package com.redfin.sitemapgenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds a sitemap for Google News.  To configure options, use {@link #builder(URL, File)}
 * @author Dan Fabulich
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/news-sitemap">Google Developer: News Sitemap</a>
 */
public class GoogleNewsSitemapGenerator extends SitemapGenerator<GoogleNewsSitemapUrl,GoogleNewsSitemapGenerator> {
	
	/** Configures a builder so you can specify sitemap generator options
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @return a builder; call .build() on it to make a sitemap generator
	 */
	public static SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder(URL baseUrl, File baseDir) {
		SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder = 
			new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleNewsSitemapGenerator.class);
		builder.maxUrls = 1000;
		return builder;
	}
	
	/** Configures a builder so you can specify sitemap generator options
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @return a builder; call .build() on it to make a sitemap generator
	 */
	public static SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder(String baseUrl, File baseDir) throws MalformedURLException {
		SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder = 
			new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleNewsSitemapGenerator.class);
		builder.maxUrls = SitemapConstants.GOOGLE_NEWS_MAX_URLS_PER_SITEMAP;
		return builder;
	}
	
	GoogleNewsSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
		super(options, new Renderer());
		if (options.maxUrls > SitemapConstants.GOOGLE_NEWS_MAX_URLS_PER_SITEMAP) {
			throw new SitemapException(String.format("Google News sitemaps can have only %d URLs per sitemap: %d",
					SitemapConstants.GOOGLE_NEWS_MAX_URLS_PER_SITEMAP, options.maxUrls));
		}
	}

	/** Configures the generator with a base URL and directory to write the sitemap files.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 * @throws MalformedURLException In case the given baseUrl is invalid
	 */
	public GoogleNewsSitemapGenerator(String baseUrl, File baseDir)
			throws MalformedURLException {
		this(new SitemapGeneratorOptions(baseUrl, baseDir));
	}

	/** Configures the generator with a base URL and directory to write the sitemap files.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
	 */
	public GoogleNewsSitemapGenerator(URL baseUrl, File baseDir) {
		this(new SitemapGeneratorOptions(baseUrl, baseDir));
	}

	/**Configures the generator with a base URL and a null directory. The object constructed
	 * is not intended to be used to write to files. Rather, it is intended to be used to obtain
	 * XML-formatted strings that represent sitemaps.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 */
	public GoogleNewsSitemapGenerator(String baseUrl) throws MalformedURLException {
		this(new SitemapGeneratorOptions(new URL(baseUrl)));
	}
	
	/**Configures the generator with a base URL and a null directory. The object constructed
	 * is not intended to be used to write to files. Rather, it is intended to be used to obtain
	 * XML-formatted strings that represent sitemaps.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 */
	public GoogleNewsSitemapGenerator(URL baseUrl) {
		this(new SitemapGeneratorOptions(baseUrl));
	}
	
	private static class Renderer extends AbstractSitemapUrlRenderer<GoogleNewsSitemapUrl> implements ISitemapUrlRenderer<GoogleNewsSitemapUrl> {

		public Class<GoogleNewsSitemapUrl> getUrlClass() {
			return GoogleNewsSitemapUrl.class;
		}

		public String getXmlNamespaces() {
			return String.format("xmlns:%s=\"%s\"", SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI);
		}

		public void render(GoogleNewsSitemapUrl url, StringBuilder sb, W3CDateFormat dateFormat) {
			StringBuilder tagSb = new StringBuilder();
			tagSb.append("    <").append(SitemapConstants.GOOGLE_NEWS_NS).append(":news>\n");
			tagSb.append("      <").append(SitemapConstants.GOOGLE_NEWS_NS).append(":publication>\n");
			renderSubTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "name", url.getPublication().getName());
			renderSubTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "language", url.getPublication().getLanguage());
			tagSb.append("      </").append(SitemapConstants.GOOGLE_NEWS_NS).append(":publication>\n");
			renderTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "genres", url.getGenres());
			renderTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "publication_date", dateFormat.format(url.getPublicationDate()));
			renderTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "title", url.getTitle());
			renderTag(tagSb, SitemapConstants.GOOGLE_NEWS_NS, "keywords", url.getKeywords());
			tagSb.append("    </").append(SitemapConstants.GOOGLE_NEWS_NS).append(":news>\n");
			super.render(url, sb, dateFormat, tagSb.toString());
		}
		
	}

}
