package tavant.twms.domain.reports;

import java.util.List;

import tavant.twms.domain.claim.ClaimState;

public class ReportVO {
	//TODO:Replace Transformers.aliasToBean to Map in claimreportrepository so that the VO can be removed.
	public String dealerGroupName;
	public String month;
	public int year;
	public long accepted;
	public long rejected;
	public ClaimState state;
	//TODO:use single list
	public List claims;
	public List subReports;
	public List supplierRecovery;
	public double count;
	public String orderBy;
	public List getClaims() {
		return claims;
	}
	public void setClaims(List claims) {
		this.claims = claims;
	}
	public String getDealerGroupName() {
		return dealerGroupName;
	}
	public void setDealerGroupName(String dealerGroupName) {
		this.dealerGroupName = dealerGroupName;
	}
	public ClaimState getState() {
		return state;
	}
	public void setState(ClaimState state) {
		this.state = state;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List getSubReports() {
		return subReports;
	}
	public void setSubReports(List subReports) {
		this.subReports = subReports;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public long getAccepted() {
		return accepted;
	}
	public void setAccepted(long accepted) {
		this.accepted = accepted;
	}
	public long getRejected() {
		return rejected;
	}
	public void setRejected(long rejected) {
		this.rejected = rejected;
	}
	public List getSupplierRecovery() {
		return supplierRecovery;
	}
	public void setSupplierRecovery(List supplierRecovery) {
		this.supplierRecovery = supplierRecovery;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}
