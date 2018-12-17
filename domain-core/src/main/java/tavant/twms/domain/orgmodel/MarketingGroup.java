package tavant.twms.domain.orgmodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "marketing_group")
public class MarketingGroup{

    @Id
	private String mktGrpCode;


	private String mktGrpName;

	public String getMktGrpCode() {
		return mktGrpCode;
	}

	public void setMktGrpCode(String mktGrpCode) {
		this.mktGrpCode = mktGrpCode;
	}

	public String getMktGrpName() {
		return mktGrpName;
	}

	public void setMktGrpName(String mktGrpName) {
		this.mktGrpName = mktGrpName;
	}

}
