package tavant.twms.domain.claim;

import java.util.Collection;

public interface JobCodeRepository {

	public Job findJob(final Long jobId);

	public Job findJob(final String jobCode);

	@SuppressWarnings("unchecked")
	public Collection<Job> findJobsStartingWith(final String jobCodePrefix);

	public Long saveJob(final Job job);

	public void updateJob(final Job job);

	public void deleteJobById(final Long id);

	public boolean isJobCodeAssignedToAnyClaims(final String jobCode);

}