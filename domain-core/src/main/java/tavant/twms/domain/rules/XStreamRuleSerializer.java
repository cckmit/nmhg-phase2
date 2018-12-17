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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author radhakrishnan.j
 * 
 */
public class XStreamRuleSerializer implements RuleSerializer {

	public XStreamRuleSerializer() {
	}

	public Object fromXML(String xml) {
		Object predicate = getSerializer().fromXML(xml);
		return predicate;
	}

	public String toXML(Object object) {
		return getSerializer().toXML(object);
	}

	public XStream getSerializer() {
		XStream xStream = new XStream(new DomDriver());
		xStream.useAttributeFor("name", String.class);
		xStream.useAttributeFor("category", String.class);
		xStream.useAttributeFor("description", String.class);
		xStream.useAttributeFor("type", String.class);
		xStream.useAttributeFor("literal", String.class);
		xStream.useAttributeFor("expression", String.class);
		xStream.useAttributeFor("context", String.class);
		xStream.useAttributeFor("beanName", String.class);
		xStream.useAttributeFor("methodName", String.class);
		xStream.useAttributeFor("id", Long.class);

		xStream.useAttributeFor(Boolean.TYPE);
		xStream.omitField(DomainPredicate.class, "predicateAsXML");
		xStream.omitField(DomainPredicate.class, "refersToPredicates");
		// added by jitesh.jain@tavant.com for Issue TWMS4.1-1851
		xStream.omitField(DomainPredicate.class, "d");
		// end of changes
		xStream.omitField(DomainRule.class, "predicateAsXML");
		xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
		
		Mapper mapper = xStream.getMapper();
		xStream.registerConverter(new CGLIBEnhancedConverterHibernateFix(mapper,xStream.getReflectionProvider()));

		Annotations.configureAliases(xStream, DomainRule.class,
				DomainPredicate.class, DomainSpecificVariable.class,
				Equals.class, NotEquals.class, Or.class, And.class,
				Constant.class, Constants.class, IsOneOf.class, Not.class,
				IsNoneOf.class, IsBefore.class, IsAfter.class,
				IsOnOrAfter.class, IsOnOrBefore.class, Any.class, All.class,
				IsAReturnWatchedPart.class, IsAReviewWatchedPart.class,
				ForEachOf.class, IsNotSet.class, GreaterThan.class,
				ForAnyOf.class, IsSet.class, MethodInvocationTarget.class,
				IsTrue.class, IsFalse.class, BelongsTo.class, DoesNotBelongTo.class,
				DateGreaterBy.class, DateLesserBy.class, IsSameAs.class,
				DateAtleastGreaterBy.class,DateAtmostGreaterBy.class,DateExactlyGreaterBy.class,
				DateAtleastLesserBy.class, DateAtmostLesserBy.class, DateExactlyLesserBy.class,
				DateNotGreaterBy.class, DateNotLesserBy.class);
		return xStream;
	}

}
