package com.redfin.sitemapgenerator;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Builds a sitemap index, which points only to other sitemaps.
 * @author Dan Fabulich
 *
 */
public class SitemapIndexGenerator {
	private final URL baseUrl;	
	private final File outFile;
	private final boolean allowEmptyIndex;
	private final List<SitemapIndexUrl> urls = new ArrayList<>();
	private final int maxUrls;
	private final W3CDateFormat dateFormat;
	private final Date defaultLastMod;
	private final boolean autoValidate;
	
	/** Options to configure sitemap index generation */
	public static class Options {
		private final URL baseUrl;
		private final File outFile;
		private W3CDateFormat dateFormat = null;
		private boolean allowEmptyIndex = false;
		private int maxUrls = SitemapConstants.MAX_SITEMAPS_PER_INDEX;
		private Date defaultLastMod = new Date();
		private boolean autoValidate = false;
		// TODO GZIP?  Is that legal for a sitemap index?

		/**Configures the generator with a base URL and destination to write the sitemap index file.
		 * 
		 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
		 * @param outFile The sitemap index will be written out at this location
		 */
		public Options(URL baseUrl, File outFile) {
			this.baseUrl = baseUrl;
			this.outFile = outFile;
		}
		/**Configures the generator with a base URL and destination to write the sitemap index file.
		 * 
		 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
		 * @param outFile The sitemap index will be written out at this location
		 */
		public Options(String baseUrl, File outFile) throws MalformedURLException {
			this(new URL(baseUrl), outFile);
		}
		/** The date formatter, typically configured with a {@link W3CDateFormat.Pattern} and/or a time zone */
		public Options dateFormat(W3CDateFormat dateFormat) {
			this.dateFormat = dateFormat;
			return this;
		}

		/**
		 * Permit writing an index that contains no URLs.
		 *
		 * @param allowEmptyIndex {@code true} if an empty index is permissible
		 * @return this instance, for chaining
		 */
		public Options allowEmptyIndex(boolean allowEmptyIndex) {
			this.allowEmptyIndex = allowEmptyIndex;
			return this;
		}

		/**
		 * The maximum number of sitemaps to allow per sitemap index; the default is the
		 * maximum allowed (see {@link SitemapConstants#MAX_SITEMAPS_PER_INDEX}), but
		 * you can decrease it if you wish (for testing)
		 */
		Options maxUrls(int maxUrls) {
			if (maxUrls > SitemapConstants.MAX_SITEMAPS_PER_INDEX) {
				throw new SitemapException(String.format("You can't have more than %d sitemaps per index", SitemapConstants.MAX_SITEMAPS_PER_INDEX));
			}
			this.maxUrls = maxUrls;
			return this;
		}
		/**
		 * The default lastMod date for sitemap indexes; the default is
		 * now, but you can pass in null to omit a lastMod entirely. We don't
		 * recommend this; Google may not like you as much.
		 */
		public Options defaultLastMod(Date defaultLastMod) {
			this.defaultLastMod = defaultLastMod;
			return this;
		}
		
		/**
		 * Validate the sitemap index automatically after writing it; this takes
		 * time
		 */
		public Options autoValidate(boolean autoValidate) {
			this.autoValidate = autoValidate;
			return this;
		}
		
		/** Constructs a sitemap index generator configured with the options you specified */
		public SitemapIndexGenerator build() {
			return new SitemapIndexGenerator(this);
		}
	}

	/**Configures the generator with a base URL and destination to write the sitemap index file.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param outFile The sitemap index will be written out at this location
	 */
	public SitemapIndexGenerator(URL baseUrl, File outFile) {
		this(new Options(baseUrl, outFile));
	}
	
	/**Configures the generator with a base URL and destination to write the sitemap index file.
	 * 
	 * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
	 * @param outFile The sitemap index will be written out at this location
	 */
	public SitemapIndexGenerator(String baseUrl, File outFile) throws MalformedURLException {
		this(new Options(baseUrl, outFile));
	}
	
	private SitemapIndexGenerator(Options options) {
		this.baseUrl = options.baseUrl;		
		this.outFile = options.outFile;
		this.allowEmptyIndex = options.allowEmptyIndex;
		this.maxUrls = options.maxUrls;
		W3CDateFormat dateFormat = options.dateFormat;
		if (dateFormat == null) dateFormat = new W3CDateFormat();
		this.dateFormat = dateFormat;
		this.defaultLastMod = options.defaultLastMod;
		this.autoValidate = options.autoValidate;
	}
	
