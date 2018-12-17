/*
 * $Id: JasperReportsResult.java 440597 2006-09-06 03:34:39Z wsmoak $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tavant.twms.web.reports;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.views.jasperreports.JasperReportConstants;
import org.apache.struts2.views.jasperreports.ValueStackDataSource;
import org.apache.struts2.views.jasperreports.ValueStackShadowMap;

import tavant.twms.infra.ApplicationSettingsHolder;



/**
 *  Generates a JasperReports report using the specified format or PDF if no
 *  format is specified.
 *	location (default):- the location of the compiled jasper report, relative from current URL.
 * 	dataSource (required):- the Ognl expression used to retrieve the datasource(usually a List).
 * 	parse:- true by default. If set to false, the location param will not be parsed for Ognl expressions.
 * 	format:- the format in which the report should be generated.
 * 	contentDisposition:- disposition (defaults to "inline").
 * 	documentName:- name of the document.
 * 	delimiter:- the delimiter used when generating CSV reports.Default, the character used is ",".
 * 	imageServletUrl:- name of the url that, when prefixed with the context page, can return report images.
 * 
 */
public class JasperReportsResult extends StrutsResultSupport implements
		JasperReportConstants {

	private static final long serialVersionUID = -2523174799621182907L;

	private final static Log LOG = LogFactory.getLog(JasperReportsResult.class);

	protected String dataSource;

	protected String format;
	//added
	protected String orderBy;
	//added
	protected String groupBy;

	protected String documentName;

	protected String contentDisposition;

	protected String delimiter;
	//Modifed
	protected String imageServletUrl = "/servlets/image?image=";
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private byte[] output;
	
	private JasperPrint jasperPrint;
	
	private JRExporter exporter;
	
    private ApplicationSettingsHolder applicationSettings;

	@SuppressWarnings("unchecked")
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {

		if (this.format == null) {
			this.format = FORMAT_PDF;
		}
		if (dataSource == null) {
			String message = "No dataSource specified...";
			LOG.error(message);
			throw new RuntimeException(message);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating JasperReport for dataSource = " + dataSource
					+ ", format = " + this.format);
		}

		request = (HttpServletRequest) invocation.getInvocationContext().
														get(ServletActionContext.HTTP_REQUEST);
		response = (HttpServletResponse) invocation.getInvocationContext().
														get(ServletActionContext.HTTP_RESPONSE);
		ValueStack stack = invocation.getStack();
		ValueStackDataSource stackDataSource = new ValueStackDataSource(stack, dataSource);
		format = conditionalParse(format, invocation);
		orderBy = conditionalParse(orderBy, invocation);
		groupBy = conditionalParse(groupBy, invocation);
		dataSource = conditionalParse(dataSource, invocation);
		//Added:JRAbstractLRUVirtualizer:other(Other virtulizers to be added))
		JRSwapFile swapFile = new JRSwapFile(applicationSettings.getDefaultLocation(), 1024, 1024);
		JRAbstractLRUVirtualizer virtualizer = new JRSwapFileVirtualizer(20,swapFile, true);

		if (contentDisposition != null) {
			contentDisposition = conditionalParse(contentDisposition,invocation);
		}
		if (documentName != null) {
			documentName = conditionalParse(documentName, invocation);
		}
		if (!StringUtils.isNotEmpty(format)) {
			format = FORMAT_PDF;
		}

		if (!"contype".equals(request.getHeader("User-Agent"))) {
			ServletContext servletContext = (ServletContext) invocation
					.getInvocationContext().get(ServletActionContext.SERVLET_CONTEXT);
			String systemId = servletContext.getRealPath(finalLocation);
			Map parameters = new ValueStackShadowMap(stack);
			File directory = new File(systemId.substring(0, systemId.lastIndexOf(File.separator)));
			parameters.put("reportDirectory", directory);
			if(orderBy != null){
				parameters.put("orderBy", orderBy);
				}
			if(groupBy != null){
				parameters.put("groupBy", groupBy);
				}
			//Added to JasperReports - Internationalization
	    	parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE,ResourceBundle.getBundle("messages", ActionContext.getContext().getLocale()));
			parameters.put(JRParameter.REPORT_LOCALE, invocation.getInvocationContext().getLocale());
			//Added
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			try {
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(systemId);

				jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, stackDataSource);
				if (virtualizer != null) {
					virtualizer.setReadOnly(true);
				}
			} catch (JRException e) {
				LOG.error("Error building report for uri " + systemId, e);
				throw new ServletException(e.getMessage(), e);
			}
			try {
				if (contentDisposition != null || documentName != null) {
					final StringBuffer tmp = new StringBuffer();
					tmp.append((contentDisposition == null) ? "inline": contentDisposition);

					if (documentName != null) {
						tmp.append("; filename=");
						tmp.append(documentName);
						tmp.append(".");
						tmp.append(format.toLowerCase());
					}
					response.setHeader("Content-disposition", tmp.toString());
				}
				if (format.equals(FORMAT_PDF)) {
					exportPDF(jasperPrint);
				} else {
					if (format.equals(FORMAT_CSV)) {
						response.setContentType("text/plain");
						exporter = new JRCsvExporter();
					} else if (format.equals(FORMAT_HTML)) {
						//Added
						exporter = new JRHtmlExporter();
						exportHTML(exporter,jasperPrint);
					} else if (format.equals(FORMAT_XLS)) {
						//Added
						exporter = new JExcelApiExporter();
						exportXLS(exporter,jasperPrint);
					} else if (format.equals(FORMAT_XML)) {
						response.setContentType("text/xml");
						exporter = new JRXmlExporter();
					} else {
						throw new ServletException("Unknown report format: "+ format);
					}
					output = exportReportToBytes(jasperPrint, exporter);
				}
			} catch (JRException e) {
				String message = "Error producing format report for uri " + systemId;
				LOG.error(message, e);
				throw new ServletException(e.getMessage(), e);
			}
			virtualizer.cleanup();
			response.setContentLength(output.length);
			ServletOutputStream ouputStream;
			try {
				ouputStream = response.getOutputStream();
				ouputStream.write(output);
				ouputStream.flush();
				ouputStream.close();
			} catch (IOException e) {
				LOG.error("Error writing report output", e);
				throw new ServletException(e.getMessage(), e);
			}
		} else {
			// Code to handle "contype" request from IE
			try {
				ServletOutputStream outputStream;
				response.setContentType("application/pdf");
				response.setContentLength(0);
				outputStream = response.getOutputStream();
				outputStream.close();
			} catch (IOException e) {
				LOG.error("Error writing report output", e);
				throw new ServletException(e.getMessage(), e);
			}
		}
	}
	private void exportPDF(JasperPrint jasperPrint)throws JRException{
		response.setContentType("application/pdf");
		output = JasperExportManager.exportReportToPdf(jasperPrint);
	}
	//	Added
	private void exportHTML(JRExporter exporter,JasperPrint jasperPrint) throws JRException{
		response.setContentType("text/html");
	
    	response.setHeader("content-disposition","attachment;filename="+documentName+".html");
		String serverName = request.getServerName();
		String serverPort = String.valueOf(request.getServerPort());
		String contextPath = request.getContextPath();
		if (serverPort != null && serverPort.trim().length() > 0) {
			serverName = "http://" + serverName + ":"+ serverPort + contextPath;
		}
		request.getSession().setAttribute(
						ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,jasperPrint);
		ByteArrayOutputStream htmlReport = new ByteArrayOutputStream();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, htmlReport);
		exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER,
				"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, serverName
						+ "/servlets/image?image=");
		//exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS ,Boolean.TRUE);
		exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
		exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
	}
	//	Added
	private void exportXLS(JRExporter exporter,JasperPrint jasperPrint) throws JRException{
		exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT,jasperPrint);
		exporter.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND,Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE,Boolean.FALSE);
		//exporter.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_FONT_SIZE_FIX_ENABLED,Boolean.TRUE);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition","attachment; filename=" + documentName+ ".XLS");
	}

	private byte[] exportReportToBytes(JasperPrint jasperPrint,
			JRExporter exporter) throws JRException {
		byte[] output;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		if (delimiter != null) {
			exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER,delimiter);
		}
		exporter.exportReport();
		output = baos.toByteArray();
		return output;
	}
	
	public String getImageServletUrl() {
		return imageServletUrl;
	}
	public void setImageServletUrl(final String imageServletUrl) {
		this.imageServletUrl = imageServletUrl;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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
	public JasperPrint getJasperPrint() {
		return jasperPrint;
	}
	public void setJasperPrint(JasperPrint jasperPrint) {
		this.jasperPrint = jasperPrint;
	}
	public JRExporter getExporter() {
		return exporter;
	}
	public void setExporter(JRExporter exporter) {
		this.exporter = exporter;
	}
	public byte[] getOutput() {
		return output;
	}
	public void setOutput(byte[] output) {
		this.output = output;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
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
