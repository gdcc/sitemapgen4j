package com.redfin.sitemapgenerator;

public final class SitemapException extends RuntimeException {
    public SitemapException() {
        super();
    }
    
    public SitemapException(String message) {
        super(message);
    }
    
    public SitemapException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SitemapException(Throwable cause) {
        super(cause);
    }
    
    SitemapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
