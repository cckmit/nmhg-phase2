package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

public interface SeriesRefCertificationRepository extends
		GenericRepository<SeriesRefCertification, Long> {

	public SeriesRefCertification findBySeries(ItemGroup series);

	public List<SeriesCertification> findByCertificateNameForPR(String certificateName, String brand);

	public List<SeriesRefCertification> findAllCertificatesStartingWith(
			String certificateName, int pageNumber, int pageSize);

	public PageResult<SeriesRefCertification> findAllSeriesWithCertificates(String fromClause,
			String orderByClause, String selectClause,
			PageSpecification pageSpecification,
			QueryParameters queryParameters, String distinctClause);

}

