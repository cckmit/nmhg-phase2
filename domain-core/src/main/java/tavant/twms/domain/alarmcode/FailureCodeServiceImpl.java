package tavant.twms.domain.alarmcode;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class FailureCodeServiceImpl extends GenericServiceImpl<FailureCode, Long, Exception> implements FailureCodeService {
    private FailureCodeRepository failureCodeRepository;

    @Override
    public GenericRepository<FailureCode, Long> getRepository() {
        return failureCodeRepository;
    }

}
