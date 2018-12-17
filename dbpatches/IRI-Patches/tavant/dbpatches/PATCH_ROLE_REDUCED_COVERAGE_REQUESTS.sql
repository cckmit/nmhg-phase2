-- Creating role for approval of reduced coverage requests
-- Author Prashanth Konda
-- Date Nov 25 2008

insert into role(ID, NAME, VERSION, D_INTERNAL_COMMENTS, DISPLAY_NAME)
values ( (select max(id)+1 from role),'reducedCoverageRequestsApprover',1,'For Approving extension of reduced coverages','reducedCoverageRequestsApprover')
/
COMMIT
/