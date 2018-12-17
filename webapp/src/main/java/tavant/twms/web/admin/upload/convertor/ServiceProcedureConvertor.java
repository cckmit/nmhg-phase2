/**
 *
 */
package tavant.twms.web.admin.upload.convertor;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

import java.util.List;

/**
 * @author kaustubhshobhan.b
 * 
 */
public class ServiceProcedureConvertor implements Convertor {

    private FailureStructureService failureStructureService;

    public Object convert(Object object) {
        String code = (String) object;
        // Super hacks in action
        Claim claim = (Claim) ConversionErrors.getInstance().get(ConversionErrors.CLAIM);

        List<ClaimedItem> claimedItems = claim.getClaimedItems();

        if (claimedItems.isEmpty()) {
            ConversionErrors.getInstance().addError("No Items specified.");
        }
        
        ItemReference itemReference = claimedItems.get(0).getItemReference();
        if ((itemReference == null) || (itemReference.getUnserializedItem() == null)) {
            ConversionErrors.getInstance().addError("Invalid Item");
        }

        ServiceProcedure serviceProcedure = null;
        try {
            serviceProcedure = this.failureStructureService.findServiceProcedure(itemReference
                    .getUnserializedItem(), code);
        } catch (RuntimeException e) {
           /*if((itemReference!=null) && (ConversionErrors.getInstance()!=null) ){
            ConversionErrors.getInstance().addError(
                    "Invalid service procedure code : [" + code + "] for item : [" + 
                    itemReference.getReferredInventoryItem().getSerialNumber() +"]");
           }else{
             ConversionErrors.getInstance().addError(
                 "Invalid service procedure code : [" + code + "]" );
           }*/
        }
        if (serviceProcedure == null) {
            ConversionErrors.getInstance().addError("Invalid service procedure code : [" + code +"]");
            ServiceProcedureDefinition def = new ServiceProcedureDefinition();
            def.setCode(code);
            serviceProcedure = new ServiceProcedure();
            serviceProcedure.setDefinition(def);
        }
        return serviceProcedure;
    }

    public Object convertWithDependency(Object object, Object dependency) {
        return new UnsupportedOperationException();
    }

    public void setFailureStructureService(FailureStructureService failureStructureService) {
        this.failureStructureService = failureStructureService;
    }

}
