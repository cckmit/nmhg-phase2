package tavant.twms.domain.reports;

import java.util.List;

import tavant.twms.domain.claim.Claim;

public interface ClaimReportRepository {

	/**
	 * Find all claims given dealer, startDate,endDate
	 *
	 * @param ReportSearchCriteria
	 * @return List Claim
	 */
	@SuppressWarnings("unchecked")
	public List<Claim> findAllClaimsForCriteria(
			final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find all claims between system date and systemdate-12months
	 *
	 * @return List Claim
	 */

	@SuppressWarnings("unchecked")
	public List findClaimsForProcessingEfficiency();

	/**
	 * Find dealers count
	 *
	 * @return Long count
	 */

	@SuppressWarnings("unchecked")
	public List findDealersCount(final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find all PartReturns
	 *
	 * @return List PartReturns
	 */

	@SuppressWarnings("unchecked")
	public List findPartReturns(final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find all Due PartReturns
	 *
	 * @return List PartReturns
	 */

	@SuppressWarnings("unchecked")
	public List findAllDuePartReturns(
			final ReportSearchCriteria reportSearchCriteria);

	@SuppressWarnings("unchecked")
	public List findSupplierRecovery(
			final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find Claims By Product
	 *
	 * @return List claims
	 */
	@SuppressWarnings("unchecked")
	public List findClaimsByProduct(
			final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find Claims By Fault
	 *
	 * @return List claims
	 */
	// TODO:Remove later
	@SuppressWarnings("unchecked")
	public List findClaimsByFaultNEW(
			final ReportSearchCriteria reportSearchCriteria);

	public List findClaimsByFault(
			final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find Warranty Payout
	 *
	 * @return List claims
	 */
	@SuppressWarnings("unchecked")
	public List findWarrantyPayout(
			final ReportSearchCriteria reportSearchCriteria);

	/**
	 * Find Tax Amount
	 *
	 * @return List claims
	 */
	@SuppressWarnings("unchecked")
	public List findTaxAmount();

}
