package tavant.twms.claim;

import com.domainlanguage.time.CalendarDate;

import org.springframework.util.Assert;
import tavant.twms.domain.claim.*;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.integration.layer.constants.IntegrationConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: 26 Nov, 2010
 * Time: 11:20:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class XMLConversionTest extends IntegrationTestCase{
    public static int count = 0;
    ClaimService claimService;
    ClaimXMLConverter claimXmlConverter;
    Long startingId = 0l;
	Long lastId = 0l;   
    public static final String query = new String("select id from Claim claim")
                                        + " where claim.businessUnitInfo = :businessUnit \n"
                                        + " and claim.state = :clmState \n"
                                        + " and claim.type = :clmType \n"
                                        + " and claim.filedOnDate >= :fromDate \n"
                                        + " and claim.filedOnDate < :tillDate \n"
                                        + " and rownum = 1"
                                        + " order by id desc";



    public void testClaimXmlConversion() {
    	 System.out.println("started at:: "+new Date());
        login("system");
//        Connection conn = getSession().connection();
//        PreparedStatement ps =  null;
//    	PreparedStatement ps2 = null;
//        try {
//        	ps = conn.prepareStatement("select id, modified_xml from Claim_Audit_backup "
//        			+"where modified_xml Like '%<content class=\"dynamic-proxy\">%' and id > ? and rownum <= 1000");
//    		ps2 = conn.prepareStatement("update Claim_Audit_backup set exception_log = ? where id = ?");
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
        
//        verifyClaims();
        verifyClaim("10274244");
//        verifyXMLs(conn,ps, ps2);
        //System.out.println("Claims processed: " + count);       
        flushAndClear();
        System.out.println("Completed at:: "+new Date());
    }
   
    private void verifyXMLs(Connection conn, PreparedStatement ps, PreparedStatement ps2) {
    	//int totalCount = getCountOfClaimAudits(conn);
    	  	
    	//doBatch(conn, ps, ps2, start);  
    loop:while(true) {
    		lastId = doBatch(conn, ps, ps2, startingId);    
    		System.out.println("Processed from :: "+startingId+" "+lastId);
    		if (startingId <= lastId) {    			
    			startingId = lastId;
    		} else {
    			break loop;
    		}
    	}    	
    }
    
    private int getCountOfClaimAudits(Connection conn) {
    	int count = 0;
    	PreparedStatement ps =  null;
    	ResultSet rs = null;
    	try {
    		ps = conn.prepareStatement("select count(*) total from Claim_Audit_Verification4"); 
    		rs = ps.executeQuery();
    		if(rs.next()) {    			
    			count = rs.getInt(1); 			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			rs.close();
        		ps.close();        		
    		} catch (Exception e) {
    			System.out.println("problem in finding count of records:: ");
    			e.printStackTrace();
    		}    		
    	}
    	return count;
    }
    private long doBatch(Connection conn, PreparedStatement ps, PreparedStatement ps2, long start) {   	
    	
    	ResultSet rs = null;
    	Long id = 0l;
    	Map<Long, String> xmls = new HashMap<Long, String>(14000, 0.75f);
    	System.out.println("Starts Batch loading...");
    	long startTime = System.currentTimeMillis();
    	lastId = 0l;
    	try {    		
    		ps.setLong(1, start);
    		rs = ps.executeQuery();
    		while(rs.next()) {
    			id = rs.getLong(1);
    			String modified_xml = rs.getString(2);
    			xmls.put(id, modified_xml);    			
    		}    
    		lastId = id;
    		System.out.println("Last Claim Audit ID in this batch:: "+lastId);
    	} catch (Exception e) {   
    		System.out.println("Error in bactch loading...");
    		e.printStackTrace();
    	} finally {
    		try {    			
    			rs.close();        		        		
    		} catch (Exception e) {
    			
    		}    		
    	}
    	long endLoadingTime = System.currentTimeMillis();
    	System.out.println("Batch loaded... and Done in sec. "+((endLoadingTime-startTime)/1000));
    	System.out.println("Batch size:: "+xmls.size());
    	Set<Long> ids = new HashSet<Long>(xmls.keySet());    		
    	Long key = null;
    	Iterator<Long> it = ids.iterator();
    	String modified_xml = null;
    	String error_msg = null;
    	boolean isErrorOccured = false;
    	while(it.hasNext()) {
    		try {  
    			key = it.next();
    			modified_xml = xmls.get(key);
    			Object newClaim = claimXmlConverter.convertXMLToObject(modified_xml);
    			Assert.isInstanceOf(Claim.class, newClaim);
    		} catch (com.thoughtworks.xstream.converters.ConversionException e) {
    			System.out.println("Failed:: "+key);
    			isErrorOccured = true;
    			error_msg = e.getMessage();
                if (e.getMessage().startsWith(" : no more data available")) {
                    System.out.println("[INCOMPLETE XML ERROR]");
                } else if (e.getMessage().startsWith("For input string: \"\"")) {
                    System.out.println("[NULL TAG ERROR]");
                } else if (e.getMessage().startsWith(" : unexpected character in markup")) {
                    System.out.println("[INVALID CHAR ERROR]");
                } else { 
                	if (error_msg != null && error_msg.startsWith("null")) {
                    	error_msg = "[CONVERSION ERROR] but root cause UNKNOWN";
                	}        
                    System.out.println("[CONVERSION ERROR]");
                }
            } catch (com.thoughtworks.xstream.io.StreamException e ) {
            	System.out.println("Failed:: "+key+ " [BAD TAG ERROR]");
            	isErrorOccured = true;
            	error_msg = e.getMessage();               
            } catch (Exception e) {
            	System.out.println("Failed:: "+key +" [ERROR]");
            	isErrorOccured = true;
            	error_msg = e.getMessage();                
            } finally {
            	if (isErrorOccured) {
            		try {
            			ps2.setString(1, "JAVA TC Error ::"+error_msg);
            			ps2.setLong(2, key);
            			ps2.executeUpdate();
            		} catch (Exception ex) {
            			System.out.println("Error from Update then original error is:: "+key+" "+error_msg);
            			try {
            				ps2.setString(1, "JAVA TC Error ::"+error_msg.subSequence(0, 50).toString());
                			ps2.setLong(2, key);
                			ps2.executeUpdate();
            			} catch (Exception e) {
            				System.out.println("Still Error after:: "+key+" "+error_msg);
            			}
            			//ex.printStackTrace();
            		} finally {
            			isErrorOccured = false;
            		}
            	}  
            }            
    	}   	
    	try {
    		conn.commit();
    		ids.clear();
    		ids = null;
    		xmls.clear();
    		xmls = null;
    	} catch (Exception e) {
    		System.out.println("Problem with commiting and closing...");
    		e.printStackTrace();
    	}
    	System.out.println("Batch completed in secs. " +((System.currentTimeMillis()-endLoadingTime)/1000));
    	return lastId;
   }
    
    private void verifyClaim(String claimNumber) {
    	Claim claim = claimService.findClaimByNumber(claimNumber);
    	if (claim == null) {
    		System.out.println("Claim does not exits:: "+claimNumber);
    	} else {
    		verifyDeserialization(claim);
    	}        
    }

    private void verifyClaims() {
        for (String bu : getBusinessUnits()) {
            for (String claimType : getClaimTypes()) {
                for (ClaimState claimState : getClaimStates()) {
                    CalendarDate startDate = CalendarDate.date(2009, 8, 7); //yyyy,mm,dd change this to go start from
                    CalendarDate endDate = CalendarDate.date(2008, 1, 1); //yyyy,mm,dd change this to go back till
                    int interval = 7; //sample interval in days. Change this to 30 for monthly iteration and 7 for weekly iteration
                    CalendarDate fromDate =  startDate.plusDays(-interval);
                    while (fromDate.isAfter(endDate)) {
                        Claim claim = getClaim(bu, claimState, claimType, fromDate,  startDate);
                        if (claim != null)
                            verifyDeserialization(claim);
                        getSession().clear();
                        startDate = fromDate;
                        fromDate =  startDate.plusDays(-interval);
                    }
                }
            }
        }
    }

    private void verifyDeserialization(Claim claim) {
        long time = System.currentTimeMillis();
        if (claim != null) {
            List<String> auditXMLs = getXMLFromClaimAudit(claim, getClaimAuditStates());
            deserializeXML(claim, auditXMLs);
        }
        System.out.println("verifyDeserialization: " + (System.currentTimeMillis()-time) + " ms");
    }

    private void deserializeXML(Claim claim, List<String> auditXMLs) {
        String currency = claim.getCurrencyForCalculation().getCurrencyCode();
        for (String xml : auditXMLs) {
           // xml = doReplace(currency, xml);
        	//xml = xml.replace("<d>", "<!--<d>");
        	//xml = xml.replace("</d>", "</d>-->");
        	
            try {
                count++;
                Object newClaim = claimXmlConverter.convertXMLToObject(xml);
                Assert.isInstanceOf(Claim.class, newClaim);
                System.out.println("BusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
            } catch (com.thoughtworks.xstream.converters.ConversionException e) {
                if (e.getMessage().startsWith(" : no more data available")) {
                    System.out.println("[INCOMPLETE XML ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
                } else if (e.getMessage().startsWith("For input string: \"\"")) {
                    System.out.println("[NULL TAG ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
                } else if (e.getMessage().startsWith(" : unexpected character in markup")) {
                    System.out.println("[INVALID CHAR ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
                } else {
                    e.printStackTrace();
                    //System.out.println(xml);
                    System.out.println("[CONVERSION ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
                }
            } catch (com.thoughtworks.xstream.io.StreamException e ) {
                System.out.println("[BAD TAG ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
            } catch (Exception e) {
                e.printStackTrace();
                //System.out.println(xml);
                System.out.println("[ERROR]" + "\tBusinessUnit: " + claim.getBusinessUnitInfo() + "\tClaim Number: " + claim.getClaimNumber() + "\tState: " + claim.getState() + "\tType: " + claim.getType() + "\tFiledOn: " + claim.getFiledOnDate());
            }
        }
    }

    private String doReplace(String currency, String xml) {
        String xmlResult = xml
        .replace("org.hibernate.collection.PersistentList", "java.util.List")
/* Yet to include in procedure*/
        .replace("----", "-")
        .replace("---", "-")
        .replace("--", "-")
//                    .replace("<id/>", "<id></id>")
/* Included in procedure */
        //Remove attribute on <inactiveLaborDetails> (2 varients) all possible combinations
        .replace("<inactiveLaborDetails class=\"org.hibernate.collection.PersistentList\"/>","<!--<inactiveLaborDetails class=\"org.hibernate.collection.PersistentList\"/>-->")
        .replace("<inactiveLaborDetails class=\"list\"/>","<!--<inactiveLaborDetails class=\"list\"/>-->")
        .replace("<inactiveLaborDetails class=\"org.hibernate.collection.PersistentList\">","<!--<inactiveLaborDetails class=\"org.hibernate.collection.PersistentList\">")
        .replace("<inactiveLaborDetails class=\"list\">","<!--<inactiveLaborDetails class=\"list\">")
        .replace("</inactiveLaborDetails>","</inactiveLaborDetails>-->")
        .replace("<inactiveLaborDetails/>","<!--<inactiveLaborDetails/>-->")
        //Comment <payment> tag (2 varients)
        .replace("<payment class=\"tavant.twms.domain.claim.payment.Payment\"", "<!--<payment class=\"tavant.twms.domain.claim.payment.Payment\"")
        
        //Change attribute on <belongsTo> tag in <ServicingLocation> tag
        .replace("<belongsTo class=\"tavant.twms.domain.orgmodel.Organization\">", "<belongsTo class=\"tavant.twms.domain.orgmodel.Party\">")
        //Changing null <distance/> tag to have 0 value
        .replace("<distance></distance>", "<distance>0</distance>")
        //Changing null <hours/> tag to have 0 value
        .replace("<hours></hours>", "<hours>0</hours>")
        //Change the <content> tag
        .replace("<content class=\"org.hibernate.lob.SerializableBlob\"/>","<content class=\"dynamic-proxy\">\n" +
                "              <interface>java.sql.Blob</interface>\n" +
                "              <interface>org.hibernate.engine.jdbc.WrappedBlob</interface>\n" +
                "              <interface>java.io.Serializable</interface>\n" +
                "              <handler class=\"org.hibernate.engine.jdbc.SerializableBlobProxy\"/>\n" +
                "            </content>")
        //equipmentItemReference has changed to partItemReference.
        //Making this change though it is handled in the ClaimXMLConverter
        .replace("equipmentItemReference", "partItemReference")
        //<supplierReturnNeeded> field has been removed
        //Making this change though this can be handled in ClaimXMLConverter if XStream in upgraded to 1.3.1
        .replace("<supplierReturnNeeded/>", "<!--<supplierReturnNeeded/>-->")
        .replace("<supplierReturnNeeded>", "<!--<supplierReturnNeeded>")
        .replace("</supplierReturnNeeded>", "</supplierReturnNeeded>-->")
        //<siteNumber> has been removed from Address entity
        //Making this change though this can be handled in ClaimXMLConverter if XStream in upgraded to 1.3.1
        .replace("<siteNumber/>", "<!--<siteNumber/>-->")
        .replace("<siteNumber>", "<!--<siteNumber>")
        .replace("</siteNumber>", "</siteNumber>-->")
        //<location> has been removed from Address entity
        //Making this change though this can be handled in ClaimXMLConverter if XStream in upgraded to 1.3.1
        //todo This also comments the location in Travel Location. Needs to be fixed.
        .replace("<location/>", "<!--<location/>-->")
        .replace("<location>", "<!--<location>")
        .replace("</location>", "</location>-->")
/* Yet to include in procedure*/
        //Change attribute reference on <lastUpdatedBy> tag as this is invalid  (2 varients)
        .replace("serviceInformation/serviceDetail/nonOEMPartsReplaced/tavant.twms.domain.claim.NonOEMPartReplaced/invoice/d/lastUpdatedBy", "lastUpdatedBy")
        .replace("serviceInformation/serviceDetail/laborPerformed/tavant.twms.domain.claim.LaborDetail/d/lastUpdatedBy", "lastUpdatedBy")
        //Remove attributes with currency reference (XML FORMAT ISSUE)
        //todo get correct currency to replace
        .replace("<currency reference=\"../../../../laborPerformed/tavant.twms.domain.claim.LaborDetail/laborRate/currency\"/>", "<currency>" + currency + "</currency>")
        .replace("<currency reference=\"../../../laborPerformed/tavant.twms.domain.claim.LaborDetail/laborRate/currency\"/>", "<currency>" + currency + "</currency>")
        .replace("<currency reference=\"../../laborPerformed/tavant.twms.domain.claim.LaborDetail/laborRate/currency\"/>", "<currency>" + currency + "</currency>")
        //Remove the <filedBy> tag if it refers to <lastUpdatedBy> tag as filedBy is above lastUpdatedBy
        .replace("<filedBy class=\"tavant.twms.domain.orgmodel.User\" reference=\"../lastUpdatedBy\"/>", "<!--<filedBy class=\"tavant.twms.domain.orgmodel.User\" reference=\"../lastUpdatedBy\"/>-->")
        //Comment <failureDate> tag if failure date is not present. (XML FORMAT ISSUE)
        .replace("<failureDate><year></year><month></month><day></day></failureDate>", "<!--<failureDate><year></year><month></month><day></day></failureDate>-->")
        //Comment <installationDate> tag if installation date is not present. (XML FORMAT ISSUE)
        .replace("<installationDate><year></year><month></month><day></day></installationDate>", "<!--<installationDate><year></year><month></month><day></day></installationDate>-->");       
        
        return xmlResult;
    }

    private Claim getClaim(String businessUnit, ClaimState claimState, String claimType, CalendarDate fromDate, CalendarDate tillDate) {
//        long time = System.currentTimeMillis();
        List<Long> id = getSession().createQuery(query)
                                .setParameter("businessUnit", businessUnit)
                                .setParameter("clmState", claimState)
                                .setParameter("clmType", claimType)
                                .setParameter("fromDate", fromDate)
                                .setParameter("tillDate", tillDate)
                                .list();
        Claim claim = null;
        if (id != null && id.size() > 0) {
            claim = claimService.findClaim(id.get(0));
        }
//        System.out.println("getClaim: " + (System.currentTimeMillis()-time) + " ms");
        return claim;
    }

    private List<ClaimState> getClaimStates() {
        List<ClaimState> claimStates = new ArrayList();
        claimStates.add(ClaimState.ACCEPTED_AND_CLOSED);
        return claimStates;
    }

    private List<ClaimState> getClaimAuditStates() {
        List<ClaimState> claimAuditStates = new ArrayList();
        //return empty array if all audit XMLs are to be fetched for processing
        //claimAuditStates.add(ClaimState.ACCEPTED_AND_CLOSED);
        //claimAuditStates.add(ClaimState.PENDING_PAYMENT_RESPONSE);
        claimAuditStates.add(ClaimState.ACCEPTED);
        //claimAuditStates.add(ClaimState.REPLIES);
        return claimAuditStates;
    }

    private List<String> getBusinessUnits() {
        List<String> businessUnits = new ArrayList();
        businessUnits.add(IntegrationConstants.AIR_BU_NAME);
        businessUnits.add(IntegrationConstants.TFM_BU_NAME);
        businessUnits.add(IntegrationConstants.IRI_CLUB_CAR_BU_NAME);
        businessUnits.add(IntegrationConstants.HUSSMANN_BU_NAME);
        businessUnits.add(IntegrationConstants.TRANSPORT_SOLUTIONS_BU_NAME);
        businessUnits.add(IntegrationConstants.TK_TSA_BU_NAME);
        return businessUnits;
    }

    private List<String> getClaimTypes() {
        List<String> claimTypes = new ArrayList();
        claimTypes.add(ClaimType.MACHINE.name());
        claimTypes.add(ClaimType.PARTS.name());
        claimTypes.add(ClaimType.CAMPAIGN.name());
        return claimTypes;
    }


    /*Returns all audit XMLs if states are not defined*/
    private List<String> getXMLFromClaimAudit(Claim claim, List<ClaimState> states) {
        List<String> auditXmls = new ArrayList();
        for(ClaimAudit audit : claim.getClaimAudits()) {
            if (states != null && states.size() > 0) {
                for (ClaimState state: states) {
                    if (audit.getPreviousState().equals(state)) {
                        auditXmls.add(audit.getPreviousClaimSnapshotAsString());
                    }
                }
            } else {
                auditXmls.add(audit.getPreviousClaimSnapshotAsString());
            }
        }
        return auditXmls;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void setClaimXmlConverter(ClaimXMLConverter claimXmlConverter) {
        this.claimXmlConverter = claimXmlConverter;
    }
}
