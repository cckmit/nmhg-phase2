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
 * Time: 11:25:46 AM
 */

package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Configurable
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("belongsTo")
public class BelongsTo implements MethodInvocation, Predicate, Actionable {

	private DomainSpecificVariable domainVariable;
	private Value watchedList;

	private MethodInvocationTarget categoryService;

	private String methodName = "isBusinessObjectInNamedCategory";

	public BelongsTo() {
		super();
	}

	public BelongsTo(DomainSpecificVariable domainVariable, Value watchedList) {
		super();
		Assert.notNull(domainVariable);
		Assert.notNull(watchedList);
		this.domainVariable = domainVariable;
		this.watchedList = watchedList;
		categoryService = new MethodInvocationTarget("categoryService");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Visitable[] arguments() {
		return new Visitable[] { domainVariable, watchedList };
	}

	public Visitable invokeOn() {
		return categoryService;
	}

	public String methodName() {
		return methodName;
	}

	public String returnType() {
		return Type.BOOLEAN;
	}

	public String getDomainTerm() {
		return "label.operators.belongsTo";
	}

	public DomainSpecificVariable getDomainVariable() {
		return domainVariable;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public Value getWatchedList() {
		return watchedList;
	}

	public void setWatchedList(Value watchedList) {
		this.watchedList = watchedList;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public MethodInvocationTarget getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(MethodInvocationTarget categoryService) {
		this.categoryService = categoryService;
	}

	public void setArguments(Visitable[] arguments) {
		domainVariable = (DomainSpecificVariable) arguments[0];
		watchedList = (Value) arguments[1];
	}

	public void setInvokeOn(Visitable visitable) {
	}

	public String getType() {
		return returnType();
	}

	public boolean isCollection() {
		return false;
	}

	public Predicate getInverse() {
		throw new UnsupportedOperationException(
				"Method getInverse() is not supported for "
						+ this.getClass().getName());
	}

	public PredicateEvaluationAction getAction() {
        return PredicateEvaluationActionFactory.getInstance().getActionForBelongsTo(getDomainVariable(), getWatchedList());
	}
}
