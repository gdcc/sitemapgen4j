package com.redfin.sitemapgenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** One configurable Google Image Search URL.  To configure, use {@link Options}
 *
 * @see Options
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/image-sitemaps">Google Developer: Image Sitemaps</a>
 */
public class GoogleImageSitemapUrl extends WebSitemapUrl {

    private final List<Image> images;
    private static final String ERROR_MORE_1000_IMG = "A URL cannot have more than 1000 image tags";

    public GoogleImageSitemapUrl(String url) throws MalformedURLException {
        this(new Options(url));
    }

    public GoogleImageSitemapUrl(URL url) {
        this(new Options(url));
    }

    public GoogleImageSitemapUrl(Options options) {
        super(options);
        this.images = options.images;
    }

    public void addImage(Image image) {
        this.images.add(image);
        if(this.images.size() > 1000) {
            throw new SitemapException(ERROR_MORE_1000_IMG);
        }
    }

    /** Options to configure Google Extension URLs */
    public static class Options extends AbstractSitemapUrlOptions<GoogleImageSitemapUrl, GoogleImageSitemapUrl.Options> {
        private List<Image> images;


        public Options(URL url) {
            super(url, GoogleImageSitemapUrl.class);
            images = new ArrayList<>();
        }

        public Options(String url) throws MalformedURLException {
            super(url, GoogleImageSitemapUrl.class);
            images = new ArrayList<>();
        }

        public Options images(List<Image> images) {
            if(images != null && images.size() > 1000) {
                throw new SitemapException(ERROR_MORE_1000_IMG);
            }
            this.images = images;
            return this;
        }

        public Options images(Image...images) {
            if(images.length > 1000) {
                throw new SitemapException(ERROR_MORE_1000_IMG);
            }
            return images(Arrays.asList(images));

        }
    }

    /**Retrieves list of images*/
    public List<Image> getImages() {
        return this.images;
    }
}
