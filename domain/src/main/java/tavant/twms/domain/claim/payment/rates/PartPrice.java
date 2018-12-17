package tavant.twms.domain.claim.payment.rates;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.TimeBoundValue;

import com.domainlanguage.money.Money;

@Entity
@Filters({
	  @Filter(name="excludeInactive")
	})
public class PartPrice extends TimeBoundValue<Money>  {
	 @Id
	    @GeneratedValue(generator = "PartPrice")
		@GenericGenerator(name = "PartPrice", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
				@Parameter(name = "sequence_name", value = "PART_PRICE_SEQ"),
				@Parameter(name = "initial_value", value = "1000"),
				@Parameter(name = "increment_size", value = "20") })
	    private Long id;

	    @Version
	    private int version;
	    
	    @OneToMany(fetch = FetchType.LAZY)
		@Cascade( { org.hibernate.annotations.CascadeType.ALL,
				org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		@JoinColumn(name = "PART_PRICE", nullable = false)
		private List<PartPriceValues> partPriceValues = new ArrayList<PartPriceValues>();
	    
	    @ManyToOne
	    private PartPrices partPrices;
	    
	    public PartPrice() {
	        super();
	    }

	       public Long getId() {
	        return this.id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public int getVersion() {
	        return this.version;
	    }

	    public void setVersion(int version) {
	        this.version = version;
	    }

	   

	
		public List<PartPriceValues> getPartPriceValues() {
			return partPriceValues;
		}

		public void setPartPriceValues(List<PartPriceValues> partPriceValues) {
			this.partPriceValues = partPriceValues;
		}

		public PartPrices getPartPrices() {
			return partPrices;
		}

		public void setPartPrices(PartPrices partPrices) {
			this.partPrices = partPrices;
		}

		@Override
		public Money getValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setParent(Object parent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setValue(Money newValue) {
			// TODO Auto-generated method stub
			
		}

		
}
