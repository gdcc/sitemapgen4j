package com.redfin.sitemapgenerator;

import org.xmlunit.builder.Input;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
	public static String getResourceAsString(Class<?> clazz, String path) {
		InputStream stream = clazz.getResourceAsStream(path);
		if (stream == null) throw new RuntimeException("resource path not found: " + path);
		InputStreamReader reader = new InputStreamReader(stream);
		StringBuilder sb = new StringBuilder();
		try {
			int c;
			while ((c = reader.read()) != -1) {
				sb.append((char)c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static String slurpFileAndDelete(File file) {
		file.deleteOnExit();
		StringBuilder sb = new StringBuilder();
		try {
			FileReader reader = new FileReader(file);
			int c;
			while ((c = reader.read()) != -1) {
				sb.append((char)c);
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		file.delete();
		return sb.toString();
	}
	
	// This is a hack. Without this instantiation, we would not be able to access the XSD from the main classpath.
	private static final TestUtil instance = new TestUtil();
	
	public static void isValidSitemap(String xml) {
		Validator validator = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
		Source source = Input.fromStream(instance.getClass().getResourceAsStream("sitemap.xsd")).build();
		
		validator.setSchemaSource(source);
		ValidationResult vr = validator.validateInstance(Input.fromString(xml).build());
		assertTrue(vr.isValid(), vr.getProblems().toString());
	}
	
	public static OffsetDateTime getEpochOffsetDateTime(){
		return OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
	}
}
