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
package tavant.twms.fit.infra;

import fit.Parse;
import fitnesse.fixtures.TableFixture;

/**
 * @author vineeth.varghese
 * @date Oct 27, 2006
 */
public class TableFixtureWithNoRows extends TableFixture {

	/* (non-Javadoc)
	 * @see fitnesse.fixtures.TableFixture#doStaticTable(int)
	 */
	@Override
	protected void doStaticTable(int arg0) {
    //Do nothing
	}
	
	@Override
	public void doRows(Parse parse) {
		//Do nothing
	}


}
