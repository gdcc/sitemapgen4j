package com.redfin.sitemapgenerator;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

abstract class SitemapGenerator<U extends ISitemapUrl, T extends SitemapGenerator<U,T>> {
	private final URL baseUrl;
	private final File baseDir;
	private final String fileNamePrefix;
	private final String fileNameSuffix;
	private final boolean allowEmptySitemap;
	private final boolean allowMultipleSitemaps;
	private final List<U> urls = new ArrayList<>();
	private final W3CDateFormat dateFormat;
	private final int maxUrls;
	private final boolean autoValidate;
	private final boolean gzip;
	private final ISitemapUrlRenderer<U> renderer;
	private int mapCount = 0;
	private boolean finished = false;
	
	private final List<File> outFiles = new ArrayList<>();
	
	public SitemapGenerator(AbstractSitemapGeneratorOptions<?> options, ISitemapUrlRenderer<U> renderer) {
		baseDir = options.baseDir;
		baseUrl = options.baseUrl;
		fileNamePrefix = options.fileNamePrefix;
		W3CDateFormat dateFormatter = options.dateFormat;
		if (dateFormatter == null) dateFormatter = W3CDateFormat.AUTO;
		this.dateFormat = dateFormatter;
		allowEmptySitemap = options.allowEmptySitemap;
		allowMultipleSitemaps = options.allowMultipleSitemaps;
		maxUrls = options.maxUrls;
		autoValidate = options.autoValidate;
		gzip = options.gzip;
		this.renderer = renderer;

		if(options.suffixStringPattern != null && !options.suffixStringPattern.isEmpty()) {
			fileNameSuffix = gzip ? options.suffixStringPattern + ".xml.gz" : options.suffixStringPattern + ".xml";
		}
		else {
			fileNameSuffix = gzip ? ".xml.gz" : ".xml";
		}
	}

	/** Add one URL of the appropriate type to this sitemap.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or else write out one sitemap immediately.
	 * @param url the URL to add to this sitemap
	 * @return this
	 */
	public T addUrl(U url) {
		if (finished) throw new SitemapException("Sitemap already printed; you must create a new generator to make more sitemaps");
		UrlUtils.checkUrl(url.getUrl(), baseUrl);
		if (urls.size() == maxUrls) {
			if (!allowMultipleSitemaps) throw new SitemapException("More than " + maxUrls + " urls, but allowMultipleSitemaps is false.  Enable allowMultipleSitemaps to split the sitemap into multiple files with a sitemap index.");
			if (baseDir != null) {
				if (mapCount == 0) mapCount++;
				writeSiteMap();
				mapCount++;
				urls.clear();
			}
		}
		urls.add(url);
		return getThis();
	}
	
	/** Add multiple URLs of the appropriate type to this sitemap, one at a time.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or write out one sitemap immediately.
	 * @param urls the URLs to add to this sitemap
	 * @return this
	 */
	public T addUrls(Iterable<? extends U> urls) {
		for (U url : urls) addUrl(url);
		return getThis();
	}
	
	/** Add multiple URLs of the appropriate type to this sitemap, one at a time.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or write out one sitemap immediately.
	 * @param urls the URLs to add to this sitemap
	 * @return this
	 */
	public T addUrls(U... urls) {
		for (U url : urls) addUrl(url);
		return getThis();
	}
	
	/** Add multiple URLs of the appropriate type to this sitemap, one at a time.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or write out one sitemap immediately.
	 * @param urls the URLs to add to this sitemap
	 * @return this
	 */
	public T addUrls(String... urls) {
		for (String url : urls) addUrl(url);
		return getThis();
	}
	
	/** Add one URL of the appropriate type to this sitemap.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or else write out one sitemap immediately.
	 * @param url the URL to add to this sitemap
	 * @return this
	 */
	public T addUrl(String url) {
		U sitemapUrl;
		try {
			sitemapUrl = renderer.getUrlClass().getConstructor(String.class).newInstance(url);
			return addUrl(sitemapUrl);
		} catch (Exception e) {
			throw new SitemapException(e);
		}
	}
	
	/** Add multiple URLs of the appropriate type to this sitemap, one at a time.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or write out one sitemap immediately.
	 * @param urls the URLs to add to this sitemap
	 * @return this
	 */
	public T addUrls(URL... urls) {
		for (URL url : urls) addUrl(url);
		return getThis();
	}
	
