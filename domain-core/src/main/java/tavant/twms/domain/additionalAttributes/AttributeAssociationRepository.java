/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepository;


/**
 * @author pradipta.a
 */
public interface AttributeAssociationRepository extends GenericRepository<AttributeAssociation, Long>{

	public List<AttributeAssociation> findAttributesForItemGroups(final List<ItemGroup> itemGroups,AttributePurpose attributePurpose);

    public List<AttributeAssociation> findAttributesForSupplier(final long id);
    
    public List<AdditionalAttributes> findAttributesForClaim(final long id);

    public List<AttributeAssociation> findAttributesForPart(final long id);

    public List<AttributeAssociation> findAttributesForFaultCode(final long id, final long modelId);

    public List<AttributeAssociation> findAttributesForJobCode(final long id);
    
    public Boolean isAnyAttributeConfiguredForBU();
    
    public Boolean isAnyAttributeConfiguredForSupplier();

}
