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
package tavant.twms.domain.claim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.failurestruct.AssemblyDefinition;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@PropertiesWithNestedCurrencyFields( { "serviceDetail","oemPartReplaced" })
public class ServiceInformation implements AuditableColumns{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    String faultCode;

    @OneToOne(fetch = FetchType.LAZY)
    FaultCode faultCodeRef;

    @ManyToOne(fetch = FetchType.LAZY)
    Item causalPart;

    @ManyToOne(fetch = FetchType.LAZY)
    FailureTypeDefinition faultFound;

    @ManyToOne(fetch = FetchType.LAZY)
    FailureCauseDefinition causedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    FailureRootCauseDefinition rootCause;

    @Column(nullable = true)
    private boolean supplierPartRecoverable = Boolean.FALSE;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private OEMPartReplaced oemPartReplaced;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    ServiceDetail serviceDetail = new ServiceDetail();

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item oemDealerCausalPart;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "service_info_fault_clm_attr")
    private List<ClaimAttributes> faultClaimAttributes = new ArrayList<ClaimAttributes>();

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "service_info_part_clm_attr")
    private List<ClaimAttributes> partClaimAttributes = new ArrayList<ClaimAttributes>();

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "THIRD_PARTY_LABOR_RATE_AMT"), @Column(name = "THIRD_PARTY_LABOR_RATE_CURR")})
    private Money thirdPartyLaborRate;

    @OneToOne
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private CustomReportAnswer customReportAnswer;

    public Money getThirdPartyLaborRate() {
        return thirdPartyLaborRate;
    }

    public void setThirdPartyLaborRate(Money thirdPartyLaborRate) {
        this.thirdPartyLaborRate = thirdPartyLaborRate;
    }

    /**
     * @return the causalPart
     */
    public Item getCausalPart() {
        return causalPart;
    }

    /**
     * @param causalPart the causalPart to set
     */
    public void setCausalPart(Item causalPart) {
        this.causalPart = causalPart;
    }

    /**
     * @return the faultCode
     */
    public String getFaultCode() {
        if (faultCodeRef != null && !StringUtils.hasText(faultCode)) {
            return faultCodeRef.getDefinition().getCode();
        }
        return faultCode;
    }

    public String getFaultCodeDescription() {
        String toReturn = "";
        if (faultCodeRef != null) {
            List<AssemblyDefinition> assemblyDefList = faultCodeRef.getDefinition().getComponents();
            if (assemblyDefList != null && assemblyDefList.size() > 0) {
                for (Iterator<AssemblyDefinition> iter = assemblyDefList.iterator(); iter.hasNext(); ) {
                    toReturn = toReturn + iter.next().getName();
                    if (iter.hasNext()) {
                        toReturn = toReturn + "-";
                    }
                }
            }
        }
        return toReturn;
    }

    /**
     * @param faultCode the faultCode to set
     */
    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public FaultCode getFaultCodeRef() {
        return faultCodeRef;
    }

    public void setFaultCodeRef(FaultCode faultCodeRef) {
        this.faultCodeRef = faultCodeRef;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the serviceDetail
     */
    public ServiceDetail getServiceDetail() {
        return serviceDetail;
    }

    /**
     * @param serviceDetail the serviceDetail to set
     */
    public void setServiceDetail(ServiceDetail serviceDetail) {
        this.serviceDetail = serviceDetail;
    }

    public FailureCauseDefinition getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(FailureCauseDefinition causedBy) {
        this.causedBy = causedBy;
    }

    public FailureTypeDefinition getFaultFound() {
        return faultFound;
    }

    public void setFaultFound(FailureTypeDefinition faultFound) {
        this.faultFound = faultFound;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", id).append("fault code", faultCode).append(
                "causal part", causalPart).append("fault found", faultFound).toString();
    }

    public OEMPartReplaced getOemPartReplaced() {
        return oemPartReplaced;
    }

    public void setOemPartReplaced(OEMPartReplaced oemPartReplaced) {
        this.oemPartReplaced = oemPartReplaced;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public boolean isSupplierPartRecoverable() {
        return supplierPartRecoverable;
    }

    public void setSupplierPartRecoverable(boolean supplierPartRecoverable) {
        this.supplierPartRecoverable = supplierPartRecoverable;
    }

    public Item getOemDealerCausalPart() {
        return oemDealerCausalPart;
    }

    public void setOemDealerCausalPart(Item oemDealerCausalPart) {
        this.oemDealerCausalPart = oemDealerCausalPart;
    }

    public List<ClaimAttributes> getFaultClaimAttributes() {
        return faultClaimAttributes;
    }

    public void setFaultClaimAttributes(List<ClaimAttributes> faultClaimAttributes) {
        this.faultClaimAttributes = faultClaimAttributes;
    }

    public void addFaultClaimAttributes(ClaimAttributes claimAttributes) {
        this.faultClaimAttributes.add(claimAttributes);
    }

    public void addPartClaimAttributes(ClaimAttributes claimAttributes) {
        this.partClaimAttributes.add(claimAttributes);
    }

    public List<ClaimAttributes> getPartClaimAttributes() {
        return partClaimAttributes;
    }

    public void setPartClaimAttributes(List<ClaimAttributes> partClaimAttributes) {
        this.partClaimAttributes = partClaimAttributes;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public FailureRootCauseDefinition getRootCause() {
        return rootCause;
    }

    public void setRootCause(FailureRootCauseDefinition rootCause) {
        this.rootCause = rootCause;
    }

    public CustomReportAnswer getCustomReportAnswer() {
        return customReportAnswer;
    }

    public void setCustomReportAnswer(CustomReportAnswer customReportAnswer) {
        this.customReportAnswer = customReportAnswer;
    }

    public ServiceInformation clone() {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.setFaultCode(faultCode);
        serviceInformation.setFaultCodeRef(faultCodeRef);
        serviceInformation.setCausalPart(causalPart);
        serviceInformation.setCausalBrandPart(causalBrandPart);
        serviceInformation.setFaultFound(faultFound);
        serviceInformation.setCausedBy(causedBy);
        serviceInformation.setRootCause(rootCause);
        serviceInformation.setSupplierPartRecoverable(supplierPartRecoverable);
        serviceInformation.setOemPartReplaced(oemPartReplaced);
        serviceInformation.setContract(contract);
        serviceInformation.setOemDealerCausalPart(oemDealerCausalPart);

        for(ClaimAttributes claimAttributes : this.faultClaimAttributes) {
            serviceInformation.getFaultClaimAttributes().add(claimAttributes.clone());
        }

        for(ClaimAttributes partClaimAttributes : this.partClaimAttributes) {
            serviceInformation.getFaultClaimAttributes().add(partClaimAttributes.clone());
        }

        serviceInformation.setThirdPartyLaborRate(thirdPartyLaborRate);
        serviceInformation.setCustomReportAnswer(customReportAnswer);
        serviceInformation.setServiceDetail(serviceDetail.clone());
        return serviceInformation;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    BrandItem causalBrandPart;

    String causalPartBrand;

    public BrandItem getCausalBrandPart() {
        return causalBrandPart;
    }

    public void setCausalBrandPart(BrandItem causalBrandPart) {
        this.causalBrandPart = causalBrandPart;
    }

    public String getCausalPartBrand() {
        return causalPartBrand;
    }

    public void setCausalPartBrand(String causalPartBrand) {
        this.causalPartBrand = causalPartBrand;
    }
}
