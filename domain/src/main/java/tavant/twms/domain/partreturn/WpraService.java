package tavant.twms.domain.partreturn;

import tavant.twms.domain.claim.Claim;

import java.util.List;

public interface WpraService {
	
	public String getProcessorRoleBUSpecific(Claim claim);

    public Wpra createWpraForParts(List<PartReturn> parts);

    public Wpra createWpraForParts(List<PartReturn> parts, Claim claim);

    public void reloadWpras(List<Wpra> wpras);

    public Wpra findWpraById(String id);

}
