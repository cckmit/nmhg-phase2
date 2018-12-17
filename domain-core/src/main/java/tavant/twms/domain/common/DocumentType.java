package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DOCUMENTTYPE")
@Filters({
  @Filter(name="excludeInactive")
})

public class DocumentType extends ListOfValues{
	@Transient
    private String name;
	
	public DocumentType() {
		super();
	}
	
    public String getName() {
        return getDescription();
    }
	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.DocumentType;
	}
}
