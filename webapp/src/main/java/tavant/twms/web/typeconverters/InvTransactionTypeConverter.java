package tavant.twms.web.typeconverters;

import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryTransactionType;


public class InvTransactionTypeConverter extends NamedDomainObjectConverter<InventoryTransactionService, InventoryTransactionType> {
    public InvTransactionTypeConverter() {
        super("invTransactionService");
    }

    @Override
    public InventoryTransactionType fetchByName(String name)  {
        return getService().getTransactionTypeByName(name);
    }

    @Override
    public String getName(InventoryTransactionType transactionType) {
        return transactionType.getTrnxTypeValue();
    }
}
