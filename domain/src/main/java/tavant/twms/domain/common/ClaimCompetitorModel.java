package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("CLAIMCOMPETITORMODEL")
@Filters({
  @Filter(name="excludeInactive")
})
public class ClaimCompetitorModel extends ListOfValues {
	
	public ClaimCompetitorModel() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.ClaimCompetitorModel;
	}


}
