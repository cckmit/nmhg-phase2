package tavant.twms.domain.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 30, 2009
 * Time: 9:42:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CampaignSectionPrice {
    @Id
    @GeneratedValue(generator = "CampaignSectionPrice")
    @GenericGenerator(name = "CampaignSectionPrice",strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",parameters = {
            @Parameter(name = "sequence_name", value = "CAMPAIGN_SECTION_PRICE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20")})
    private Long id;

    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "price_per_unit_amt"),
	@Column(name = "price_per_unit_curr") })
	protected Money pricePerUnit;
    
    private String sectionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Money getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Money pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
}
