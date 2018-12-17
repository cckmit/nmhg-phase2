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
package tavant.twms.domain.rules;

import java.text.SimpleDateFormat;

import com.domainlanguage.time.CalendarDate;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import tavant.twms.dateutil.TWMSDateFormatUtil;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@XStreamAlias("constant")
@Inheritance
public class Constant implements Value, ExpressionToken {
	private String literal;

	private String type;

	public Constant(String literal, String typeName) {
		super();
		if (DateType.DATE == typeName) {
			CalendarDate calendarDate = CalendarDate.from(literal,
					TWMSDateFormatUtil.getDateFormatForLoggedInUser());

			this.literal = calendarDate
					.toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
		} else {
			this.literal = literal;
		}
		this.type = typeName;
	}

	public Constant() {
	}

	public String getType() {
		return type;
	}

	public boolean isCollection() {
		return false;
	}

	public String getToken() {
		return literalType().getEvaluableExpression(literal);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public void validate(ValidationContext validationContext) {
		if (!literalType().isLiteralValid(literal)) {
			validationContext.addError(" incompatible literal [" + literal
					+ "] for type [" + type + "]");
		}
	}

	private LiteralSupport literalType() {
		return ((LiteralSupport) TypeSystem.getInstance().getType(type));
	}

	@Override
	public String toString() {
		return literal;
	}

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public void setType(String type) {
		this.type = type;
	}

}
