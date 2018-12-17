package tavant.twms.domain.claim;

import org.springframework.util.StringUtils;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

public class OEMPartReplacedValidator extends ExpressionValidator {

    private InventoryService inventoryService;

    @Override
    public void validate(Object object) throws ValidationException {

        OEMPartReplaced partReplaced = (OEMPartReplaced) object;
        // Validate the newSerialNumber for the replaced OEM part.
        validateNewSerialNumberForReplacedOemPart(partReplaced);
    }
    
    
    

    private void validateNewSerialNumberForReplacedOemPart(OEMPartReplaced partReplaced) {

        ValueStack ognlStack = ActionContext.getContext().getValueStack();
        Claim claim = (Claim) ognlStack.findValue(getExpression());

        if (partReplaced.getItemReference().isSerialized()) {
            if (InstanceOfUtil.isInstanceOfClass( PartsClaim.class, claim)) {
                if (!(new HibernateCast<PartsClaim>().cast(claim)).getPartInstalled().booleanValue()) {
                    return;
                }
            }

            String serialNumberOfNewPart = partReplaced.getSerialNumberOfNewPart();
            if (StringUtils.hasText(serialNumberOfNewPart)) {
                try {
                    InventoryItem newInventoryItem = this.inventoryService.findSerializedItem(serialNumberOfNewPart
                            .trim());
                    // Item exists. Match with the replaced item. If no match
                    // found, display an error message.
                    if (!newInventoryItem.getOfType().equals(
                            partReplaced.getItemReference().getReferredInventoryItem().getOfType())) {
                        getValidatorContext().addActionError("error.newClaim.invalidPartType");
                    }
                } catch (ItemNotFoundException e) {
                    // TODO: Inventory item does not exist, need to create one.

                    // HOW DO WE HANDLE THIS FOR NOW??
                }
            } 
            // TO-Do to Revisit Later
/*            else {
                getValidatorContext().addActionError("error.newClaim.serialNoNewPartRequired");
                return;
            }
*/
        }

    }



    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
}
