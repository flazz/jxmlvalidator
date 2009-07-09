package edu.fcla.da.xml;

import java.util.List;
import java.util.Vector;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Aggregates errors, fatal errors, and warnings from
 * 
 */
public class Checker extends DefaultHandler {

	private List<SAXParseException> errors;

	private List<SAXParseException> fatals;

	private List<SAXParseException> warnings;

	/**
	 * 
	 */
	public Checker() {
		errors = new Vector<SAXParseException>();
		warnings = new Vector<SAXParseException>();
		fatals = new Vector<SAXParseException>();
	}

	/**
	 * aggregates errors; callback from DocumentBuilder.
	 * 
	 * @param e
	 * 
	 */
	public void error(SAXParseException e) {
		errors.add(e);
	}

	/**
	 * aggregates fatal errors; callback from DocumentBuilder.
	 * 
	 * @param e
	 * 
	 */
	public void fatalError(SAXParseException e) {
		fatals.add(e);
	}

	/**
	 * @return Vector object
	 */
	public List<SAXParseException> getErrors() {
		return errors;
	}

	/**
	 * @return Vector object
	 */
	public List<SAXParseException> getFatals() {
		return fatals;
	}

	/**
	 * @return Vector object
	 */
	public List<SAXParseException> getWarnings() {
		return warnings;
	}

	/**
	 * aggregates warnings; callback from DocumentBuilder.
	 * 
	 * @param e
	 * 
	 */
	public void warning(SAXParseException e) {
		warnings.add(e);
	}

}
