sitemapgen4j
============

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=bugs)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=coverage)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=gdcc_sitemapgen4j&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=gdcc_sitemapgen4j)

SitemapGen4j is a library to generate XML sitemaps in Java.

**NOTE:** This is a modernized fork of the upstream https://github.com/dfabulich/sitemapgen4j.
It will receive bug fixes and security updates as necessary and can be found as ``io.gdcc:sitemapgen4j`` on Maven Central.

## What's an XML sitemap?

Quoting from [sitemaps.org](https://sitemaps.org/index.php):

>Sitemaps are an easy way for webmasters to inform search engines about pages on their sites that are available for crawling. In its simplest form, a Sitemap is an XML file that lists URLs for a site along with additional metadata about each URL (when it was last updated, how often it usually changes, and how important it is, relative to other URLs in the site) so that search engines can more intelligently crawl the site.
> 
> Web crawlers usually discover pages from links within the site and from other sites. Sitemaps supplement this data to allow crawlers that support Sitemaps to pick up all URLs in the Sitemap and learn about those URLs using the associated metadata. Using the Sitemap protocol does not guarantee that web pages are included in search engines, but provides hints for web crawlers to do a better job of crawling your site.
> 
> Sitemap 0.90 is offered under the terms of the Attribution-ShareAlike Creative Commons License and has wide adoption, including support from Google, Yahoo!, and Microsoft.

## Getting started

**NOTE: As of v2.0.0, sitemapgen4j is compatible with Java 11+ only.**

First, add this as a dependency to your POM:

```xml
<dependency>
    <groupId>io.gdcc</groupId>
    <artifactId>sitemapgen4j</artifactId>
    <version>2.2.0</version>
</dependency>
```

The easiest way to get started is to just use the WebSitemapGenerator class, like this:

```java
WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
wsg.addUrl("https://www.example.com/index.html"); // repeat multiple times
wsg.write();
```

## Configuring options

But there are a lot of nifty options available for URLs and for the generator as a whole.  To configure the generator, use a builder:

```java
WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
    .gzip(true).build(); // enable gzipped output
wsg.addUrl("https://www.example.com/index.html");
wsg.write();
```

To configure the URLs, construct a WebSitemapUrl with WebSitemapUrl.Options.

```java
import java.time.OffsetDateTime;

WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
WebSitemapUrl url = new WebSitemapUrl.Options("https://www.example.com/index.html")
        .lastMod(OffsetDateTime.now()).priority(1.0).changeFreq(ChangeFreq.HOURLY).build();
// this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly 
wsg.

addUrl(url);
wsg.

write();
```

## Configuring the date format

One important configuration option for the sitemap generator is the date format.  The <a href="https://www.w3.org/TR/NOTE-datetime">W3C datetime standard</a> allows you to choose the precision of your datetime (anything from just specifying the year like "1997" to specifying the fraction of the second like "1997-07-16T19:20:30.45+01:00"); if you don't specify one, we'll try to guess which one you want, and we'll use the default timezone of the local machine, which might not be what you prefer.

```java

// Use DAY pattern (2009-02-07), Greenwich Mean Time timezone
ZoneId zoneId = TimeZone.getTimeZone("GMT").toZoneId();
W3CDateFormat dateFormat = W3CDateFormat.DAY.withZone(zoneId);
WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
    .dateFormat(dateFormat).build(); // actually use the configured dateFormat
wsg.addUrl("https://www.example.com/index.html");
wsg.write();
```

## Lots of URLs: a sitemap index file

One sitemap can contain a maximum of 50,000 URLs.  (Some sitemaps, like Google News sitemaps, can contain only 1,000 URLs.) If you need to put more URLs than that in a sitemap, you'll have to use a sitemap index file.  Fortunately,  WebSitemapGenerator can manage the whole thing for you. 

```java
WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
for (int i = 0; i < 60000; i++) wsg.addUrl("https://www.example.com/doc"+i+".html");
wsg.write();
wsg.writeSitemapsWithIndex(); // generate the sitemap_index.xml

```

That will generate two sitemaps for 60K URLs: sitemap1.xml (with 50K urls) and sitemap2.xml (with the remaining 10K), and then generate a sitemap_index.xml file describing the two.

It's also possible to carefully organize your sub-sitemaps.  For example, it's recommended to group URLs with the same changeFreq together (have one sitemap for changeFreq "daily" and another for changeFreq "yearly"), so you can modify the lastMod of the daily sitemap without modifying the lastMod of the yearly sitemap.  To do that, just construct your sitemaps one at a time using  the WebSitemapGenerator, then use the SitemapIndexGenerator to create a single index for all of them. 

```java
WebSitemapGenerator wsg;
// generate foo sitemap
wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
    .fileNamePrefix("foo").build();
for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/foo"+i+".html");
wsg.write();
// generate bar sitemap
wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
    .fileNamePrefix("bar").build();
for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/bar"+i+".html");
wsg.write();
// generate sitemap index for foo + bar 
SitemapIndexGenerator sig = new SitemapIndexGenerator("https://www.example.com", myFile);
sig.addUrl("https://www.example.com/foo.xml");
sig.addUrl("https://www.example.com/bar.xml");
sig.write();
```

You could also use the SitemapIndexGenerator to incorporate sitemaps generated by other tools.  For example, you might use Google's official Python sitemap generator to generate some sitemaps, and use WebSitemapGenerator to generate some sitemaps, and use SitemapIndexGenerator to make an index of all of them. 

## Validate your sitemaps

SitemapGen4j can also validate your sitemaps using the official XML Schema Definition (XSD).  If you used SitemapGen4j to make the sitemaps, you shouldn't need to do this unless there's a bug in our code.  But you can use it to validate sitemaps generated by other tools, and it provides an extra level of safety.

It's easy to configure the WebSitemapGenerator to automatically validate your sitemaps right after you write them (but this does slow things down, naturally). 

```java
WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
    .autoValidate(true).build(); // validate the sitemap after writing
wsg.addUrl("https://www.example.com/index.html");
wsg.write();
```

You can also use the SitemapValidator directly to manage sitemaps.  It has two methods: validateWebSitemap(File f) and validateSitemapIndex(File f).

## Google-specific sitemaps

Google can understand a wide variety of custom sitemap formats that they made up, including Google Image, News, and Video sitemaps.
SitemapGen4j can generate any/all of these different types of sitemaps.

To generate a special type of sitemap, just use GoogleImageSitemapGenerator, GoogleNewsSitemapGenerator, or GoogleVideoSitemapGenerator instead of WebSitemapGenerator.

You can't mix-and-match regular URLs with Google-specific sitemaps, so you'll also have to use a GoogleImageSitemapUrl, GoogleNewsSitemapUrl, or GoogleVideoSitemapUrl instead of a WebSitemapUrl.  Each of them has unique configurable options not available to regular web URLs.  

## Release Notes

### v2.1.2

#### 🌟 FEATURES
- (none)

#### 💔 BREAKING CHANGES
- (none)

#### 🏹 BUG FIXES
- Fixing wrong XML namespace URIs #18

### v2.1.0

#### 🌟 FEATURES
- (none)

#### 💔 BREAKING CHANGES
- Removed obsolete Google Geo, Mobile, and Code sitemaps

#### 🏹 BUG FIXES
- More code modernizations
- Addressing lots of code smells as suggested by Sonar

### v2.0.0

#### 🌟 FEATURES
- Feature compatible with v1.1+ release train

#### 💔 BREAKING CHANGES
- Requires Java 11+

#### 🏹 BUG FIXES
- (none)