package tavant.twms.integration.layer.transformer.global;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import tavant.globalsync.foc.DataAreaType;
import tavant.globalsync.foc.FocDocument;
import tavant.globalsync.foc.HussmannPart;
import tavant.globalsync.foc.ReplacedPartConfiguration;
import tavant.globalsync.foc.FocDocument.Foc;
import tavant.twms.claim.FocBean;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;



public class FocClaimTransformer {
		 
	
	public FocDocument transform(String src) {
		FocDocument dto = null;
		if (!StringUtils.isBlank(src)) {
			try {
				dto = FocDocument.Factory.parse(src);
			} catch (XmlException e) {
				throw new RuntimeException("Error parsing xml",e);
			}
		}
		return dto;
	}
	
	public String transform(FocDocument focDocument){		
		return focDocument.xmlText(createXMLOptions());
	}
	
	
	public FocDocument convertBeanToDocument(FocBean focBean) {
		FocDocument focDocument = FocDocument.Factory.newInstance();
		focDocument.addNewFoc();
		Foc foc = focDocument.getFoc();				  
		foc.addNewDataArea();
		foc.getDataArea().setCausalPartNumber(String.valueOf(focBean.getCausalPart().getNumber()));
		foc.getDataArea().setCausedBy(focBean.getCausedBy().getId() != null ?
				String.valueOf(focBean.getCausedBy().getId()):"");
		
		int dayOfRepair = focBean.getRepairDate().breachEncapsulationOf_day();
		int monthOfRepair = focBean.getRepairDate().breachEncapsulationOf_month();
		int yearOfRepair = focBean.getRepairDate().breachEncapsulationOf_year();
		Calendar repairDate = new GregorianCalendar();
		repairDate.set(yearOfRepair, monthOfRepair-1, dayOfRepair);		
		foc.getDataArea().setRepairDate(repairDate);
		
		int dayOfFailure = focBean.getFailureDate().breachEncapsulationOf_day();
		int monthOfFailure = focBean.getFailureDate().breachEncapsulationOf_month();
		int yearOfFailure = focBean.getFailureDate().breachEncapsulationOf_year();
		Calendar failureDate = new GregorianCalendar();
		failureDate.set(yearOfFailure, monthOfFailure-1, dayOfFailure);		
		foc.getDataArea().setFailureDate(failureDate);
		
		foc.getDataArea().setFailureFound(String.valueOf(focBean.getFaultFound().getId()));
		foc.getDataArea().setCompanyName(focBean.getCompanyName());
		foc.getDataArea().setCompanyId(focBean.getCompanyId());
		foc.getDataArea().setFaultCode(String.valueOf(focBean.getFaultCodeRef().getId()));
		foc.getDataArea().setWorkOrderNumber(focBean.getWorkOrderNumber());
		foc.getDataArea().setOrderNo(focBean.getOrderNo());
		foc.getDataArea().setSerialNumber(focBean.getSerialNumber());
		foc.getDataArea().setServiceProviderNo(focBean.getServiceProviderNo());
		foc.getDataArea().setServiceProviderType(focBean.getServiceProviderType());
		
		ReplacedPartConfiguration[] replacedPartConfigurationArray = null;  
		List<ReplacedPartConfiguration> replacedConfigs = null; 
		if(focBean.getHussmanPartsReplacedInstalled() != null && focBean.getHussmanPartsReplacedInstalled().size() > 0 ){
			replacedConfigs = new ArrayList<ReplacedPartConfiguration>();
			
			for (HussmanPartsReplacedInstalled element : focBean.getHussmanPartsReplacedInstalled()) {
		
				List<HussmannPart> hussmannReplacedParts = new ArrayList<HussmannPart>();
				if(element.getReplacedParts() !=null && element.getReplacedParts().size() >0 ){										
					for (OEMPartReplaced replacedPart: element.getReplacedParts()) {						
						HussmannPart hussmannPart = HussmannPart.Factory.newInstance();
						hussmannPart.setPartnumber(replacedPart.getItemReference().getReferredItem().getNumber());
						if(replacedPart.getNumberOfUnits() != null){
							hussmannPart.setQuantity( BigInteger.valueOf(replacedPart.getNumberOfUnits()) );
						}
						hussmannReplacedParts.add(hussmannPart);
					}
				}	
					
				List<HussmannPart> hussmannInstalledParts = new ArrayList<HussmannPart>();
				if(element.getHussmanInstalledParts() !=null && element.getHussmanInstalledParts().size() >0 ){
					for (InstalledParts installedPart: element.getHussmanInstalledParts()) {
						HussmannPart hussmannPart = HussmannPart.Factory.newInstance();
						hussmannPart.setPartnumber(installedPart.getItem().getNumber());
						if(installedPart.getNumberOfUnits() !=null){
							hussmannPart.setQuantity( BigInteger.valueOf(installedPart.getNumberOfUnits()));
						}
						hussmannInstalledParts.add(hussmannPart);												
					}
				}	
				ReplacedPartConfiguration replacedPartConfiguration = ReplacedPartConfiguration.Factory.newInstance();
					replacedPartConfiguration.setHussmannInstalledPartsArray( 
							hussmannInstalledParts.toArray(new HussmannPart[hussmannInstalledParts.size()]));
					replacedPartConfiguration.setHussmannReplacedPartsArray( 
							hussmannReplacedParts.toArray(new HussmannPart[hussmannReplacedParts.size()]));					
					replacedConfigs.add(replacedPartConfiguration);
				
				
			}
		}
		if(replacedConfigs !=null){
			replacedPartConfigurationArray = replacedConfigs.toArray(new ReplacedPartConfiguration[replacedConfigs.size()]);
	 		foc.getDataArea().setReplacedPartConfigsArray(replacedPartConfigurationArray);
		}
		foc.addNewApplicationArea();
		foc.getApplicationArea().setCreationDateTime(new GregorianCalendar());
		
		return focDocument;
	}
	
