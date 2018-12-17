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

import com.domainlanguage.timeutil.Clock;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import tavant.twms.common.TWMSException;
import tavant.twms.domain.reports.TREADReportService;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.exputils.ExcelUtil;
import static tavant.twms.web.upload.HeaderUtil.EXCEL;
import static tavant.twms.web.upload.HeaderUtil.setHeader;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TREADReportAction extends TwmsActionSupport implements ServletResponseAware {
	
	private TREADReportService treadReportService;
	private ExcelUtil excelUtil;
	private final String TEMPLATE = "tavant/twms/web/reports/tread-template.xls";
	private final Logger LOGGER = Logger.getLogger(TREADReportAction.class);
	
	private int year;
	private String quarter;
	private HttpServletResponse response;
	
	public String setup(){
		return SUCCESS;
	}
	
	public void generate(){
        setHeader(response, createFileName(), EXCEL);
		try {
			export(response.getOutputStream());
		}
		catch (IOException e) {
			throw new TWMSException("Error occured while generating TREAD report",e);
		}
		catch(Exception e) {
			throw new TWMSException("Error occured while generating TREAD report",e);
		}
	}

	void export(OutputStream outputStream) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("OEM","OEM");
		params.put("year",year);
		params.put("quarter", quarter);
		params.put("generatedDate",Clock.today());
		
		params.put("productionStats", treadReportService.getProductionInfo(year, convertToInt(quarter)));
		params.put("claimStats", treadReportService.getClaimsInfo(year, convertToInt(quarter)));
		params.put("consumerComplaintStats", treadReportService.getConsumerComplaintsInfo(year, convertToInt(quarter)));
		params.put("fieldReportStats", treadReportService.getFieldReportsInfo(year, convertToInt(quarter)));
		params.put("propertyDamageStats", treadReportService.getPropertyDamageInfo(year, convertToInt(quarter)));
		InputStream templateStream = null;
		try {
			templateStream = new FileInputStream(getTreadTemplate());
			excelUtil.export(templateStream, params , outputStream);

		} catch (FileNotFoundException e) {
			throw new TWMSException("Error occured while generating TREAD report",e);
		} catch (IOException e) {
			throw new TWMSException("Error occured while generating TREAD report",e);
		} catch (Exception e) {
			throw new TWMSException("Error occured while generating TREAD report",e);
		}
		finally {
			if (templateStream != null) { 
				try {
					templateStream.close();
				} catch (IOException e) {
					LOGGER.error("Error occured while closing TREAD template stream",e);
					// the main export would have gone through, no need to propagate
					// this exception.
				}
			}
		}
	}

	private String createFileName() {
		return "TREAD-REPORT-" + year + "-" + quarter + ".xls";
	}

	private int convertToInt(String quarter) {
		Map<String,Integer> quarters = new HashMap<String,Integer>();
		quarters.put("Q1",1);
		quarters.put("Q2",2);
		quarters.put("Q3",3);
		quarters.put("Q4",4);
		return quarters.get(quarter) != null ? quarters.get(quarter) : 0 ;
	}

	private File getTreadTemplate() throws IOException {
		ClassPathResource cp = new ClassPathResource(TEMPLATE);
		Assert.notNull(cp.getFile(),"Tread report template file not found");
		return cp.getFile();
	}

	public void setTreadReportService(TREADReportService treadReportService) {
		this.treadReportService = treadReportService;
	}

	public void setExcelUtil(ExcelUtil excelUtil) {
		this.excelUtil = excelUtil;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.response = servletResponse;
	}
	
	@SuppressWarnings("unchecked")
	public List getYears() {
		List years = new ArrayList();
		int currentYear = Clock.today().breachEncapsulationOf_year();
		for(int i=0;i<5;i++) {
			years.add(currentYear);
			currentYear--;
		}
		return years;
	}
	
	public List getQuarters() {
		List<String> quarters = new ArrayList<String>();
		quarters.add("Q1");
		quarters.add("Q2");
		quarters.add("Q3");
		quarters.add("Q4");
		return quarters;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