	/** Adds a single sitemap to the index */
	public SitemapIndexGenerator addUrl(SitemapIndexUrl url) { 
		UrlUtils.checkUrl(url.url, baseUrl);
		if (urls.size() >= maxUrls) {
			throw new SitemapException("More than " + maxUrls + " urls");
		}
		urls.add(url);
		return this;
	}
	
	/** Add multiple sitemaps to the index */
	public SitemapIndexGenerator addUrls(Iterable<? extends SitemapIndexUrl> urls) {
		for (SitemapIndexUrl url : urls) addUrl(url);
		return this;
	}
	
	/** Add multiple sitemaps to the index */
	public SitemapIndexGenerator addUrls(SitemapIndexUrl... urls) {
		for (SitemapIndexUrl url : urls) addUrl(url);
		return this;
	}
	
	/** Add multiple sitemaps to the index */
	public SitemapIndexGenerator addUrls(String... urls) throws MalformedURLException {
		for (String url : urls) addUrl(url);
		return this;
	}
	
	/** Adds a single sitemap to the index */
	public SitemapIndexGenerator addUrl(String url) throws MalformedURLException {
		return addUrl(new SitemapIndexUrl(url));
	}
	
	/** Add multiple sitemaps to the index */
	public SitemapIndexGenerator addUrls(URL... urls) {
		for (URL url : urls) addUrl(url);
		return this;
	}
	
	/** Adds a single sitemap to the index */
	public SitemapIndexGenerator addUrl(URL url) {
		return addUrl(new SitemapIndexUrl(url));
	}
	
	/** Adds a single sitemap to the index */
	public SitemapIndexGenerator addUrl(URL url, Date lastMod) {
		return addUrl(new SitemapIndexUrl(url, lastMod));
	}
	
	/** Adds a single sitemap to the index */
	public SitemapIndexGenerator addUrl(String url, Date lastMod) throws MalformedURLException {
		return addUrl(new SitemapIndexUrl(url, lastMod));
	}
	
	/** Add a numbered list of sitemaps to the index, e.g. "sitemap1.xml" "sitemap2.xml" "sitemap3.xml" etc.
	 * 
	 * @param prefix the first part of the filename e.g. "sitemap"
	 * @param suffix the last part of the filename e.g. ".xml" or ".xml.gz"
	 * @param count the number of sitemaps (1-based)
	 */
	public SitemapIndexGenerator addUrls(String prefix, String suffix, int count) {
		if (count == 0) {
			try {
				addUrl(new URL(baseUrl, prefix + suffix));
			} catch (MalformedURLException e) {
				throw new SitemapException(e);
			}
		} else {
			for (int i = 1; i <= count; i++) {
				String fileName = prefix + i + suffix;
				try {
					addUrl(new URL(baseUrl, fileName));
				} catch (MalformedURLException e) {
					throw new SitemapException(e);
				}
			}
		}
		return this;
	}
	
	/** Writes out the sitemap index */
	public void write() {
		try {
			// TODO gzip? is that legal for a sitemap index?
			write(new FileWriter(outFile));
		} catch (IOException e) {
			throw new SitemapException("Problem writing sitemap index file " + outFile, e);
		}
	}

	private void write(OutputStreamWriter out) {
		if (!allowEmptyIndex && urls.isEmpty()) throw new SitemapException("No URLs added, sitemap index would be empty; you must add some URLs with addUrls");
		// Auto-close the stream after use
		try (out) {
			writeSiteMap(out);
			out.flush();
			if (autoValidate) SitemapValidator.validateSitemapIndex(outFile);
		} catch (SAXException e) {
			throw new SitemapException("Problem validating sitemap index file (bug?)", e);
		} catch (IOException ex) {
			throw new SitemapException("Error writing to output", ex);
		}

	}
	
	public String writeAsString() {
		StringBuilder sb = new StringBuilder();
		writeAsString(sb);
		return sb.toString();
	}

	private void writeAsString(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); 
		sb.append(String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI));
		for (SitemapIndexUrl url : urls) {
			sb.append("  <sitemap>\n");
			sb.append("    <loc>");
			sb.append(UrlUtils.escapeXml(url.url.toString()));
			sb.append("</loc>\n");
			Date lastMod = url.lastMod;
			
			if (lastMod == null) lastMod = defaultLastMod;
			
			if (lastMod != null) {
				sb.append("    <lastmod>");
				sb.append(dateFormat.format(lastMod));
				sb.append("</lastmod>\n");
			}
			sb.append("  </sitemap>\n");
		}
		sb.append("</sitemapindex>");
	}

	private void writeSiteMap(OutputStreamWriter out) throws IOException {
		StringBuilder sb = new StringBuilder();
		writeAsString(sb);
		out.write(sb.toString());
	}

}
