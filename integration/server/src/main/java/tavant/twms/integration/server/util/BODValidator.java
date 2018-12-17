package tavant.twms.integration.server.util;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;



public class BODValidator {
	
	public static void main(String []args)
	{
		try {
		File xsd = new File("E:/NMHG_DEV/integration/bods/src/main/xsd/Tavant/twms/BODs/global/WarrantyClaimCreditNotification.xsd");
		String sampleXML = "E:/NMHG_DEV/integration/server/src/test/resources/server/xml/creditnotification/WarrantyClaimCreditNotification.xml";
		
		// Convert XML to String
		sampleXML = (String) ReadWriteTextFile.getContents(sampleXML);	
		BODValidator bodValidator = new BODValidator();
		boolean success = bodValidator.XMLValidate(xsd, new InputSource(new StringReader(sampleXML)));
		System.out.println(success);
		}catch(Exception ex){
			ex.toString();
		}
	}
	
//	public boolean validate(File xsd, InputSource xml) {
//		boolean success = true;
//		SchemaFactory schemaFactory = SchemaFactory
//				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//		// hook up org.xml.sax.ErrorHandler implementation.
//		schemaFactory.setErrorHandler(new MyErrorHandler());
//		try {
//			// get the custom xsd schema describing the required format for my
//			// XML files.
//			Schema schemaXSD = schemaFactory.newSchema(xsd);
//
//			// Create a Validator capable of validating XML files according to
//			// my custom schema.
//			Validator validator = schemaXSD.newValidator();
//
//			// Get a parser capable of parsing vanilla XML into a DOM tree
//			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//
//			// parse the XML purely as XML and get a DOM tree represenation.
//			Document document = parser.parse(xml);
//
//			// parse the XML DOM tree againts the stricter XSD schema
//			validator.validate(new DOMSource(document));
//
//		} catch (Exception e) {
//			System.out.println(e.toString());
//			success = false;
//		}		
//		return success;
//	}

	private static class MyErrorHandler extends DefaultHandler {
		public void warning(SAXParseException e) throws SAXException {
			System.out.println("Warning: ");
			printInfo(e);
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println("Error: ");
			printInfo(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println("Fattal error: ");
			printInfo(e);
		}

		private void printInfo(SAXParseException e) {
			System.out.println("   Public ID: " + e.getPublicId());
			System.out.println("   System ID: " + e.getSystemId());
			System.out.println("   Line number: " + e.getLineNumber());
			System.out.println("   Column number: " + e.getColumnNumber());
			System.out.println("   Message: " + e.getMessage());
		}
	}
	
	public boolean XMLValidate(File xsd, InputSource xml){
		boolean success = true;

	        try {

	            // parse an XML document into a DOM tree
	            DocumentBuilder parser =
	                DocumentBuilderFactory.newInstance().newDocumentBuilder();
	             
	            Document document = parser.parse(xml);

	            // create a SchemaFactory capable of understanding WXS schemas
	            SchemaFactory factory =
	                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	            // load a WXS schema, represented by a Schema instance
	            Source schemaFile = new StreamSource(xsd);
	            Schema schema = factory.newSchema(schemaFile);

	            // create a Validator object, which can be used to validate
	            // an instance document
	            Validator validator = schema.newValidator();

	            // validate the DOM tree

	            validator.validate(new DOMSource(document));

	        } catch (ParserConfigurationException e) {
	            System.err.println("ParserConfigurationException caught...");
	            success = false;
	            e.printStackTrace();
	        } catch (SAXException e) {
	            System.err.println("SAXException caught...");
	            success = false;
	            e.printStackTrace();
	        } catch (IOException e) {
	            System.err.println("IOException caught...");
	            success = false;
	            e.printStackTrace();
	        }
			return success;
	    
	    }
	    
	}

