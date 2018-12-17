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
package tavant.twms.domain.common;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author radhakrishnan.j
 *
 */
class RelevanceScore implements Comparable<RelevanceScore> {
    private BigDecimal score = new BigDecimal(-1D,MathContext.DECIMAL32);
    public static final RelevanceScore IRRELEVANT = new RelevanceScore(-1D);

    public RelevanceScore(double d) {
    	this(new BigDecimal(d,MathContext.DECIMAL32));
    }

    public RelevanceScore(BigDecimal score) {   
    	super();
        this.score = score;
    }    
    
    public BigDecimal value() {
        return score;
    }

    public boolean isIrrelevant() {
        return !isRelevant();
    }
    
    public boolean isRelevant() {
        return score.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    @Override
    public String toString() {
        return score.toString();
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RelevanceScore o) {
		return score.compareTo(o.score);
	}
    
}
