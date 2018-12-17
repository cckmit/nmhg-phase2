/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.common.fop;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.servlet.ServletContextURIResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author kuldeep.patil
 *
 */
public class FopHandler {

	public static void transformXMLString(String xslFileName, SAXSource xmlSource, ServletContext context, HttpServletResponse response) {
		transformXMLString(new StreamSource(FopHandler.class.getResourceAsStream(xslFileName)), xmlSource, context, response);
	}

	public static void transformXMLString(StreamSource transformSource, SAXSource xmlSource, ServletContext context, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "filename=pdffile.pdf");
            transformXMLString(transformSource, xmlSource, context, response.getOutputStream());
        } catch (IOException e) {
			throw new RuntimeException("Error during pdf generation", e);
		}
	}

    public static void transformXMLString(StreamSource transformSource, SAXSource xmlSource, ServletContext context, OutputStream response) {
        try {
            URIResolver uriResolver = new ServletContextURIResolver(context);
           // TransformerFactory transformerFactory = TransformerFactory.newInstance();
            com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl transformerFactory = new com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl();
            transformerFactory.setURIResolver(uriResolver);

            Transformer xslfoTransformer = transformerFactory.newTransformer(transformSource);

            FopFactory fopFactory = FopFactory.newInstance();
            fopFactory.setURIResolver(uriResolver);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), response);

            Result res = new SAXResult(fop.getDefaultHandler());
            xslfoTransformer.transform(xmlSource, res);

        } catch (Exception e) {
            throw new RuntimeException("Error during pdf generation", e);
        }
    }
}
