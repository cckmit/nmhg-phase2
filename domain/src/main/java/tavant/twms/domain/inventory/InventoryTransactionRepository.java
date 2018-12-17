package tavant.twms.domain.inventory;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.BookingsReport;
import tavant.twms.infra.GenericRepository;

public interface InventoryTransactionRepository extends GenericRepository<InventoryTransaction, Long>{

    public InventoryTransactionType getTransactionTypeByName(String trnxType);
    
    public List<InventoryTransaction> getTransactionsOfDRAndD2d(final Date lstUpdateTime,final String businessUnit);
    
	@Transactional(readOnly = true)
	public BookingsReport findLastReportingTime();
	
	@Transactional(readOnly = false)
	public void save(BookingsReport  BookingsReport  );

}

