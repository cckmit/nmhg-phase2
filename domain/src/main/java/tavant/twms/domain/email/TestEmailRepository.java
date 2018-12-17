package tavant.twms.domain.email;

import org.springframework.transaction.annotation.Transactional;
import tavant.twms.infra.GenericRepository;

import java.util.List;

/**
 * Created by deepak.patel on 31/3/14.
 */
public interface TestEmailRepository extends GenericRepository<TestEmails,Long> {

    @Transactional(readOnly = true)
    public List<TestEmails> findAllTestEmails();
}
