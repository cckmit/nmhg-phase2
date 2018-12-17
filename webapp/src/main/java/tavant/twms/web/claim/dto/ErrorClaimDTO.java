/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.claim.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.web.xls.reader.ConversionResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author kaustubhshobhan.b
 *
 */
public class ErrorClaimDTO {
	private static Logger logger = LogManager.getLogger(ErrorClaimDTO.class);

    private String claimType;

    private String failureDate;

    private String repairDate;

    private String hoursInService;

    private String installationDate;

    private String faultCode;

    private String causalPart;

    private FailureTypeDefinition faultFound;

    private FailureCauseDefinition causedBy;

    private String location;

    private String trips;

    private BigDecimal hours = BigDecimal.ZERO;

    private BigDecimal distance = BigDecimal.ZERO;

    private String serialNumber;

    private String itemNumber;

    private String probableCause;

    private String workPerformed;

    private String conditionFound;

    private String otherComments;

    private String failures = "";
    
    private String mealsExpense;
    
    private String itemFreightAndDuty;
    
    private String handlingFee;
    
    private String transportation;

	private List<ServiceInformationDTO> serviceInfoList = new ArrayList<ServiceInformationDTO>();

    public ErrorClaimDTO(){

    }
    public ErrorClaimDTO(ConversionResult result){
    	Claim claim= (Claim)result.getResult();
    	if(claim.getFailureDate()!=null){
    	  setFailureDate(claim.getFailureDate().toString());
    	}
    	if(claim.getRepairDate()!=null){
    	  setRepairDate(claim.getRepairDate().toString());
    	}

        //TODO: For now taking the first item. Needs rethink.
        ClaimedItem firstClaimedItem = claim.getClaimedItems().get(0);

        if(firstClaimedItem.getHoursInService()!=null){
    	  setHoursInService(firstClaimedItem.getHoursInService().toString());
    	}
    	if((claim.getInstallationDate()!= null)){
    	  setInstallationDate(claim.getInstallationDate().toString());
    	}
    	if(claim.getServiceInformation().getFaultCode()!=null){
    	  setFaultCode(claim.getServiceInformation().getFaultCode());
    	}
    	setCausalPart("");
    	if(claim.getServiceInformation().getFaultFound()!=null){
    	  setFaultFound(claim.getServiceInformation().getFaultFound());
    	}
    	if(claim.getServiceInformation().getCausedBy()!=null){
    	  setCausedBy(claim.getServiceInformation().getCausedBy());
    	}
    	if(claim.getServiceInformation().getServiceDetail().getTravelDetails().getLocation()!=null){
    	  setLocation(claim.getServiceInformation().getServiceDetail().getTravelDetails().getLocation());
    	}
    	String hour=claim.getServiceInformation().getServiceDetail().getTravelDetails().getHours();
    	if(!StringUtils.isEmpty(hour))
    		setHours(new BigDecimal(hour));
    	setDistance(claim.getServiceInformation().getServiceDetail().getTravelDetails().getDistance());

        ItemReference firstItemReference = firstClaimedItem.getItemReference();
        
        if(firstItemReference.getReferredInventoryItem()!=null){
          setSerialNumber("yes");
          setItemNumber(firstItemReference.getReferredInventoryItem().getSerialNumber());
    	}else if(firstItemReference.getUnserializedItem()!=null){
    	  setSerialNumber("no");
    	  setItemNumber(firstItemReference.getUnserializedItem().getNumber());
    	}
    	if(claim.getServiceInformation().getServiceDetail().getMealsExpense()!=null){
    	  setMealsExpense(claim.getServiceInformation().getServiceDetail().getMealsExpense().toString());
    	}
    	if(claim.getServiceInformation().getServiceDetail().getItemFreightAndDuty()!=null){
    	  setItemFreightAndDuty(claim.getServiceInformation().getServiceDetail().getItemFreightAndDuty().toString()); 
    	}
    	if(claim.getServiceInformation().getServiceDetail().getHandlingFee()!=null){
      	  setHandlingFee(claim.getServiceInformation().getServiceDetail().getHandlingFee().toString()); 
      	}
		if (claim.getServiceInformation().getServiceDetail()
				.getTransportationAmt() != null) {
			setTransportation(claim.getServiceInformation().getServiceDetail()
					.getTransportationAmt().toString());
		}
        setProbableCause(claim.getProbableCause());
        setWorkPerformed(claim.getWorkPerformed());
        setConditionFound(claim.getConditionFound());
        setOtherComments(claim.getOtherComments());
        for (String error : result.getErrors()) {
		failures = failures + error +" ,";
		}
        setServiceInfoList(claim);
    }
    private void setServiceInfoList(Claim claim) {
    	int length;
        ServiceInformationDTO informationDTO ;

        if(claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size()>
          claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size() &&
          (claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size()>
          claim.getServiceInformation().getServiceDetail().getLaborPerformed().size())) {
        	length=claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size();
        }else if(!(claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size()>
        claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size())&&
        (claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size()>
        claim.getServiceInformation().getServiceDetail().getLaborPerformed().size())){
        	length = claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size();
        }else{
        	length = claim.getServiceInformation().getServiceDetail().getLaborPerformed().size();
        }

        for(int i=0; i<length;i++){
          informationDTO = new ServiceInformationDTO();

          if((claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size()>i) &&
               (claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().size()!=0)){
        	 OEMPartReplaced oemPartReplaced = claim.getServiceInformation().getServiceDetail().getOEMPartsReplaced().get(i);
        	 informationDTO.setOemQuantity(oemPartReplaced.getNumberOfUnits());
        	 if(oemPartReplaced.getItemReference().getUnserializedItem()!=null){
        	   informationDTO.setOemItemNo(oemPartReplaced.getItemReference().getUnserializedItem().getNumber());
        	   if(!oemPartReplaced.getItemReference().getReferredItem().isSerialized()){
        	     informationDTO.setOemSerialNo("No");
        	   }else{
        	     informationDTO.setOemSerialNo("Yes");
        	   }
            }
          }

          if((claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size()>i) &&
                (claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().size()!=0)){
        	  NonOEMPartReplaced nonOEMPartReplaced = claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced().get(i);
        	  informationDTO.setNonOemDescription(nonOEMPartReplaced.getDescription());
        	  informationDTO.setNonOemPrice(nonOEMPartReplaced.getPricePerUnit().toString());
              if(nonOEMPartReplaced.getNumberOfUnits()!=null){
        	  informationDTO.setNonOemQuantity(nonOEMPartReplaced.getNumberOfUnits().toString());
              }
          }

          if((claim.getServiceInformation().getServiceDetail().getLaborPerformed().size()>i) &&
                (claim.getServiceInformation().getServiceDetail().getLaborPerformed().size()!=0)){
        	  LaborDetail laborDetail = claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(i);
        	  informationDTO.setJobCode(laborDetail.getServiceProcedure().getDefinition().getCode());
        	  informationDTO.setHoursSpent(laborDetail.getTotalHours(claim.getServiceInformation().getServiceDetail().getStdLaborEnabled()));
          }

          serviceInfoList.add(informationDTO);
        }

	}

