package tavant.twms.domain.orgmodel;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.orgmodel.CoreCertificationRepository;
import tavant.twms.domain.orgmodel.SeriesCertificationRepository;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author mritunjay.kumar
 * 
 */
public class CertificateServiceImpl implements CertificateService  {
	
	private CoreCertificationRepository coreCertificationRepository;
	
	private SeriesCertificationRepository seriesCertificationRepository;
	
	private SeriesRefCertificationRepository seriesRefCertificationRepository;

	public List<CoreCertification> findCoCertificationByCertificateNameAndBrand(
			String certificateName,String categoryLevel, String certificateBrand) {
				return coreCertificationRepository.findCOCertificateByNameAndBrand(certificateName,categoryLevel,certificateBrand);
	}

	public List<SeriesRefCertification> findSeriesCertificationsByCertificateNameAndBrand(
			String categoryName, String title, String brand) {
		return seriesCertificationRepository.findSeriesCertificationsByCertificateNameAndBrand(categoryName, title, brand);
	}


	public SeriesRefCertificationRepository getSeriesRefCertificationRepository() {
		return seriesRefCertificationRepository;
	}

	public void setSeriesRefCertificationRepository(
			SeriesRefCertificationRepository seriesRefCertificationRepository) {
		this.seriesRefCertificationRepository = seriesRefCertificationRepository;
	}

	public CoreCertificationRepository getCoreCertificationRepository() {
		return coreCertificationRepository;
	}

	public void setCoreCertificationRepository(
			CoreCertificationRepository coreCertificationRepository) {
		this.coreCertificationRepository = coreCertificationRepository;
	}

	public SeriesCertificationRepository getSeriesCertificationRepository() {
		return seriesCertificationRepository;
	}

	public void setSeriesCertificationRepository(
			SeriesCertificationRepository seriesCertificationRepository) {
		this.seriesCertificationRepository = seriesCertificationRepository;
	}

	@Transactional(readOnly = false)
	public void save(SeriesRefCertification seriesRefCertification) {
		this.seriesRefCertificationRepository.save(seriesRefCertification);
	}
	
	@Transactional(readOnly = false)
	public void saveInCoreCertificate(CoreCertification coreCertificate){
		this.coreCertificationRepository.save(coreCertificate);
	}

	@Transactional(readOnly = false)
	public void update(SeriesRefCertification seriesRefCertification) {
		this.seriesRefCertificationRepository.update(seriesRefCertification);

	}

	@Transactional(readOnly = false)
	public void delete(SeriesRefCertification seriesRefCertification) {
		this.seriesRefCertificationRepository.delete(seriesRefCertification);
	}

	public SeriesRefCertification findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SeriesRefCertification> findByIds(
			Collection<Long> collectionOfIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SeriesRefCertification> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public PageResult<SeriesRefCertification> findAll(
			PageSpecification pageSpecification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = false)
	public void deleteAll(List<SeriesRefCertification> entitiesToDelete) {
		// TODO Auto-generated method stub

	}

	public SeriesRefCertification findBySeries(ItemGroup series) {
		// TODO Auto-generated method stub
		return seriesRefCertificationRepository.findBySeries(series);
	}

	public List<SeriesCertification> findByCertificateNameForPR(
			String certificateName, String brand) {
		// TODO Auto-generated method stub
		return seriesRefCertificationRepository.findByCertificateNameForPR(
				certificateName, brand);
	}

	public List<SeriesRefCertification> findAllCertificatesStartingWith(
			String certificateName, int pageNumber, int pageSize) {
		return seriesRefCertificationRepository
				.findAllCertificatesStartingWith(certificateName, pageNumber,
						pageSize);

	}

	public CoreCertification findByCertificateNameForCO(String certificateName,
			String brand) {
		// TODO Auto-generated method stub
		return coreCertificationRepository.findByCertificateNameForCO(
				certificateName, brand);
	}

	public SeriesRefCertification findSeriesRefCertificationById(Long id){
		return seriesRefCertificationRepository.findById(id);
	}

     }
