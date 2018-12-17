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

package tavant.twms.web.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import static tavant.twms.web.upload.HeaderUtil.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tavant.twms.infra.ApplicationSettingsHolder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bibin.jacob
 * 
 */

public class ReportSearchActionHelper{

	private static final String TASK_PDF  = "PDF";
	private static final String TASK_HTML = "Web Page";
	private static final String TASK_XLS  = "MS Excel";
	
	private HttpServletResponse response;
	
    private ApplicationSettingsHolder applicationSettings;

	private HttpServletRequest request;
	
	private byte[] bytes = null;


	@SuppressWarnings("unchecked")
	public void generateReport(String task,List listOfClaim,String report)  throws Exception{
		JRSwapFile swapFile = new JRSwapFile(applicationSettings.getDefaultLocation(), 1024, 1024);
		JRAbstractLRUVirtualizer virtualizer = new JRSwapFileVirtualizer(20, swapFile, true);	
		Map parameters = new HashMap();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		//Keep .jasper files in target/classes/reports directory(currently in webapp/src/main/webapp/reports) 
		//and .jrxml files in webapp/src/main/jasperreports.
		//In pom.xml <targetDirectory>target/classes/reports</targetDirectory> instead of
		//<targetDirectory>src/main/webapp/reports</targetDirectory>
		InputStream reportStream =getClass().getResourceAsStream("/reports/"+report+".jasper");
    	JasperReport jasperReport =(JasperReport)JRLoader.loadObject(reportStream);
    	JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listOfClaim);
    	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);
    	if (virtualizer != null)
		{
			virtualizer.setReadOnly(true);			
		}
		if (TASK_PDF.equals(task)){
			exportPDF(report,jasperPrint);
		}
		else if (TASK_HTML.equals(task)){
			exportHTML(report, jasperPrint);
    	}
        else if (TASK_XLS.equals(task)){
			exportXLS(report, jasperPrint);
    	}
    	virtualizer.cleanup();
    	response.setContentLength(bytes.length);
    	response.getOutputStream().write(bytes, 0, bytes.length);
	}
	
	private void exportPDF(String report, JasperPrint jasperPrint) throws JRException
	{
        bytes=JasperExportManager.exportReportToPdf(jasperPrint);
        setHeader(response, report+".pdf", PDF);
	}
	private void exportHTML(String report, JasperPrint jasperPrint) throws JRException
	{

       	JRHtmlExporter exporter=new JRHtmlExporter();
    	String serverName = request.getServerName();
    	String serverPort = String.valueOf(request.getServerPort());
    	String contextPath = request.getContextPath();
    	if (serverPort != null && serverPort.trim().length() > 0){
    		serverName = "http://"+serverName + ":" + serverPort + contextPath;
    	}
    	request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
    	ByteArrayOutputStream htmlReport = new ByteArrayOutputStream();
    	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
    	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, htmlReport);
	    exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER,"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
    	exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, serverName+"/servlets/image?image=");
    	exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
    	exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
    	exporter.exportReport();
    	bytes = htmlReport.toByteArray();
        setHeader(response, report+".html", HTML);
	}
	private void exportXLS(String report, JasperPrint jasperPrint) throws JRException
	{
 		JExcelApiExporter exporter = new JExcelApiExporter();
    	ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
    	exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jasperPrint);
    	exporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, xlsReport);
    	exporter.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
    	exporter.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
        exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE,Boolean.TRUE);
    	exporter.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    	exporter.setParameter(JExcelApiExporterParameter.IS_FONT_SIZE_FIX_ENABLED,Boolean.TRUE);
    	exporter.exportReport();
    	bytes = xlsReport.toByteArray();
        setHeader(response, report+".XLS",VND_EXCEL);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * @return the applicationSettings
	 */
	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	/**
	 * @param applicationSettings the applicationSettings to set
	 */
	public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}
}
