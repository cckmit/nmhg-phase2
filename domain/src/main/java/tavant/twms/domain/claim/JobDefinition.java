/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Dec 20, 2006
 * Time: 3:02:20 PM
 */

package tavant.twms.domain.claim;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class JobDefinition implements AuditableColumns{

    @Id
    @AccessType("field")
    @GeneratedValue(generator = "JobDefinition")
	@GenericGenerator(name = "JobDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "JOB_DEFN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;

    private String code;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "labor_rate_amt"), @Column(name = "labor_rate_curr") })
    private Money laborRate;

    private BigDecimal expectedNumberOfHours;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSuffix() {
        String codeSuffix = this.code;

        int lastIndexOfSeparator = this.code.lastIndexOf(Assembly.FAULT_CODE_SEPARATOR);
        if (lastIndexOfSeparator != -1) {
            codeSuffix = this.code.substring(lastIndexOfSeparator + 1);
        }

        return codeSuffix;
    }

    public void setCodeSuffix(String jobCodeSuffix) {

        int lastIndexOfSeparator = this.code.lastIndexOf(Assembly.FAULT_CODE_SEPARATOR);
        if (lastIndexOfSeparator != -1) {
            String jobCodePrefix = this.code.substring(0, lastIndexOfSeparator + 1);
            this.code = jobCodePrefix + jobCodeSuffix;
        }
    }

    /**
     * @return the expectedNumberOfHours
     */
    public BigDecimal getExpectedNumberOfHours() {
        return this.expectedNumberOfHours;
    }

    /**
     * @param expectedNumberOfHours the expectedNumberOfHours to set
     */
    public void setExpectedNumberOfHours(BigDecimal expectedNumberOfHours) {
        this.expectedNumberOfHours = expectedNumberOfHours;
    }

    /**
     * @return laborRate the laborRate to set
     */
    public Money getLaborRate() {
        return this.laborRate;
    }

    /**
     * @param laborRate the laborRate to set
     */
    public void setLaborRate(Money laborRate) {
        this.laborRate = laborRate;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("name", this.name).append("job code", this.code)
                .append("labor rate", this.laborRate).append("expected number of hours",
                                                             this.expectedNumberOfHours).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}