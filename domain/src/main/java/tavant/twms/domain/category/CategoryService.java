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
 * Date: Apr 5, 2007
 * Time: 12:58:57 AM
 */

package tavant.twms.domain.category;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;

@Transactional(readOnly = true)
public interface CategoryService extends
		GenericService<Category, Long, Exception> {

	public List<? extends Category> findAllCategoriesForABusinessObject(
			Class businessObjectClass);

	public List<DealerCategory> findAllDealerCategories();

	public List<ApplicablePolicyCategory> findAllApplicablePolicyCategories();

	public boolean isBusinessObjectInNamedCategory(Object businessObject,
			String categoryName);
	
	public boolean isBusinessObjectNotInNamedCategory(Object businessObject,
			String categoryName);

	public boolean isBusinessObjectInNamedCategory(
			List<Object> businessObjects, String categoryName, boolean forEach);
	
	public boolean isBusinessObjectNotInNamedCategory(
			List<Object> businessObjects, String categoryName, boolean forEach);

	// public boolean isDealerInNamedCategory(Dealership dealer,
	// String categoryName);
	//
	// public boolean isApplicablePolicyInNamedCategory(ApplicablePolicy policy,
	// String categoryName);

}