	public FocBean covertDocumentToBean(FocDocument focDocument){
		FocBean focBean = new FocBean();		
		if (focDocument != null && focDocument.getFoc() != null
				&& focDocument.getFoc().getDataArea() != null) {
			
			DataAreaType dataArea = focDocument.getFoc().getDataArea();
			
			focBean.getCausalPart().setNumber(dataArea.getCausalPartNumber());
		
			if (!StringUtils.isBlank(dataArea.getCausedBy())) {
					focBean.getCausedBy().setId(
							Long.parseLong(dataArea.getCausedBy()));
			}
			
			if(!StringUtils.isBlank(dataArea.getFaultCode())){
					focBean.getFaultCodeRef().setId(Long.parseLong(dataArea.getFaultCode()));
			}
			
			if(!StringUtils.isBlank(dataArea.getFailureFound())){
					focBean.getFaultFound().setId(Long.parseLong(dataArea.getFailureFound()));
			}
			
			
			focBean.setCompanyId(dataArea.getCompanyId());
			
			focBean.setCompanyName(dataArea.getCompanyName());
			
			if(dataArea.getFailureDate() !=null){
				focBean.setFailureDate(CalendarDate.from(TimePoint.from(dataArea.getFailureDate()),Clock.defaultTimeZone()));
			}
			
			focBean.setOrderNo(dataArea.getOrderNo());
			
			if(dataArea.getRepairDate()!=null){
				focBean.setRepairDate(CalendarDate.from(TimePoint.from(dataArea.getRepairDate()),Clock.defaultTimeZone()));
			}
			focBean.setSerialNumber(dataArea.getSerialNumber());	
			focBean.setServiceProviderNo(dataArea.getServiceProviderNo());
			focBean.setServiceProviderType(dataArea.getServiceProviderType());
			focBean.setWorkOrderNumber(dataArea.getWorkOrderNumber());
			
			List<HussmanPartsReplacedInstalled> list = null;
			if(dataArea.getReplacedPartConfigsArray() !=null && dataArea.getReplacedPartConfigsArray().length >0){
				int length = dataArea.getReplacedPartConfigsArray().length ;
				list = new ArrayList<HussmanPartsReplacedInstalled>();
				
				for (int i = 0; i < length; i++) {					
					ReplacedPartConfiguration replacedPartConfiguration =dataArea.getReplacedPartConfigsArray()[i];
					HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled = new HussmanPartsReplacedInstalled();
					
					if(replacedPartConfiguration!=null && replacedPartConfiguration.getHussmannInstalledPartsArray() !=null
							&& replacedPartConfiguration.getHussmannInstalledPartsArray().length > 0){
					 List<InstalledParts> installedParts = new ArrayList<InstalledParts>(replacedPartConfiguration.getHussmannInstalledPartsArray().length);					 
					 for (HussmannPart hussmannInstalledPart : replacedPartConfiguration.getHussmannInstalledPartsArray()) {					
						 InstalledParts ipart = new InstalledParts();
						 ipart.setNumberOfUnits(hussmannInstalledPart.getQuantity().intValue());
						 /*ipart.setPartNumber(hussmannInstalledPart.getPartnumber());*/
		                 Item item = new Item();
		                 item.setNumber(hussmannInstalledPart.getPartnumber());
                         ipart.setItem(item); 
						 installedParts.add(ipart);
					 }
					 hussmanPartsReplacedInstalled.setHussmanInstalledParts(installedParts);
					}
					
					
					if(replacedPartConfiguration!=null && replacedPartConfiguration.getHussmannReplacedPartsArray() !=null
							&& replacedPartConfiguration.getHussmannInstalledPartsArray().length > 0){
					 List<OEMPartReplaced> replacedParts = new ArrayList<OEMPartReplaced>(replacedPartConfiguration.getHussmannReplacedPartsArray().length);					 
					 for (HussmannPart hussmannReplacedPart : replacedPartConfiguration.getHussmannReplacedPartsArray()) {					
						 OEMPartReplaced rpart = new OEMPartReplaced();
						 rpart.setNumberOfUnits(hussmannReplacedPart.getQuantity().intValue());						 
		                 ItemReference itemReference = new ItemReference();
		                 Item item = new Item();
		                 item.setNumber(hussmannReplacedPart.getPartnumber());
		                 itemReference.setReferredItem(item);
		                 rpart.setItemReference(itemReference);
						 replacedParts.add(rpart);
					 }
					 hussmanPartsReplacedInstalled.setReplacedParts(replacedParts);
					}
					
					
					list.add(hussmanPartsReplacedInstalled);
				}
				
				
			}
			
			focBean.setHussmanPartsReplacedInstalled(list);
			
		}
		
		
		return focBean;
	}
	
	private static XmlOptions createXMLOptions() {
		// Generate the XML document
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setUseDefaultNamespace();
		return xmlOptions;
	}
	
	
    
}
