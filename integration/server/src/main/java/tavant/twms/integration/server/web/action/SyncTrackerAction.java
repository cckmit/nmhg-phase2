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
package tavant.twms.integration.server.web.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import tavant.twms.integration.server.common.BeanLocator;
import tavant.twms.integration.server.common.PropertiesBean;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;

@SuppressWarnings("serial")
public class SyncTrackerAction extends AbstractIntegrationServerAction {

    private static final String PAYLOAD = "payload";
    private static final String ERROR_MESSAGE = "errormessage";
    private List<SyncTracker> syncTrackers;
    private SyncTrackerDAO syncTrackerDao;
    protected Long trackerId;
    protected String payload;
    protected String errorMessage;
    private String uniqueIdValue;
    protected String status;
    protected List<String> statusOptions;
    private int page;
    private int rows;
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static Pattern RESPONSE_STATUS_PATTERN = Pattern.compile("<status>(.*?)<\\/status>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static Pattern ERROR_TYPE_PATTERN = Pattern.compile("<ErrorType>(.*?)<\\/ErrorType>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @SkipValidation
    @Override
    public String execute() throws Exception {
//        syncTrackers = syncTrackerDao.findAll();
        return SUCCESS;
    }

    public String search() throws Exception {
        long total = syncTrackerDao.getCount(uniqueIdValue, syncType, status, fromDate, toDate);
        if(total > 0){
            syncTrackers = syncTrackerDao.search(uniqueIdValue, syncType, status,
                    fromDate, toDate, getPage()-1, getRows());
            JSONObject json = new JSONObject();
            JSONArray rowObjs = new JSONArray();
            for (SyncTracker syncTracker : syncTrackers) {
                JSONObject row = new JSONObject();
                row.put("id", syncTracker.getId());
                row.put("syncType", syncTracker.getSyncType());
                row.put("uniqueIdName", syncTracker.getUniqueIdName());
                row.put("uniqueIdValue", syncTracker.getUniqueIdValue());
                row.put("status", syncTracker.getStatus().getStatus());
                row.put("noOfAttempts", syncTracker.getNoOfAttempts());
                row.put("createDate", syncTracker.getCreateDate());
                row.put("updateDate", syncTracker.getUpdateDate());
                rowObjs.put(row);
            }
            json.put("rows", rowObjs);
            json.put("total",((int)total/getRows())+1);
            json.put("records", total);
            json.put("page", getPage());
            return writeJSONResponse(json.toString());
        }else{
            return writeJSONResponse(new JSONObject().toString());
        }
    }

    @SkipValidation
    public String fetchPayload() throws Exception {
        SyncTracker st = syncTrackerDao.findById(trackerId);
        payload = (st != null && st.getBodXML() != null ? st.getBodXML() : "Not available");
        return PAYLOAD;
    }

    @SkipValidation
    public String fetchError() throws Exception {
        SyncTracker st = syncTrackerDao.findById(trackerId);
        errorMessage = (st != null && st.getErrorMessage() != null ? st.getErrorMessage() : "Not available");
        return ERROR_MESSAGE;
    }

    @SkipValidation
    public String reprocess() throws Exception {
        SyncTracker st = syncTrackerDao.findById(trackerId);
        String response = null;
        st.setStartTime(new Date());
        st.setNoOfAttempts(st.getNoOfAttempts() + 1);
        try {
            final String xml = transformBODXml(st.getBodXML());
            BeanLocator beanLocator = new BeanLocator();
            WebServiceTemplate webServiceTemplate = (WebServiceTemplate) beanLocator.getBean("webServiceTemplate");
            PropertiesBean propertiesBean = (PropertiesBean) beanLocator.getBean("propertiesBean");
            String url = propertiesBean.getIntegrationURL() + "syncGlobalCreditNotification";
            response = webServiceTemplate.sendAndReceive(url, new WebServiceMessageCallback() {

                public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
                    SOAPMessage soapMessage = ((SaajSoapMessage) message).getSaajMessage();
                    try {
                        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
                        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
                        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                        SOAPElement stateElement = envelope.addChildElement("syncGlobalCreditNotification");
                        SOAPElement valueElement = stateElement.addChildElement("value0");
                        valueElement.setTextContent(xml);
                        envelope.getBody().addChildElement(stateElement);
                        soapMessage.saveChanges();
                    } catch (SOAPException ex) {
                        throw new TransformerException("Exception while tranforming bod xml to soap message", ex);
                    }
                }
            }, new WebServiceMessageExtractor<String>() {

                public String extractData(WebServiceMessage message) throws IOException, TransformerException {
                    try {
                        SOAPMessage soapMessage = ((SaajSoapMessage) message).getSaajMessage();
                        return soapMessage.getSOAPPart().getEnvelope().getBody().getTextContent().trim();
                    } catch (Exception ex) {
                        throw new TransformerException("Exception while tranforming/extracting response xml from App Server", ex);
                    }
                }
            });
            populateSyncTracker(st, response);
        } finally {
            st.setUpdateDate(new Date());
            syncTrackerDao.update(st);
        }
        errorMessage = response;
        return ERROR_MESSAGE;
    }

    public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDAO) {
        this.syncTrackerDao = syncTrackerDAO;
    }

    public List<String> getStatusOptions() {
        return statusOptions;
    }

    public void setStatusOptions(List<String> statusOptions) {
        this.statusOptions = statusOptions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SyncTracker> getSyncTrackers() {
        return syncTrackers;
    }

    public void setSyncTrackers(List<SyncTracker> syncTrackers) {
        this.syncTrackers = syncTrackers;
    }

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        statusOptions = new ArrayList<String>() {
        };
        statusOptions.add(SyncStatus.COMPLETED.getStatus());
        statusOptions.add(SyncStatus.TO_BE_PROCESSED.getStatus());
        statusOptions.add(SyncStatus.FAILED.getStatus());
        statusOptions.add(SyncStatus.IN_PROGRESS.getStatus());
        if (syncTrackerDao == null) {
            this.syncTrackerDao = (SyncTrackerDAO) new BeanLocator().getBean("syncTrackerDao");
        }
        syncTypeOptions = syncTrackerDao.findAllSyncTypes();
    }

    public String getUniqueIdValue() {
        return uniqueIdValue;
    }

    public void setUniqueIdValue(String uniqueIdValue) {
        this.uniqueIdValue = uniqueIdValue;
    }

    private String transformBODXml(String bodXML) throws TransformerException {
        InputStream xsltStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("xslt/transformCreditNotification.xsl");
        if (xsltStream == null) {
            throw new RuntimeException("XSL file for credit notification xml transformation not found !!!");
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer(new StreamSource(xsltStream));
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Could not create XSL transformer !!!", ex);
        }
        StringWriter s = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(bodXML)), new StreamResult(s));
        return s.toString();
    }

    private void populateSyncTracker(SyncTracker st, String response) {
        String[] responses = StringUtils.splitByWholeSeparator(response,XML_HEADER);        
        Matcher codeMatcher = RESPONSE_STATUS_PATTERN.matcher(response);
        if (codeMatcher.find() && "SUCCESS".equalsIgnoreCase(codeMatcher.group(1))) {
            st.setStatus(SyncStatus.COMPLETED);
            st.setErrorMessage(null);
            st.setErrorType(null);
        }else{
            Matcher m = ERROR_TYPE_PATTERN.matcher(response);
            if(m.find()){
                st.setErrorType(m.group(1));
            }
            st.setErrorMessage(responses[0]);
        }

    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }    
    
}
