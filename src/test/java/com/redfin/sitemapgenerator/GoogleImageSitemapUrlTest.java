package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoogleImageSitemapUrlTest {

    private static final URL LANDING_URL = newURL("https://www.example.com/index.html");
    private static final URL CONTENT_URL = newURL("https://www.example.com/index.flv");
    File dir;
    GoogleImageSitemapGenerator wsg;

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
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl(LANDING_URL);
        url.addImage(new Image("https://cdn.example.com/image1.jpg"));
        url.addImage(new Image("https://cdn.example.com/image2.jpg"));
        wsg.addUrl(url);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
                        SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_IMAGE_NS,
                        SitemapConstants.GOOGLE_IMAGE_NS_URI) +
                "  <url>\n" +
                "    <loc>https://www.example.com/index.html</loc>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image1.jpg</image:loc>\n" +
                "    </image:image>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image2.jpg</image:loc>\n" +
                "    </image:image>\n" +
                "  </url>\n" +
                "</urlset>";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testBaseOptions() throws Exception {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl.Options(LANDING_URL)
                .images(new Image("https://cdn.example.com/image1.jpg"), new Image("https://cdn.example.com/image2.jpg"))
                .priority(0.5)
                .changeFreq(ChangeFreq.WEEKLY)
                .build();
        wsg.addUrl(url);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
                        SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_IMAGE_NS, SitemapConstants.GOOGLE_IMAGE_NS_URI) +
                "  <url>\n" +
                "    <loc>https://www.example.com/index.html</loc>\n" +
                "    <changefreq>weekly</changefreq>\n" +
                "    <priority>0.5</priority>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image1.jpg</image:loc>\n" +
                "    </image:image>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image2.jpg</image:loc>\n" +
                "    </image:image>\n" +
                "  </url>\n" +
                "</urlset>";

        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testImageOptions() throws Exception {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl.Options(LANDING_URL)
                .images(new Image.ImageBuilder("https://cdn.example.com/image1.jpg")
                        .title("image1.jpg")
                        .caption("An image of the number 1")
                        .geoLocation("Pyongyang, North Korea")
                        .license("https://cdn.example.com/licenses/imagelicense.txt")
                        .build(),
                        new Image.ImageBuilder("https://cdn.example.com/image2.jpg")
                                .title("image2.jpg")
                                .caption("An image of the number 2")
                                .geoLocation("Pyongyang, North Korea")
                                .license("https://cdn.example.com/licenses/imagelicense.txt")
                                .build())
                .priority(0.5)
                .changeFreq(ChangeFreq.WEEKLY)
                .build();
        wsg.addUrl(url);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
                        SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_IMAGE_NS, SitemapConstants.GOOGLE_IMAGE_NS_URI) +
                "  <url>\n" +
                "    <loc>https://www.example.com/index.html</loc>\n" +
                "    <changefreq>weekly</changefreq>\n" +
                "    <priority>0.5</priority>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image1.jpg</image:loc>\n" +
                "      <image:caption>An image of the number 1</image:caption>\n" +
                "      <image:title>image1.jpg</image:title>\n" +
                "      <image:geo_location>Pyongyang, North Korea</image:geo_location>\n" +
                "      <image:license>https://cdn.example.com/licenses/imagelicense.txt</image:license>\n" +
                "    </image:image>\n" +
                "    <image:image>\n" +
                "      <image:loc>https://cdn.example.com/image2.jpg</image:loc>\n" +
                "      <image:caption>An image of the number 2</image:caption>\n" +
                "      <image:title>image2.jpg</image:title>\n" +
                "      <image:geo_location>Pyongyang, North Korea</image:geo_location>\n" +
                "      <image:license>https://cdn.example.com/licenses/imagelicense.txt</image:license>\n" +
                "    </image:image>\n" +
                "  </url>\n" +
                "</urlset>";

        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testTooManyImages() throws Exception {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        List<Image> images = new ArrayList<Image>();
        for(int i = 0; i <= 1000; i++) {
            images.add(new Image("https://cdn.example.com/image" + i + ".jpg"));
        }
        
        var options = new GoogleImageSitemapUrl.Options(LANDING_URL);
        
        assertThrows(RuntimeException.class, () -> options.images(images), "Too many images allowed");
        assertDoesNotThrow(() -> options
            .priority(0.5)
            .changeFreq(ChangeFreq.WEEKLY)
            .build());
    }



    private String writeSingleSiteMap(GoogleImageSitemapGenerator wsg) {
        List<File> files = wsg.write();
        assertEquals( 1, files.size(), "Too many files: " + files.toString());
        assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
        return TestUtil.slurpFileAndDelete(files.get(0));
    }

}
