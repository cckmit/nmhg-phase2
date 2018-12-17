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
package tavant.twms.domain.claim.payment.definition;

import java.util.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class PaymentSection implements AuditableColumns, BUSpecificSectionNames {
    private static final Logger logger = Logger.getLogger(PaymentSection.class);

    @Id
	@GeneratedValue(generator = "PaymentSection")
	@GenericGenerator(name = "PaymentSection", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PAYMENT_SECTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "payment_section_var_levels")
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    
    private List<PaymentVariableLevel> paymentVariableLevels = new ArrayList<PaymentVariableLevel>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;
    
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

    public List<PaymentVariableLevel> getPaymentVariableLevels() {
        return this.paymentVariableLevels;
    }

    public void setPaymentVariableLevels(List<PaymentVariableLevel> paymentVariableLevels) {
        this.paymentVariableLevels = paymentVariableLevels;
    }

    public boolean addPaymentVariableLevel(PaymentVariableLevel paymentVariableLevel) {
        return this.paymentVariableLevels.add(paymentVariableLevel);
    }

    public Section getSection() {
        return this.section;
    }

    public void setSection(Section sections) {
        this.section = sections;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("section", this.section)
                .append("paymentVariables", this.paymentVariableLevels).toString();
    }

   /* public Map<String, String> getPrettyPrintLineItems() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(this.section.getName(), this.section.getName());
        LevelMap levelVariables = new LevelMap();
        levelVariables.put(0, this.section.getName());
        for (PaymentVariableLevel pvl : getSortedPaymentVariableLevels()) {
            PaymentVariable paymentVariable = pvl.getPaymentVariable();
            String amountValueAsFormula = paymentVariable.getAmountName() + " * "
                    + levelVariables.getPreviousValue(pvl.getLevel());
            if(logger.isDebugEnabled())
            {
                logger.debug("Formula is [" + amountValueAsFormula + "]");
            }
            // alter the levels name
            int levelToChange = levelVariables.getClosestKeyLessThanOrEqualTo(pvl.getLevel());
            String oldAmount = levelVariables.get(levelToChange).replaceAll("[(]", "")
                    .replaceAll("[)]", "").trim();
            String currentLevelAmountName = "( " + oldAmount + " + " + paymentVariable.getName()
                    + " )";
            if(logger.isDebugEnabled())
            {
                logger.debug("Changing the level [" + pvl.getLevel() + "] to be ["
                    + currentLevelAmountName + "]");
            }
            levelVariables.put(pvl.getLevel(), currentLevelAmountName);
            values.put(paymentVariable.getName(), amountValueAsFormula);
        }
        return values;
    }*/

    static class LevelMap extends HashMap<Integer, String> {

        private static final long serialVersionUID = 1L;

        Integer getPreviousKey(Integer key) {
            SortedSet<Integer> keys = new TreeSet<Integer>(keySet());
            if(logger.isDebugEnabled())
            {
                logger.debug("Keys are " + keys);
            }
            SortedSet<Integer> keysLessThanChosenKey = keys.headSet(key);
            if(logger.isDebugEnabled())
            {
                logger.debug("Keys less than [" + key + "] are " + keysLessThanChosenKey);
            }
            return keysLessThanChosenKey.isEmpty() ? null : keysLessThanChosenKey.last();
        }

        public Integer getClosestKeyLessThanOrEqualTo(Integer key) {
            return containsKey(key) ? key : getPreviousKey(key);
        }

        public String getPreviousValue(Integer key) {
            return get(getPreviousKey(key));
        }
    }

    @SuppressWarnings("unchecked")
    public List<PaymentVariableLevel> getSortedPaymentVariableLevels() {
        List<PaymentVariableLevel> paymentVarLevels = new ArrayList<PaymentVariableLevel>();
        paymentVarLevels.addAll(getPaymentVariableLevels());
        Collections.sort((paymentVarLevels), getComparatorForLevel());
        return paymentVarLevels;
    }


    private Comparator getComparatorForLevel() {
        Comparator comparator;
        comparator = new Comparator() {
            public int compare(Object obj1, Object obj2) {

                PaymentVariableLevel param1 = (PaymentVariableLevel) obj1;
                PaymentVariableLevel param2 = (PaymentVariableLevel) obj2;
                return new CompareToBuilder().append(param1.getLevel(), param2.getLevel())
                        .append(param1.getPaymentVariable().getName(),
                                param2.getPaymentVariable().getName()).toComparison();
            }
        };
        return comparator;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getI18NMessageKey(String name){
        return NAMES_AND_KEY.get(name);
    }

    public Map<Integer,List<PaymentVariableLevel>> getPaymentVariablesForLevels(){
      Map<Integer,List<PaymentVariableLevel>> paymentVariablesForLevels = new TreeMap<Integer,List<PaymentVariableLevel>>();
      for (PaymentVariableLevel paymentVariableLevel :getSortedPaymentVariableLevels()) {
          List<PaymentVariableLevel> sameLevelVariables = new ArrayList<PaymentVariableLevel>();
            for (PaymentVariableLevel innerPaymentVariableLevel :getSortedPaymentVariableLevels()) {
                if(paymentVariableLevel.getLevel().intValue()==innerPaymentVariableLevel.getLevel().intValue()){
                    sameLevelVariables.add(innerPaymentVariableLevel);
                }
            }
          paymentVariablesForLevels.put(paymentVariableLevel.getLevel(),sameLevelVariables);
        }
        return paymentVariablesForLevels;
    }

}
