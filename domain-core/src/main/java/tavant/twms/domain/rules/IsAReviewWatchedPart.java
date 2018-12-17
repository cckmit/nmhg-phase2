/**
 * 
 */
package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import static tavant.twms.domain.common.AdminConstants.ITEM_REVIEW_WATCHLIST;

/**
 * @author mritunjay.kumar
 * 
 */
@Configurable
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("isAReviewWatchedPart")
public class IsAReviewWatchedPart implements MethodInvocation, Predicate,
		Actionable {
	private DomainSpecificVariable domainVariable;

	private MethodInvocationTarget itemGroupService;

	private String methodName = "isPartInReviewWatchList";

	public IsAReviewWatchedPart() {
		super();
	}

	public IsAReviewWatchedPart(DomainSpecificVariable domainVariable) {
		super();
		Assert.notNull(domainVariable);
		this.domainVariable = domainVariable;
		itemGroupService = new MethodInvocationTarget("itemGroupService");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Visitable[] arguments() {
		return new Visitable[] { domainVariable };
	}

	public Visitable invokeOn() {
		return itemGroupService;
	}

	public String methodName() {
		return methodName;
	}

	public String returnType() {
		return Type.BOOLEAN;
	}

	public String getDomainTerm() {
		return "label.operators.isInPartsReviewWatchList";
	}

	public DomainSpecificVariable getDomainVariable() {
		return domainVariable;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public MethodInvocationTarget getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(MethodInvocationTarget itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public void setArguments(Visitable[] arguments) {
		domainVariable = (DomainSpecificVariable) arguments[0];
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
        return PredicateEvaluationActionFactory.getInstance().getActionForIsAReviewWatchedPart(getDomainVariable());
	}

}
