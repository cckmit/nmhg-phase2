/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.domain.partreturn;

import java.util.List;
import java.util.Map;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.TypedQueryParameter;

/**
 * @author roopali.agrawal
 */
public interface PartReturnRepository extends GenericRepository<PartReturn, Long> {
    public PageResult<PartReturnClaimSummary> findPartReturnsUsingDynamicQuery(final String queryWithoutSelect,
                                                                               final String orderByClause, final String selectClause, PageSpecification pageSpecification,
                                                                               final List<TypedQueryParameter> parameterMap, final Map<String, Object> paramsMap);

    public PageResult<PartReturnClaimSummary> findAllClaimsMatchingCriteria(
            final PartReturnSearchCriteria partReturnSearchCriteria);

    public List<Claim> getAllDraftClaims();

    public List<Claim> forwardedClaimCrossedOverDueDays(
            int forwardedOverdueDate);

    public List<String> findAllStatusForPartReturn();

    public List<String> findAllLocationsForPartReturn();

    public List<Claim> fetchShipmentGeneratedClaimsCrossedWindowPeriodDays(int windowPeriodDays);

    public void updatePartReturnConfiguration(PartReturnConfiguration partReturnConfiguration);
    
    
}