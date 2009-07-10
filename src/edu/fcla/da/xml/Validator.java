package edu.fcla.da.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Namespace aware XML Validation against XMLSchema and DTD.
 * 
 * @author franco
 */
public class Validator {

	/**
	 * Command line interface
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void main(String args[]) throws SAXException, IOException, ParserConfigurationException {

		// The file to validate
		File file;
		if (System.getProperty("file") == null) {
			throw new RuntimeException("property file is not specified");
		}
		file = new File(System.getProperty("file"));

		// Link resolution
		Validator validator;
		if (System.getProperty("webcache") == null) {
			validator = new Validator();
		} else {
			File webCachDir = new File(System.getProperty("webcache"));
			Map<String, String> locations = new Hashtable<String, String>();
			locations.put("-//W3C//DTD XMLSCHEMA 200102//EN", "http://www.w3.org/2001/XMLSchema.dtd");
			locations.put("datatypes", "http://www.w3.org/2001/datatypes.dtd");
			WebCacheResolver resolver = new WebCacheResolver(webCachDir, locations);
			validator = new Validator(resolver);
		}

		// Validation
		Checker checker = validator.validate(file);

		// Report errors and warnings
		System.out.println("Warnings: " + checker.getWarnings().size());
		for (SAXParseException e : checker.getWarnings()) {
			System.out.println(e.getLineNumber() + ": " + e.getMessage());
		}

		System.out.println("Errors: " + checker.getErrors().size());
		for (SAXParseException e : checker.getErrors()) {
			System.out.println(e.getLineNumber() + ": " + e.getMessage());
		}

		System.out.println("Fatal Errors: " + checker.getFatals().size());
		for (SAXParseException e : checker.getFatals()) {
			System.out.println(e.getLineNumber() + ": " + e.getMessage());
		}

	}

	private DocumentBuilder builder;

	/**
	 * @throws ParserConfigurationException
	 * @throws FatalException
	 */
	public Validator() throws ParserConfigurationException {
		builder = makeDocumentBuilder();
	}

	/**
	 * @param resolver
	 *            an entity resolver
	 * @throws ParserConfigurationException
	 * @throws ParseConfigurationException
	 */
	public Validator(EntityResolver resolver)
			throws ParserConfigurationException {
		builder = makeDocumentBuilder();
		builder.setEntityResolver(resolver);
	}

	/**
	 * @param file
	 * @return an ErrorChecker with the results of parsing.
	 * @throws SAXException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Checker validate(File file) throws SAXException, IOException {
		Checker checker = new Checker();
		builder.setErrorHandler(checker);
		try { 
			builder.parse(file);
		} catch (SAXParseException e) {
			// Empty catch block reasoning:
			// Catch any fatal exceptions which are thrown by the parser when
			// the XML is ill-formed. Throwing an error is required in the
			// XML specification, so we can't override it. The fatal error will
			// be logged in the ErrorHandler returned below.
		}
		return checker;
	}

	/**
	 * @param is
	 * @return an ErrorChecker with the results of parsing.
	 * @throws SAXException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Checker validate(InputStream is) throws SAXException, IOException {
		Checker checker = new Checker();
		builder.setErrorHandler(checker);
		try {
			builder.parse(is);
		} catch (SAXParseException e) { /* See note in file validate method */ }
		return checker;
	}

	/**
	 * @param is
	 * @param systemId
	 * @return an ErrorChecker with the results of parsing.
	 * @throws SAXException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Checker validate(InputStream is, String systemId)
			throws SAXException, IOException {
		Checker checker = new Checker();
		builder.setErrorHandler(checker);
		try {
			builder.parse(is, systemId);
		} catch (SAXParseException e) { /* See note in file validate method */ }
		return checker;
	}

	/**
	 * @param uri
	 * @return an ErrorChecker with the results of parsing.
	 * @throws SAXException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Checker validate(String uri) throws SAXException, IOException {
		Checker checker = new Checker();
		builder.setErrorHandler(checker);
		try {
			builder.parse(uri);
		} catch (SAXParseException e) { /* See note in file validate method */ }
		return checker;
	}

	/**
	 * @return a document builder
	 * @throws ParserConfigurationException
	 * 
	 */
	private DocumentBuilder makeDocumentBuilder() throws ParserConfigurationException {

		// Create a validating & namespace aware DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// Set the properties of the DocumentBuilder
		dbf.setNamespaceAware(true);
		dbf.setAttribute("http://xml.org/sax/features/validation", true);
		dbf.setAttribute("http://apache.org/xml/features/validation/schema", true);
		dbf.setAttribute("http://apache.org/xml/features/validation/schema-full-checking", true);
		dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

		return dbf.newDocumentBuilder();
	}
}