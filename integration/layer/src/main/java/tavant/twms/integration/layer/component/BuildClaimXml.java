package tavant.twms.integration.layer.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.integration.layer.component.global.ProcessGlobalClaim;

public class BuildClaimXml {

	private ClaimService claimService;

	private ProcessGlobalClaim processGlobalClaim;

	public void buildXml(String claimNumber) throws Exception {
		Claim claim = claimService.findClaimByNumber(claimNumber);
		String claimAsXml = processGlobalClaim.syncClaim(claim,false);
		File f = new File("C:/claimXmls/" + claimNumber + ".xml");
		setContents(f, claimAsXml);
		if(claim.isNcr()==true||claim.isNcrWith30Days()==true){
			claimAsXml = processGlobalClaim.syncClaim(claim,true);
			 f = new File("C:/claimXmls/" + claimNumber + ".xml");
			setContents(f, claimAsXml);
		}
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setProcessGlobalClaim(ProcessGlobalClaim processGlobalClaim) {
		this.processGlobalClaim = processGlobalClaim;
	}

	static public void setContents(File aFile, String aContents)
			throws FileNotFoundException, IOException {
		Writer output = null;
		try {
			aFile.createNewFile();
			output = new BufferedWriter(new FileWriter(aFile));
			output.write(aContents);
		} finally {
			if (output != null)
				output.close();
		}
	}
}
