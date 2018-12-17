package tavant.twms.domain.inventory;

import java.util.Date;
import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;


public class InventoryTransactionServiceImpl extends
        GenericServiceImpl<InventoryTransaction, Long, Exception> implements
        InventoryTransactionService {

    InventoryTransactionRepository invTransactionRepository;

    @Override
    public GenericRepository<InventoryTransaction, Long> getRepository() {
        return this.invTransactionRepository;
    }

    public void setInvTransactionRepository(InventoryTransactionRepository invTransactionRepository) {
        this.invTransactionRepository = invTransactionRepository;
    }

    public InventoryTransactionType getTransactionTypeByName(String trnxType) {    	
        return this.invTransactionRepository.getTransactionTypeByName(trnxType);
    }
   
    public List<InventoryTransaction> getTransactionsOfDRAndD2d(final Date lstUpdateTime, final String businessUnit){
    	return this.invTransactionRepository.getTransactionsOfDRAndD2d(lstUpdateTime,businessUnit);
    }
}
