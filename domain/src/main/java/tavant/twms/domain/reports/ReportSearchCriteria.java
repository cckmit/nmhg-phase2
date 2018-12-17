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
package tavant.twms.domain.reports;

import java.util.Collection;
import java.util.TreeSet;

import com.domainlanguage.time.CalendarDate;

/**
 * 
 * Report Search criteria :holds the search criteria entered by the user.
 * 
 * @author bibin.jacob
 * 
 */
public class ReportSearchCriteria {

	Collection<Long> selectedDealers = new TreeSet<Long>();

	Collection<Long> selectedDealerGroups= new TreeSet<Long>();

	Collection<Long> selectedSuppliers = new TreeSet<Long>();
	
	String dealers;

	String dealerGroups;
	
	String suppliers;
	
	String groupBy;

	String orderBy;

	String order;
	
	CalendarDate startDate;

	CalendarDate endDate;

	public CalendarDate getEndDate() {
		return endDate;
	}

	public void setEndDate(CalendarDate endDate) {
		this.endDate = endDate;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public CalendarDate getStartDate() {
		return startDate;
	}

	public void setStartDate(CalendarDate startDate) {
		this.startDate = startDate;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDealers() {
		return dealers;
	}

	public void setDealers(String dealers) {
		this.dealers = dealers;
	}

	public Collection<Long> getSelectedDealers() {
		return selectedDealers;
	}

	public void setSelectedDealers(Collection<Long> selectedDealers) {
		this.selectedDealers = selectedDealers;
	}

	public String getDealerGroups() {
		return dealerGroups;
	}

	public void setDealerGroups(String dealerGroups) {
		this.dealerGroups = dealerGroups;
	}

	public Collection<Long> getSelectedDealerGroups() {
		return selectedDealerGroups;
	}

	public void setSelectedDealerGroups(Collection<Long> selectedDealerGroups) {
		this.selectedDealerGroups = selectedDealerGroups;
	}

	public void addDealer(Long dealer) {
		selectedDealers.add(dealer);
	}

	public void addSupplier(Long supplier) {
		selectedSuppliers.add(supplier);
	}

	public void removeDealers() {
		selectedDealers = new TreeSet<Long>();	}

	public void addDealerGroup(Long dealerGroup) {
		selectedDealerGroups.add(dealerGroup);
	}

	public Collection<Long> getSelectedSuppliers() {
		return selectedSuppliers;
	}

	public void setSelectedSuppliers(Collection<Long> selectedSuppliers) {
		this.selectedSuppliers = selectedSuppliers;
	}

	public String getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	
}
