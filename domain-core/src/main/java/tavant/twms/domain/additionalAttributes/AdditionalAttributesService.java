package tavant.twms.domain.additionalAttributes;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly = true)
public interface AdditionalAttributesService {

    public PageResult<AdditionalAttributes> findAttributesForPurpose(String purpose,
            ListCriteria criteria);

    public AdditionalAttributes findAdditionalAttributes(long id);
    
    public AdditionalAttributes findAdditionalAttributeByNameForPurpose(String name, AttributePurpose purpose);

    @Transactional(readOnly = false)
    public void saveAdditionalAttribute(AdditionalAttributes additionalAttributes);

    @Transactional(readOnly = false)
    public void updateAdditionalAttribute(AdditionalAttributes additionalAttributes);
    
	public List<AdditionalAttributes> findAddAttributeByPurpose(AttributePurpose purpose);
	
	public List<AdditionalAttributes> findAttributesForEquipment (long id);
	
}
