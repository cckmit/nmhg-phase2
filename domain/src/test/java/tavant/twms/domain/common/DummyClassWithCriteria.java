package tavant.twms.domain.common;

import tavant.twms.domain.claim.Criteria;

public class DummyClassWithCriteria {
	private Criteria forCriteria = new Criteria();

	public Criteria getForCriteria() {
		return this.forCriteria;
	}

	public void setForCriteria(Criteria forCriteria) {
		this.forCriteria = forCriteria;
	}
}
