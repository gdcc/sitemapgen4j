package com.redfin.sitemapgenerator;

import java.net.URL;
import java.time.OffsetDateTime;

public interface ISitemapUrl {

	OffsetDateTime getLastMod();

	URL getUrl();

}