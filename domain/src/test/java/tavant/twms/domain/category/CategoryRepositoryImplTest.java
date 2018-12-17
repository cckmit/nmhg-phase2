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
 * Time: 1:59:22 AM
 */

package tavant.twms.domain.category;

import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.infra.DomainRepositoryTestCase;

public class CategoryRepositoryImplTest extends DomainRepositoryTestCase {

    private CategoryRepository categoryRepository;

    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void testFindAll() {
        assertEquals(4, categoryRepository.findAll().size());
    }

    public void testFindAllDealerCategories() {
        assertEquals(2, categoryRepository.findAllDealerCategories().size());
    }

    public void testIsDealerInNamedCategoryForExistingCategoryName() {
        String categoryName = "Frequent Claimants";
        Dealership dealer = new Dealership();

        dealer.setId(7L);
        assertTrue(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));

        dealer.setId(10L);
        assertTrue(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));

        dealer.setId(25L);
        assertFalse(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));
    }

    public void testIsDealerInNamedCategoryForNonExistantCategoryName() {
        String categoryName = "FooBar";
        Dealership dealer = new Dealership();

        dealer.setId(7L);
        assertFalse(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));

        dealer.setId(10L);
        assertFalse(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));

        dealer.setId(25L);
        assertFalse(categoryRepository.isDealerInNamedCategory(dealer,
                categoryName));
    }
}
