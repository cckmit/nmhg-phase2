package tavant.twms.domain.inventory;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import tavant.twms.infra.i18n.I18NBaseText;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="I18NDISCLAIMER_TEXT")
public class I18NWaiverText extends I18NBaseText {

    @Id
    @GeneratedValue(generator = "I18NWaiverText")
    @GenericGenerator(name = "I18NWaiverText", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "I18n_Waiver_Text_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
