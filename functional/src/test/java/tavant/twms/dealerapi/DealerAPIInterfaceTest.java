package tavant.twms.dealerapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.alarmcode.AlarmCodeService;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.integration.layer.DealerIntegrationService;


import com.tavant.dealerinterfaces.claimsubmission.response.ClaimSubmissionResponseDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryrequest.UnitServiceHistoryRequestDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.ClaimDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO;

public class DealerAPIInterfaceTest extends IntegrationTestCase {
    private DealerIntegrationService dealerIntegrationService;
    private AlarmCodeService alarmCodeService;
    private InventoryService inventoryService;
    private CatalogService catalogService;
    public void setAlarmCodeService(AlarmCodeService alarmCodeService) {
		this.alarmCodeService = alarmCodeService;
	}


	public InventoryService getInventoryService() {
		return inventoryService;
	}


	public CatalogService getCatalogService() {
		return catalogService;
	}


	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}


	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}


	public void setDealerIntegrationService(DealerIntegrationService dealerIntegrationService) {
        this.dealerIntegrationService = dealerIntegrationService;
    }


    public void testGetUnitServiceHistory() throws IOException {
        login("dockter");
        // assertEquals(true, isInvalidRequest());
        assertNotNull(getUnitServiceHistoryResponse());
        // assertNotNull(getUnitServiceHistoryResponseForFieldModificationCode());
        // assertNotNull(getClaimSubmittedOnInventory());
    }
    
   /* public void testRegisterUnitWarranty() throws IOException {
    	login("dockter");
    	assertNotNull(registerUnitWarrantyWithOutMajorComponentsAndProductInfo());
    	assertNotNull(registerUnitWarrantyWithMajorComponentsAndProductInfo());
    	assertNotNull(registerUnitWarrantyBasedOnTransactionType());
    	
    }*/
    
   /* public void testRegisgterMajorComponent() throws IOException {
    	login("dockter");
    	assertNotNull(registerMajorComponentWithInvalidXML());
    	assertNotNull(registerMajorComponentAsStandAlone());
    	assertNotNull(registerMajorComponentForUnit());
    	assertNotNull(registerMajorComponentWithNonCertifiedInstallingDealer());
    	
    }*/
    
    
    public String registerUnitWarrantyWithOutMajorComponentsAndProductInfo() throws IOException {
    	String responseXML=(String) dealerIntegrationService.registerUnitWarranty
    	(getRequestString("/tavant/twms/dealerinterface/UWR/soapInput_UWR_DRAFT_TRUE_WITHOUT_MAJORCOMPONENTS_PRODUCT_INFO.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_UWR_DRAFT_TRUE_WITHOUT_MAJORCOMPONENTS_PRODUCT_INFO_Response.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return responseXML;
    }    
    
    public String registerUnitWarrantyWithMajorComponentsAndProductInfo() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerUnitWarranty(getRequestString("/tavant/twms/dealerinterface/UWR/soapInput_UWR_DRAFT_TRUE_WITH_MAJORCOMPONENTS_PRODUCT_INFO.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_UWR_DRAFT_TRUE_WITH_MAJORCOMPONENTS_PRODUCT_INFO.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return responseXML;
    }
    public String registerUnitWarrantyBasedOnTransactionType() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerUnitWarranty(getRequestString("/tavant/twms/dealerinterface/UWR/soapInput_UWR_BasedOn_TransactionType.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_UWR_BasedOn_TransactionType.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
        }
        return responseXML;
    }
    
    public String registerMajorComponentAsStandAlone() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerMajorComponent(getRequestString("/tavant/twms/dealerinterface/majorcomponent/soapInput_MC_StandAlone.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_MC_StandAlone.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
        }
        return responseXML;
    }
    public String registerMajorComponentForUnit() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerMajorComponent(getRequestString("/tavant/twms/dealerinterface/majorcomponent/soapInput_MC_For_Unit.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_MC_For_Unit.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
        }
        return responseXML;
    }
    public String registerMajorComponentWithInvalidXML() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerMajorComponent(getRequestString("/tavant/twms/dealerinterface/majorcomponent/soapInput_MC_InvalidXML.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_MC_InvalidXML.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
        }
        return responseXML;
    }
    public String registerMajorComponentWithNonCertifiedInstallingDealer() throws IOException {
    	String responseXML=(String) dealerIntegrationService.
    	registerMajorComponent(getRequestString("/tavant/twms/dealerinterface/majorcomponent/soapInput_MC_NonCertified_InstallingDealer.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/devendrababu.n/Desktop/output_xmls/soapOutput_MC_NonCertified_InstallingDealer.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
        }
        return responseXML;
    }
    
    
  /*  public void testSerializedMachineClaimSubmission() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/MachineClaimSerialized.xml"));
        assertNotNull(getDraftedClaimId(responseXML));        									
    }
    
    public void testMachineClaimSubmissionSMR() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_MSMR.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testPartsClaimSubmissionWithCM() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_NSPICM.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testPartsClaimSubmissionForPartNotInstalled() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_NSP.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testNonSerializedMachineClaim() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_NSM.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testSerializedPartsClaimInstalledOnSerializedMachine() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_SPSM.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testSerializedPartsClaim() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_SP.xml"));
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testSerializedPartsClaimInstalledOnNonNSM() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_SPINSM.xml"));
       assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testSerializedPartsClaimInstalledOnCM() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_SPICM.xml"));
        System.out.println("respo = " + responseXML);
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    
    public void testCampaignClaimSubmission() throws IOException {
    	login("tkd115811");     
        String responseXML=(String) dealerIntegrationService.submitClaim(getRequestString("/tavant/twms/dealerinterface/soapInput_CC.xml"));
        System.out.println("The response = " + responseXML);
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/naveen.jadav/Desktop/InputXML/response.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        assertNotNull(getClaimNumber(responseXML));        									
    }
    
    public void testGetAlarmCodes() {
    	login("tkd115811");
    	List<String> alarmCodeList = new ArrayList<String>();
    	alarmCodeList.add("0113");
    	ItemGroup itemGroup = new ItemGroup();
    	itemGroup.setId(new Long(1100000159600L));
    	List<AlarmCode> alarmcodes = alarmCodeService.findAlarmCodesOfProductByCodes(alarmCodeList, itemGroup);
    	System.out.println("alarmcodes = " + alarmcodes);
    	assertNotNull(alarmcodes);
    }
  */  /**
     * 
     * @return true if in valid
     * @throws IOException
     */
    private boolean isInvalidRequest() throws IOException{
    	/**
    	 * Test method to validate the request XML structure
    	 */                
        String requestXML = getRequestString("/tavant/twms/dealerinterface/invalid-soapInput.xml");
        try {
        	UnitServiceHistoryRequestDocumentDTO dto = UnitServiceHistoryRequestDocumentDTO.Factory.parse(requestXML);
        	ArrayList validationErrors = new ArrayList();
        	XmlOptions validationOptions = new XmlOptions();
        	validationOptions.setErrorListener(validationErrors);
        	if(!dto.validate(validationOptions)){
            	//Print the errors if the XML is invalid.                	
        	    Iterator iter = validationErrors.iterator();
        	    while (iter.hasNext())
        	    {
        	        System.out.println("Error in request structure >> " + iter.next() + "\n");
        	    }
        	    return true;
            }        	
        } catch (XmlException e) {
            return true;
        }
        return false;
    }
    
    private String getUnitServiceHistoryResponse() throws IOException {
        String responseXML=(String) dealerIntegrationService.getUnitServiceHistory(getRequestString("/tavant/twms/dealerinterface/USH/soapInput_USH_RETAIL.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/naveen.jadav/Desktop/InputXML/USH_EQA/output/response_Retail.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return responseXML;
    }
    
    private String getUnitServiceHistoryResponseForFieldModificationCode() throws IOException {
        String responseXML=(String) dealerIntegrationService.getUnitServiceHistory(getRequestString("/tavant/twms/dealerinterface/USH/soapInput_USH_FMC.xml"));
        if (responseXML != null) {
            try {
            		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/naveen.jadav/Desktop/InputXML/USH_EQA/output/response_FMC.xml"));
                    out.write((String)responseXML);
                    out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return responseXML;
    }
    /**
     * Utility method that converts input stream (that contains the XML contents) to a String for parsing
     * @param is
     * @return
     * @throws IOException
     */
    private String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
    
    private String getRequestString(String fileName)  throws IOException {
        Resource resource = new ClassPathResource(fileName);
        return convertStreamToString(resource.getInputStream());
    }
    
    private UnitServiceHistoryResponseDocumentDTO convertUnitServiceResponseXMLToResponseDTO(String src) {
        UnitServiceHistoryResponseDocumentDTO dto = null;
        if (!StringUtils.isBlank(src)) {
            try {
                dto = UnitServiceHistoryResponseDocumentDTO.Factory.parse(src);
           } catch (XmlException e) {
                throw new RuntimeException("Error parsing xml", e);
            }
        }
        return dto;
    }
    
    private ClaimSubmissionResponseDocumentDTO convertClaimSubmissionResponseXMLToResponseDTO(String src) {
    	ClaimSubmissionResponseDocumentDTO dto = null;
        if (!StringUtils.isBlank(src)) {
            try {
                dto = ClaimSubmissionResponseDocumentDTO.Factory.parse(src);
           } catch (XmlException e) {
                throw new RuntimeException("Error parsing xml", e);
            }
        }
        return dto;
    }
    
    /**
     * This method returns the Claim Number from ClaimSubmission Response
     * @return
     * @throws IOException
     */
    private String getDraftedClaimId(String responseXML) throws IOException {        
    	ClaimSubmissionResponseDocumentDTO responseDTO = convertClaimSubmissionResponseXMLToResponseDTO(responseXML);
        return responseDTO.getClaimSubmissionResponse().getDraftClaimUniqueIdentifier();        
    }
    
    /**
     * This method returns the Claim Number from ClaimSubmission Response
     * @return
     * @throws IOException
     */
    private String getClaimNumber(String responseXML) throws IOException {        
    	ClaimSubmissionResponseDocumentDTO responseDTO = convertClaimSubmissionResponseXMLToResponseDTO(responseXML);
        return responseDTO.getClaimSubmissionResponse().getClaimNumber();        
    }
    
    /**
     * This method returns the Claim Number for the inventory item
     * @return
     * @throws IOException
     */
    private String getClaimSubmittedOnInventory() throws IOException {
        String responseXML=(String) dealerIntegrationService.getUnitServiceHistory(getRequestString("/tavant/twms/dealerinterface/internaluser-soapInput-campaigndata.xml"));
        UnitServiceHistoryResponseDocumentDTO responseDTO = convertUnitServiceResponseXMLToResponseDTO(responseXML);
        InventoryItemDTO inventoryItemDTO = responseDTO.getUnitServiceHistoryResponse().getInventoryItem();
        ClaimDTO[] claimHistoryDTO = inventoryItemDTO.getClaimHistory().getClaimDetailsArray();
        return claimHistoryDTO[0].getClaimNumber();
    }
    
    /*public void testFindMajorComponent() {
    	login("gil");
    	assertNotNull(inventoryService.findInventoryItemForMajorComponent(new Long("1100018957820")));
    }*/
    
  /*  public void testListModels() {
    	login("glenn");
    	try {
			assertNotNull(catalogService.findModelByModelName("RI5"));
		} catch (CatalogException e) {
			e.printStackTrace();
		}
    }*/
 
}
