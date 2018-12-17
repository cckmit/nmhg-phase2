package tavant.twms.infra;

import java.util.List;

import ognl.Ognl;
import ognl.OgnlException;

import tavant.twms.domain.common.CriteriaElement;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.common.CriteriaEvaluationPrecedenceRepository;

public class RelevanceScoreComputerServiceImpl implements
		RelevanceScoreComputerService {

	private CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository;

	public long computeRelevanceScore(String entityAlias, Object obj) {

		CriteriaEvaluationPrecedence criteriaEvaluationPrecedence = criteriaEvaluationPrecedenceRepository
				.findByName(entityAlias);
		List<CriteriaElement> elements = criteriaEvaluationPrecedence
				.getProperties();
		boolean[] bits = new boolean[elements.size()];
		int index = 0;
		boolean isValueSet = false;
		for (CriteriaElement element : elements) {
			isValueSet = false;
			try {
				Object val = Ognl.getValue(element.getPropertyExpression(), obj);
				if (val == null) {
					// isValueSet is already false					
				} else if ("ALL".equalsIgnoreCase(val.toString())) {
					// this should take care of enums also
						isValueSet = false;
				} else {
					isValueSet = true;
				}

			} catch (OgnlException e) {
			}
			bits[index] = isValueSet;
			index++;
		}

		return new BitSetValueComputer().compute(bits);
	}

	public void setCriteriaEvaluationPrecedenceRepository(
			CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository) {
		this.criteriaEvaluationPrecedenceRepository = criteriaEvaluationPrecedenceRepository;
	}

}
