package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("RECOVERYCLAIMCANNOTRECOVERREASON")
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name="rec_clm_cnt_recover_reason")
public class RecoveryClaimCannotRecoverReason extends ListOfValues{

	public RecoveryClaimCannotRecoverReason(){
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.RecoveryClaimCannotRecoverReason;
	}
}
