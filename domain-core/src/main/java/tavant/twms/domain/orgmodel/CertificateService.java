package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.ItemGroup;

public interface CertificateService  {
	
	public List<CoreCertification> findCoCertificationByCertificateNameAndBrand(String certificateName, String categoryName,String certificateBrand);

	public List<SeriesRefCertification> findSeriesCertificationsByCertificateNameAndBrand(String categoryName,
			String title, String string);

	public SeriesRefCertification findBySeries(ItemGroup series);
	
	public List<SeriesCertification> findByCertificateNameForPR(String certificateName,String brand);
	
	@Transactional(readOnly = false)
    void save(SeriesRefCertification seriesRefCertification);

    @Transactional(readOnly = false)
    void update(SeriesRefCertification seriesRefCertification);

    @Transactional(readOnly = false)
    void delete(SeriesRefCertification seriesRefCertification);

	public List<SeriesRefCertification> findAllCertificatesStartingWith(
			String certificateName, int pageNumber, int pageSize);

	public CoreCertification findByCertificateNameForCO(String certificateName,String brand);

	@Transactional(readOnly = false)
	public void saveInCoreCertificate(CoreCertification coreCertificate);
	
	public SeriesRefCertification findSeriesRefCertificationById(Long id);

}
