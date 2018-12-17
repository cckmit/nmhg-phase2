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
package tavant.twms.domain.claim.validation;

import org.hibernate.validator.InvalidValue;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author vineeth.varghese
 *
 */
public class ClaimValidationTest extends DomainRepositoryTestCase {
	
	ValidationService validationService;
	
	public void testForClaimWithRepairDateAfterFailureDate() {
		Claim claim = new PartsClaim();
		claim.setFailureDate(CalendarDate.date(2006,1,1));
		claim.setRepairDate(CalendarDate.date(2006,1,1));
		InvalidValue[] invalidValues = 
			validationService.getInvalidValuesFor(claim);
		assertEquals(0, invalidValues.length);
	}
	
	public void testForClaimWithRepairDateBeforeFailureDate() {
		Claim claim = new PartsClaim();
		claim.setFailureDate(CalendarDate.date(2006,1,2));
		claim.setRepairDate(CalendarDate.date(2006,1,1));
		InvalidValue[] invalidValues = 
			validationService.getInvalidValuesFor(claim);
		assertEquals(0, invalidValues.length);
	}
	
	public void testForClaimWithFutureRepairDate() {
		Claim claim = new PartsClaim();
		claim.setFailureDate(CalendarDate.date(2007,1,2));
		claim.setRepairDate(CalendarDate.date(2007,10,10));
		InvalidValue[] invalidValues = 
			validationService.getInvalidValuesFor(claim);
		assertEquals(0, invalidValues.length);
	}
	
	public void testForClaimWithFutureFailureDate() {
		Claim claim = new PartsClaim();
		claim.setFailureDate(Clock.today().plusDays(5));
		claim.setRepairDate(Clock.today().plusDays(10));
		InvalidValue[] invalidValues = 
			validationService.getInvalidValuesFor(claim);
		assertEquals(0, invalidValues.length);
	}
	
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

}
