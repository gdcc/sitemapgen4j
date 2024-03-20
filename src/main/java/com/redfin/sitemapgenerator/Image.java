package com.redfin.sitemapgenerator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represent a single image and image properties for use in extended sitemaps
 * @see <a href="https://support.google.com/webmasters/answer/178636">Image sitemaps</a>
 */
public class Image {
    private final URL url;
    private final String title;
    private final String caption;
    private final String geoLocation;
    private final URL license;

    public Image(String url) throws MalformedURLException {
        this(new URL(url));
    }

    public Image(URL url) {
        this.url = url;
        this.title = null;
        this.caption = null;
        this.geoLocation = null;
        this.license = null;
    }

    public Image(URL url, String title, String caption, String geoLocation, String license) throws MalformedURLException {
        this(url, title, caption, geoLocation, new URL(license));
    }

    public Image(URL url, String title, String caption, String geoLocation, URL license) {
        this.url = url;
        this.title = title;
        this.caption = caption;
        this.geoLocation = geoLocation;
        this.license = license;
    }


    /** Retrieves URL of Image*/
    public URL getUrl() { return url; }

    /** Retrieves title of image
     * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
     */
    @Deprecated(since = "2022-05-06")
    public String getTitle() { return title; }

    /** Retrieves caption of image
     * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
     */
    @Deprecated(since = "2022-05-06")
    public String getCaption() { return caption; }

    /** Retrieves geolocation string of image
     * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
     */
    @Deprecated(since = "2022-05-06")
    public String getGeoLocation() { return geoLocation; }

    /** Retrieves license string of image
     * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
     */
    @Deprecated(since = "2022-05-06")
    public URL getLicense() { return license; }

    public static class ImageBuilder {
        private final URL url;
        private String title;
        private String caption;
        private String geoLocation;
        private URL license;

        public ImageBuilder(String url) throws MalformedURLException {
            this(new URL(url));
        }

        public ImageBuilder(URL url) {
            this.url = url;
        }
        
        /**
         * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
         */
        @Deprecated(since = "2022-05-06")
        public ImageBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        /**
         * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
         */
        @Deprecated(since = "2022-05-06")
        public ImageBuilder caption(String caption) {
            this.caption = caption;
            return this;
        }
        
        /**
         * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
         */
        @Deprecated(since = "2022-05-06")
        public ImageBuilder geoLocation(String geoLocation) {
            this.geoLocation = geoLocation;
            return this;
        }
        
        /**
         * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
         */
        @Deprecated(since = "2022-05-06")
        public ImageBuilder license(String license) throws MalformedURLException {
            return license(new URL(license));
        }
        
        /**
         * @deprecated Since May 2022, see <a href="https://developers.google.com/search/blog/2022/05/spring-cleaning-sitemap-extensions">deprecation note</a>. Use IPTC metadata instead.
         */
        @Deprecated(since = "2022-05-06")
        public ImageBuilder license(URL license) {
            this.license = license;
            return this;
        }

        public Image build() {
            return new Image(url, title, caption, geoLocation, license);
        }
    }
}
