package com.redfin.sitemapgenerator;

/**
 * Utility class to contain basic values as constants.
 **/
public final class SitemapConstants {

    /**
     * Private constructor to prevent instantiation.
     */
    private SitemapConstants() {
    }

    /**
     * Google Image sitemap namespace attribute.
     *
     * <p>See {@link GoogleImageSitemapGenerator} for more information.
     */
    public static final String GOOGLE_IMAGE_NS = "image";

    /**
     * Google Image sitemap namespace URI.
     *
     * <p>See {@link GoogleImageSitemapGenerator} for more information.
     */
    public static final String GOOGLE_IMAGE_NS_URI = "http://www.google.com/schemas/sitemap-image/1.1";

    /**
	 * @see <a href="https://developers.google.com/search/docs/specialty/international/localized-versions#sitemap">
     *      Google Developers: Sitemap for alternate pages
     */
    public static final String GOOGLE_LINK_NS_URI = "http://www.w3.org/1999/xhtml";

    /**
     * 1000 URLs max in a Google News sitemap file.
     *
     * <p>See {@link GoogleNewsSitemapGenerator} for more information.
     */
    public static final int GOOGLE_NEWS_MAX_URLS_PER_SITEMAP = 1000;

    /**
     * Google News sitemap namespace attribute.
     *
     * <p>See {@link GoogleNewsSitemapGenerator} for more information.
     */
    public static final String GOOGLE_NEWS_NS = "news";

    /**
     * Google News sitemap namespace URI.
     *
     * <p>See {@link GoogleNewsSitemapGenerator} for more information.
     */
    public static final String GOOGLE_NEWS_NS_URI = "http://www.google.com/schemas/sitemap-news/0.9";

    /**
     * Google Video sitemap namespace attribute.
     *
     * <p>See {@link GoogleVideoSitemapGenerator} for more information.
     */
    public static final String GOOGLE_VIDEO_NS = "video";

    /**
     * Google Video sitemap namespace URI.
     *
     * <p>See {@link GoogleVideoSitemapGenerator} for more information.
     */
    public static final String GOOGLE_VIDEO_NS_URI = "http://www.google.com/schemas/sitemap-video/1.1";

    /**
     * Maximum 50000 URLs per sitemap file.
     *
     * @see <a href="https://www.sitemaps.org/protocol.html">
     *      Sitemaps XML protocol</a>
     */
    public static final int MAX_URLS_PER_SITEMAP = 50000;

    /** Maximum 50000 sitemaps per index allowed.
     *
     * @see <a href="https://www.sitemaps.org/protocol.html#index">
     *      Sitemaps XML protocol</a>
     */
    public static final int MAX_SITEMAPS_PER_INDEX = 50000;

    /**
     * File name of sitemap index files, to group multiple sitemap files.
     *
     * @see <a href="https://www.sitemaps.org/protocol.html#index">
     *      Sitemaps XML protocol</a>
     */
    public static final String SITEMAP_INDEX_FILE = "sitemap_index.xml";

    /**
     * Sitemap namespace URI to use with <code>sitemap.xml</code> file.
     *
     * @see <a href="https://www.sitemaps.org/protocol.html">
     *      Sitemaps XML protocol</a>
     */
    public static final String SITEMAP_NS_URI = "http://www.sitemaps.org/schemas/sitemap/0.9";

}
