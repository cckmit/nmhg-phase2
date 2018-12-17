package tavant.twms.infra;

public interface RelevanceScoreComputerService {

	
	public long computeRelevanceScore(String entityAlias, Object obj);
}