package tavant.twms.domain.campaign;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;

@Entity
@Table(name="HUSS_PARTS_TO_REPLACE")
@Filters({
  @Filter(name="excludeInactive")
})
public class HussPartsToReplace  {
	
	@Id
	@GeneratedValue(generator = "HussPartsToReplace")
	@GenericGenerator(name = "HussPartsToReplace", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "HUSS_PARTS_TO_REPLACE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	
	@OneToMany(cascade = CascadeType.ALL)	
	@JoinColumn(name = "FOR_HUSS_PARTS_TO_REPLACE" )
	private List<OEMPartToReplace> removedParts = new ArrayList<OEMPartToReplace>();
	
	@OneToMany(cascade = CascadeType.ALL)	
	@JoinColumn(name = "FOR_HUSS_PARTS_TO_INSTALL")
	private List<OEMPartToReplace> installedParts = new ArrayList<OEMPartToReplace>();

	@OneToMany(cascade = CascadeType.ALL)	
	@JoinColumn(name = "FOR_HUSS_PARTS_TO_REPLACE")
	private List<NonOEMPartToReplace> nonOEMpartsToReplace = new ArrayList<NonOEMPartToReplace>();

	public List<OEMPartToReplace> getRemovedParts() {
		return removedParts;
	}

	public void setRemovedParts(List<OEMPartToReplace> removedParts) {
		this.removedParts = removedParts;
	}

	public List<OEMPartToReplace> getInstalledParts() {
		return installedParts;
	}

	public void setInstalledParts(List<OEMPartToReplace> installedParts) {
		this.installedParts = installedParts;
	}

	public List<NonOEMPartToReplace> getNonOEMpartsToReplace() {
		return nonOEMpartsToReplace;
	}

	public void setNonOEMpartsToReplace(
			List<NonOEMPartToReplace> nonOEMpartsToReplace) {
		this.nonOEMpartsToReplace = nonOEMpartsToReplace;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	public HussmanPartsReplacedInstalled fetchHussmannPartsReplacedInstalled(CampaignClaim claim) {
	
		HussmanPartsReplacedInstalled hussReplacedInstalled = new HussmanPartsReplacedInstalled();
		
		List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
		List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
		List<InstalledParts> nonOEMParts= new ArrayList<InstalledParts>();		
		
		if(this.removedParts !=null){
			for (OEMPartToReplace replace : this.removedParts) {							
				OEMPartReplaced oemPartReplaced =replace.fetchRemovedPart(claim);			
				 removedParts.add(oemPartReplaced);				
			}	
		}
		if(this.installedParts !=null){
			for (OEMPartToReplace install : this.installedParts) {							
					InstalledParts oemPartReplaced =install.fetchHussmannInstalledParts(claim);			
					installedParts.add(oemPartReplaced);				
			}
		}
		
		if(this.nonOEMpartsToReplace !=null){						
			for( NonOEMPartToReplace nonOemPart : this.nonOEMpartsToReplace) {
				InstalledParts installedNonHussmannPart = nonOemPart.fetchNonHussmannInstalledParts(); 			
				nonOEMParts.add(installedNonHussmannPart);			
			}
		}

		hussReplacedInstalled.setReplacedParts(removedParts);
		hussReplacedInstalled.setHussmanInstalledParts(installedParts);
		hussReplacedInstalled.setNonHussmanInstalledParts(nonOEMParts);
		
		return hussReplacedInstalled;
	}
	
	
	
}
