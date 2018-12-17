package tavant.twms.web.complaints;

import org.apache.log4j.Logger;
import tavant.twms.domain.complaints.Complaint;
import tavant.twms.domain.complaints.ComplaintsService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsAction extends SummaryTableAction {

    private ComplaintsService complaintsService;

    private Complaint complaint;

    private static final Logger logger = Logger.getLogger(ComplaintsAction.class);

	@SuppressWarnings("unchecked")
    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.complaintNo", "id", 17, "number", "id", true, true, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.serialNo", "serialNumber", 17));
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.itemNo", "itemNumber", 17));
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.product", "product", 17));
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.model", "model", 17));
        tableHeadData.add(new SummaryTableColumn("columnTitle.complaints.year", "year", 17));
        return tableHeadData;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PageResult<?> getBody() {
    	// folderName gives the complaintType -  Consumer  or FieldReport
    	return this.complaintsService.fetchFieldReportsOrConsumerComplaintsByType(getFolderName(), getCriteria());
    }

    public String showPreview() throws Exception {
		if (getId() != null) {
		        if(logger.isInfoEnabled())
		        {
		            logger.info("The complaint id to be viewed is: " + getId());
		        }
			this.complaint = this.complaintsService.getComplaint(Long.valueOf(getId()));
			return SUCCESS;
		}
		return ERROR;
    }

    public String delete() throws Exception {
                if(logger.isInfoEnabled())
                {
                    logger.info("The complaint to be deleted is: " + this.complaint.getId());
                }
		this.complaintsService.deleteComplaint(this.complaint);
		return SUCCESS;
    }


	public void setComplaintsService(ComplaintsService complaintsService) {
		this.complaintsService = complaintsService;
	}

	public Complaint getComplaint() {
		return this.complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}
}
