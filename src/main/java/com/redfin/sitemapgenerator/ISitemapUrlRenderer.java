package com.redfin.sitemapgenerator;

interface ISitemapUrlRenderer<T extends ISitemapUrl> {
	
	Class<T> getUrlClass();
	String getXmlNamespaces();
	void render(T url, StringBuilder sb, W3CDateFormat dateFormat);
}
