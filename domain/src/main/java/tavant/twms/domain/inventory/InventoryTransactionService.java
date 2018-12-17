package tavant.twms.domain.inventory;

import java.util.Date;
import java.util.List;

import tavant.twms.infra.GenericService;

public interface InventoryTransactionService extends GenericService<InventoryTransaction, Long, Exception> {

    public InventoryTransactionType getTransactionTypeByName(String trnxType);
    public List<InventoryTransaction> getTransactionsOfDRAndD2d(final Date lstUpdateTime, final String bunsinesUnit);
}
