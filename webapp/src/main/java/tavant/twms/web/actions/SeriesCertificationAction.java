package tavant.twms.web.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.classic.Validatable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.CertificateService;
import tavant.twms.domain.orgmodel.SeriesCertification;
import tavant.twms.domain.orgmodel.CoreCertification;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.domain.orgmodel.SeriesRefCertificationRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class SeriesCertificationAction extends SummaryTableAction implements
		Preparable, Validatable {

	private String description;
	private CatalogService catalogService;
	private SeriesRefCertificationRepository seriesRefCertificationRepository;
	private CertificateService certificateService;
	private SeriesRefCertification seriesAndCertifications;
	private SeriesRefCertification existingSeriesCertification;
	private List<SeriesCertification> certifications = new ArrayList<SeriesCertification>();
	
	private List<SeriesCertification> seriesCertification = new ArrayList<SeriesCertification>();


	private String certificateName;
	private String brand;

	private static JSONArray EMPTY_CERT_DETAIL;

	private static Logger logger = Logger
			.getLogger(SeriesCertificationAction.class);

	static {
		EMPTY_CERT_DETAIL = new JSONArray();
		EMPTY_CERT_DETAIL.put("-");
		EMPTY_CERT_DETAIL.put("-");
		EMPTY_CERT_DETAIL.put("-");
		EMPTY_CERT_DETAIL.put("-");

	}

	public void prepare() throws Exception {
		if (this.seriesAndCertifications != null) {
			seriesCertification = seriesAndCertifications.getSeriesCertification();
		}
		// if (this.seriesAndCertifications != null) {
		// existingSeriesCertification = this.certificateService
		// .findByCertificateName(this.seriesAndCertifications
		// .getCertificateName());
		//
		// }
	}

	@Override
	public void validate() {
		if (this.seriesAndCertifications != null) {
			validateSeries();
			validateDateRange();
			validateRow();
			//validateUniqueCertificateForPR();
			validateCertificate();
			validateIfCertificateExistsForTheSeries();
		}
	}

	private void validateCertificate() {
		try{
		CoreCertification certificateForCO;
		Set<String> certificatesEntered = new HashSet<String>();
		List<SeriesCertification> certificateForPR = new ArrayList<SeriesCertification>();
		String brand = this.seriesAndCertifications.getSeries().getBrandType();
		if (seriesCertification != null && seriesCertification.size() > 0) {
			for (SeriesCertification eachCert : seriesCertification) {
				if(!certificatesEntered.add(eachCert.getCertificateName())){
					addActionError("error.certificate.duplicateCertificate",eachCert.getCertificateName());
					break;
				}
				certificateForCO = this.certificateService
						.findByCertificateNameForCO(
								eachCert.getCertificateName(),
								brand);
				certificateForPR = this.certificateService
						.findByCertificateNameForPR(
								eachCert.getCertificateName(),
								brand);
				if (eachCert.getCategoryLevel().equals(AdminConstants.CORE_CERTIFICATE_LEVEL)) {
					if (certificateForCO != null
							&& !certificateForCO.getCategoryName().equals(eachCert.getCategoryName())) {
						addActionError("error.certificate.certificateExists",
								 new String[] {eachCert.getCertificateName(),certificateForCO.getCategoryName()});
						break;
					}
					if (certificateForPR != null && !certificateForPR.isEmpty()
							&& !certificateForPR.get(0).getCategoryLevel().equals(eachCert.getCategoryLevel())) {
						addActionError("error.certificate.certificateExistsForPR",
								 new String[] {eachCert.getCertificateName(),AdminConstants.PRODUCT_CERTIFICATE_LEVEL});
						break;
					}
				}
				else if (eachCert.getCategoryLevel().equals(AdminConstants.PRODUCT_CERTIFICATE_LEVEL)) {
					if (certificateForPR != null && !certificateForPR.isEmpty()
							&& !certificateForPR.get(0).getCategoryName().equals(eachCert.getCategoryName())) {
						addActionError("error.certificate.certificateExists",
								 new String[] {eachCert.getCertificateName(),certificateForPR.get(0).getCategoryName()});
						break;
					}
					if (certificateForCO != null
							&& !certificateForCO.getCategoryLevel().equals(eachCert.getCategoryLevel())) {
						addActionError("error.certificate.certificateExistsForCO",
								 new String[] {eachCert.getCertificateName(),AdminConstants.CORE_CERTIFICATE_LEVEL});
						break;
					}
				}
			}
		}
		}
		catch(Exception exception){
			logger.error("Failed to validate Core certifications", exception);
		}

	
	}
	
	private void validateIfCertificateExistsForTheSeries() {
		// TODO Auto-generated method stub
		
	}

	private void validateSeries() {
		if(this.seriesAndCertifications.getSeries().getName()==null){
			addActionError("error.certification.seriesMandatory");
		}
		
	}

	private void validateRow() {
		if (seriesCertification != null && seriesCertification.size() > 0) {
			seriesCertification.removeAll(Collections.singleton(null));
			seriesAndCertifications.getSeriesCertification().removeAll(
					Collections.singleton(null));
			for (SeriesCertification eachRow : seriesCertification) {
				if (!StringUtils.hasText(eachRow.getCertificateName())) {
					addActionError("error.certification.certificateNameMandatory");
					break;
				}
				if (eachRow.getCategoryLevel().equals("-1") || eachRow.getCategoryLevel().isEmpty() ) {
					addActionError("error.certification.categoryLevelMandatory");
				}
				if (!StringUtils.hasText(eachRow.getCategoryName())) {
					addActionError("error.certification.categoryNameMandatory");
					break;
				}
			}
		}

	}

	/*private void validateUniqueCertificateForPR() {
		try{
		List<SeriesCertification> certificateForPR;
		ItemGroup series = this.seriesAndCertifications.getSeries();
		if (seriesCertification != null && seriesCertification.size() > 0) {
			for (SeriesCertification eachCert : seriesCertification) {
				if (eachCert.getCategoryLevel().equals("PR")) {
					certificateForPR = this.certificateService
							.findByCertificateNameForPR(
									eachCert.getCertificateName(),
									eachCert.getBrand());
					if (certificateForPR != null
							&& !certificateForPR.get(0).getSeriesRefCert().getSeries()
									.equals(series)) {
						addActionError("error.certificate.certificateExists"
								+ certificateForPR.get(0).getSeriesRefCert()
										.getSeries().getGroupCode());
						break;
					}
				}
			}
		}
		}
		catch(Exception exception){
			logger.error("failed to validate PRoduct certification",exception);
		}

	}*/

	private void validateDateRange() {
		CalendarDate startDate = seriesAndCertifications.getStartDate();
		CalendarDate endDate = seriesAndCertifications.getEndDate();
		if(startDate==null){
			addActionError("error.certification.startDateRequired");
		}
		if(endDate==null){
			addActionError("error.certification.endDateRequired");
		}
		if (endDate!=null && startDate!=null && endDate.isBefore(startDate)) {
			addActionError("error.certification.endDateBeforeStartDate");
		}

	}

	@Override
	protected PageResult<?> getBody() {
		return this.seriesRefCertificationRepository.findPage(
				"from SeriesRefCertification", getCriteria());

	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("Id", "id", 0, "String", "id", false,
				true, true, false));
		header.add(new SummaryTableColumn("", "label", 0, "String",
				"series.groupCode", true, false, true, false));
		header.add(new SummaryTableColumn(
				"columnTitle.technicianCertification.series",
				"series.groupCode", 15, "string"));
		header.add(new SummaryTableColumn(
				"columnTitle.technicianCertification.seriesDescription",
				"series.description", 25, "string"));
		header.add(new SummaryTableColumn("columnTitle.common.startDate",
				"startDate", 9, "date"));
		header.add(new SummaryTableColumn("label.common.endDate", "endDate", 9,
				"date"));
		header.add(new SummaryTableColumn(
				"columnTitle.technicianCertification.company",
				"series.brandType", 7, "String"));
		header.add(new SummaryTableColumn(
				"columnTitle.technicianCertification.sisterSeries",
				"series.oppositeSeries.groupCode", 7, "String"));

		return header;
	}

	public String seriesToCertification() {
		return SUCCESS;
	}

	public String viewDefinition() {
		if (org.springframework.util.StringUtils.hasLength(this.id)) {
			Long definitionId = Long.parseLong(this.id);
			this.seriesAndCertifications = this.certificateService
					.findSeriesRefCertificationById(definitionId);
		}
		return SUCCESS;
	}

	public String getDescriptionForSeries() throws JSONException {
		JSONArray details = new JSONArray();
		ItemGroup itemGroup = this.catalogService.findItemGroup(Long
				.parseLong(description));
		details.put(itemGroup.getName());
		details.put(itemGroup.getBrandType());
		if (itemGroup.getOppositeSeries() != null)
			details.put(itemGroup.getOppositeSeries().getGroupCode());
		jsonString = details.toString();
		return SUCCESS;
	}

	public void setSeriesRefCertificationRepository(
			SeriesRefCertificationRepository seriesRefCertificationRepository) {
		this.seriesRefCertificationRepository = seriesRefCertificationRepository;
	}

	private void addSortCriteria(ListCriteria criteria) {
		criteria.removeSortCriteria();
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		criteria.removeFilterCriteria();
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}
	
	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = getListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

	public String detail() {
		this.seriesAndCertifications = seriesRefCertificationRepository
				.findById(Long.parseLong(this.id));
		seriesCertification.addAll(this.seriesAndCertifications.getSeriesCertification());
		return SUCCESS;
	}

	public String saveSeriesCertificates() {
		List<SeriesCertification> coreCertificates = new ArrayList<SeriesCertification>();
		certifications.addAll(seriesCertification);
		validateIfSeriesExist();
		if (hasActionErrors()) {
			return INPUT;
		} else {
			
			for (SeriesCertification c : certifications) {
				if (c != null) {
					if (c.getCategoryLevel().equals("CO")) {
						coreCertificates.add(c);
					}
					c.setSeriesRefCert(seriesAndCertifications);
					c.setBrand(seriesAndCertifications.getSeries().getBrandType());
				}
			}
			this.certificateService.save(this.seriesAndCertifications);
			this.seriesAndCertifications.getSeriesCertification().addAll(
					certifications);
			this.certificateService.update(this.seriesAndCertifications);
			addToCoreCertification(coreCertificates);
			addActionMessage("error.certificate.savedSuccess");
			return SUCCESS;
		}
	}

	private void addToCoreCertification(
			List<SeriesCertification> coreCertificates) {
		// TODO Auto-generated method stub
		CoreCertification coreCertificate;
		for (SeriesCertification eachCert : coreCertificates) {
			coreCertificate = this.certificateService
					.findByCertificateNameForCO(eachCert.getCertificateName(),
							eachCert.getBrand());
			if (coreCertificate == null) {
				CoreCertification newCoreCert = new CoreCertification();
				newCoreCert.setBrand(eachCert.getBrand());
				newCoreCert.setCategoryLevel(eachCert.getCategoryLevel());
				newCoreCert.setCategoryName(eachCert.getCategoryName());
				newCoreCert.setCertificateName(eachCert.getCertificateName());
				this.certificateService.saveInCoreCertificate(newCoreCert);
			}
		}

	}

	private void validateIfSeriesExist() {
		if (this.seriesAndCertifications != null) {
			SeriesRefCertification existingSeries = this.certificateService
					.findBySeries(this.seriesAndCertifications.getSeries());
			if (existingSeries != null) {
				addActionError("error.certificate.seriesAlreadyExist");
			}
		}

	}

	public String updateSeriesCertificates() {
		List<SeriesCertification> coreCertificates = new ArrayList<SeriesCertification>();
		certifications.addAll(seriesCertification);
		if (hasActionErrors()) {
			return INPUT;
		} else {
			this.seriesAndCertifications.getSeriesCertification().clear();
			for (SeriesCertification c : certifications) {
				if (c != null) {
					if (c.getCategoryLevel().equals("CO")) {
						coreCertificates.add(c);
					}
					c.setSeriesRefCert(seriesAndCertifications);
				}
			}
			this.seriesAndCertifications.getSeriesCertification().addAll(
					certifications);
			this.certificateService.update(this.seriesAndCertifications);
			addToCoreCertification(coreCertificates);
			addActionMessage("error.certificate.updateSuccess");
			return SUCCESS;
		}

	}

	public String deleteSeriesCertificates() {
		this.seriesAndCertifications.getD().setActive(false);
		for(SeriesCertification certificationsOfSeries: this.seriesAndCertifications.getSeriesCertification()){
			certificationsOfSeries.getD().setActive(false);
		}
		this.certificateService.update(this.seriesAndCertifications);
		addActionMessage("error.certificate.deleteSuccess");
		return SUCCESS;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public SeriesRefCertificationRepository getSeriesRefCertificationRepository() {
		return seriesRefCertificationRepository;
	}

	public SeriesRefCertification getSeriesAndCertifications() {
		return seriesAndCertifications;
	}

	public void setSeriesAndCertifications(
			SeriesRefCertification seriesAndCertifications) {
		this.seriesAndCertifications = seriesAndCertifications;
	}

	public CertificateService getCertificateService() {
		return certificateService;
	}

	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	public SeriesRefCertification getExistingSeriesCertification() {
		return existingSeriesCertification;
	}

	public void setExistingSeriesCertification(
			SeriesRefCertification existingSeriesCertification) {
		this.existingSeriesCertification = existingSeriesCertification;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public String listCertificateDetails() throws JSONException {
		JSONArray details = new JSONArray();
		List<SeriesCertification> seriesCertificates = this.certificateService
				.findByCertificateNameForPR(certificateName.toUpperCase(),
						brand.toUpperCase());
		if (seriesCertificates != null && !seriesCertificates.isEmpty()) {
				 JSONObject productJSON = new JSONObject();
				 productJSON.put("brand",seriesCertificates.get(0).getBrand());
				 productJSON.put("categoryLevel",seriesCertificates.get(0).getCategoryLevel());
				 productJSON.put("categoryName",seriesCertificates.get(0).getCategoryName());
				 details.put(productJSON);
			jsonString = details.toString();
		} else {
			CoreCertification coreCertificate = this.certificateService
					.findByCertificateNameForCO(certificateName.toUpperCase(),
							brand.toUpperCase());
			if (coreCertificate != null) {
				 JSONObject coreJSON = new JSONObject();
				 coreJSON.put("brand",coreCertificate.getBrand());
				 coreJSON.put("categoryLevel",coreCertificate.getCategoryLevel());
				 coreJSON.put("categoryName",coreCertificate.getCategoryName());
				 details.put(coreJSON);
				jsonString = details.toString();
			} else {
				JSONObject emptyJSON = new JSONObject();
				emptyJSON.put("brand","--");
				emptyJSON.put("categoryLevel","--");
				emptyJSON.put("categoryName","--");
				details.put(emptyJSON);
				jsonString = details.toString();
			}
		}
		return SUCCESS;
	}

	public String listCertificates() {
		List<SeriesRefCertification> certificates = new ArrayList<SeriesRefCertification>();
		if (org.springframework.util.StringUtils.hasText(getSearchPrefix())) {
			certificates = this.certificateService
					.findAllCertificatesStartingWith(getSearchPrefix()
							.toUpperCase(), 0, 10);

		} else {
			return generateAndWriteEmptyComboboxJson();
		}
		return generateAndWriteComboboxJson(certificates, "id",
				"certificateName");
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public List<SeriesCertification> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<SeriesCertification> certifications) {
		this.certifications = certifications;
	}

	public List<SeriesCertification> getSeriesCertification() {
		return seriesCertification;
	}

	public void setSeriesCertification(List<SeriesCertification> seriesCertification) {
		this.seriesCertification = seriesCertification;
	}
}