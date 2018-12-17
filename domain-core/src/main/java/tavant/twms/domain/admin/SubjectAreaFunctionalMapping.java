package tavant.twms.domain.admin;

import java.util.List;

public class SubjectAreaFunctionalMapping {
	
	private SubjectArea subjectArea;
	
	private List<FunctionalMapping> functionalAreas;

	public List<FunctionalMapping> getFunctionalAreas() {
		return functionalAreas;
	}

	public void setFunctionalAreas(List<FunctionalMapping> functionalAreas) {
		this.functionalAreas = functionalAreas;
	}

	public SubjectArea getSubjectArea() {
		return subjectArea;
	}

	public void setSubjectArea(SubjectArea subjectArea) {
		this.subjectArea = subjectArea;
	}

	public SubjectAreaFunctionalMapping(SubjectArea subjectArea,
			List<FunctionalMapping> functionalAreas) {
		super();
		this.subjectArea = subjectArea;
		this.functionalAreas = functionalAreas;
	}

	public SubjectAreaFunctionalMapping() {
		super();	
	}
	

}