    public String getCausalPart() {
        return causalPart;
    }

    public void setCausalPart(String causalPart) {
        this.causalPart = causalPart;
    }
    
    public FailureCauseDefinition getCausedBy() {
		return causedBy;
	}
	public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getConditionFound() {
        return conditionFound;
    }

    public void setConditionFound(String conditionFound) {
        this.conditionFound = conditionFound;
    }

    public BigDecimal getDistance() {
		return distance;
	}
    
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
	
	public String getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(String failureDate) {
        this.failureDate = failureDate;
    }

    public String getFailures() {
        return failures;
    }

    public void setFailures(String failures) {
        this.failures = failures;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }    

    public FailureTypeDefinition getFaultFound() {
		return faultFound;
	}
	public void setFaultFound(FailureTypeDefinition faultFound) {
		this.faultFound = faultFound;
	}
	public void setCausedBy(FailureCauseDefinition causedBy) {
		this.causedBy = causedBy;
	}
	public BigDecimal getHours() {
		return hours;
	}
    
	public void setHours(BigDecimal hours) {
		this.hours = hours;
	}
	
	public String getHoursInService() {
        return hoursInService;
    }

    public void setHoursInService(String hoursInService) {
        this.hoursInService = hoursInService;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    public String getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(String probableCause) {
        this.probableCause = probableCause;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(String repairDate) {
        this.repairDate = repairDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTrips() {
        return trips;
    }

    public void setTrips(String trips) {
        this.trips = trips;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public List<ServiceInformationDTO> getServiceInfoList() {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInformationDTO> serviceInfoList) {
        this.serviceInfoList = serviceInfoList;
    }
    public String getMealsExpense() {
    
      return mealsExpense;
    }
    public void setMealsExpense(String mealsExpense) {
    
      this.mealsExpense = mealsExpense;
    }
    public String getItemFreightAndDuty() {
    
      return itemFreightAndDuty;
    }
    public void setItemFreightAndDuty(String itemFreightAndDuty) {
    
      this.itemFreightAndDuty = itemFreightAndDuty;
    }

    public String getHandlingFee() {
    	
		return handlingFee;
	}
	public void setHandlingFee(String handlingFee) {
		
		this.handlingFee = handlingFee;
	}

	public String getTransportation() {
		return transportation;
	}

	public void setTransportation(String transportation) {
		this.transportation = transportation;
	}
}