	/** Add one URL of the appropriate type to this sitemap.
	 * If we have reached the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps} is false,
	 * or write out one sitemap immediately.
	 * @param url the URL to add to this sitemap
	 * @return this
	 */
	public T addUrl(URL url) {
		U sitemapUrl;
		try {
			sitemapUrl = renderer.getUrlClass().getConstructor(URL.class).newInstance(url);
			return addUrl(sitemapUrl);
		} catch (Exception e) {
			throw new SitemapException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	T getThis() {
		return (T)this;
	}
	
	/** Write out remaining URLs; this method can only be called once.  This is necessary so we can keep an accurate count for {@link #writeSitemapsWithIndex()}.
	 *
	 * @return a list of files we wrote out to disk
	 */
	public List<File> write() {
		if (finished) throw new SitemapException("Sitemap already printed; you must create a new generator to make more sitemaps");
		if (!allowEmptySitemap && urls.isEmpty() && mapCount == 0) throw new SitemapException("No URLs added, sitemap would be empty; you must add some URLs with addUrls");
		writeSiteMap();
		finished = true;
		return outFiles;
	}
	
	/**
	 * Writes out the sitemaps as a list of strings.
	 * Each string in the list is a formatted list of URLs.
	 * We return a list because the URLs may not all fit --
	 * google specifies a maximum of 50,000 URLs in one sitemap.
	 * @return a list of XML-formatted strings
	 */
	public List<String> writeAsStrings() {
		List<String> listOfSiteMapStrings = new ArrayList<>();
		for (int start = 0; start < urls.size(); start += maxUrls) {
			int end = start + maxUrls;
			if (end > urls.size()) {
				end = urls.size();
			}
			StringBuilder sb = new StringBuilder();
			writeSiteMapAsString(sb, urls.subList(start, end));
			listOfSiteMapStrings.add(sb.toString());
		}
		return listOfSiteMapStrings;
	}
	
	private void writeSiteMapAsString(StringBuilder sb, List<U> urls) {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append(String.format("<urlset xmlns=\"%s\" ", SitemapConstants.SITEMAP_NS_URI));
		if (renderer.getXmlNamespaces() != null) {
			sb.append(renderer.getXmlNamespaces());
			sb.append(' ');
		}
		sb.append(">\n");
		for (U url : urls) {
			renderer.render(url, sb, dateFormat);
		}
		sb.append("</urlset>");
	}
	
	/**
	 * After you've called {@link #write()}, call this to generate a sitemap index of all sitemaps you generated.
	 * The sitemap index is written to {baseDir}/sitemap_index.xml
	 */
	public File writeSitemapsWithIndex() {
		return writeSitemapsWithIndex(new File(baseDir, SitemapConstants.SITEMAP_INDEX_FILE));
	}

	/**
	 * After you've called {@link #write()}, call this to generate a sitemap index of all sitemaps you generated.
	 */
	public String writeSitemapsWithIndexAsString() {
		return prepareSitemapIndexGenerator(null).writeAsString();
	}

	/**
	 * After you've called {@link #write()}, call this to generate a sitemap index of all sitemaps you generated.
	 *
	 * @param outFile the destination file of the sitemap index.
	 */
	public File writeSitemapsWithIndex(File outFile) {
		prepareSitemapIndexGenerator(outFile).write();
		return outFile;
	}

	private SitemapIndexGenerator prepareSitemapIndexGenerator(File outFile) {
		if (!finished) throw new SitemapException("Sitemaps not generated yet; call write() first");
		SitemapIndexGenerator sig;
		sig = new SitemapIndexGenerator.Options(baseUrl, outFile).dateFormat(dateFormat).autoValidate(autoValidate).build();
		sig.addUrls(fileNamePrefix, fileNameSuffix, mapCount);
		return sig;
	}
	
	private void writeSiteMap() {
		if (baseDir == null) {
			throw new NullPointerException("To write to files, baseDir must not be null");
		}
		if (urls.isEmpty() && (mapCount > 0 || !allowEmptySitemap)) return;
		String fileNamePrefix;
		if (mapCount > 0) {
			fileNamePrefix = this.fileNamePrefix + mapCount;
		} else {
			fileNamePrefix = this.fileNamePrefix;
		}
		File outFile = new File(baseDir, fileNamePrefix+fileNameSuffix);
		outFiles.add(outFile);
		
		try (FileOutputStream fileStream = new FileOutputStream(outFile);
			OutputStreamWriter out =
				gzip ? new OutputStreamWriter(new GZIPOutputStream(fileStream), StandardCharsets.UTF_8.newEncoder())
					 : new OutputStreamWriter(fileStream, StandardCharsets.UTF_8.newEncoder())) {
			writeSiteMap(out);
			out.flush();
		} catch (IOException e) {
			throw new SitemapException("Problem writing sitemap file " + outFile, e);
		}
		
		try {
			if (autoValidate) SitemapValidator.validateWebSitemap(outFile);
		} catch (SAXException e) {
			throw new SitemapException("Sitemap file failed to validate (bug?)", e);
		}
	}
	
	private void writeSiteMap(OutputStreamWriter out) throws IOException {
		StringBuilder sb = new StringBuilder();
		writeSiteMapAsString(sb, urls);
		out.write(sb.toString());
	}
	
}
