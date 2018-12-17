update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.PartsClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date >= add_months(sysdate,-6) and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Parts')
/
COMMIT
/
update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.CampaignClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date >= add_months(sysdate,-6) and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Field Modification')
/
COMMIT
/
update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.MachineClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date >= add_months(sysdate,-6) and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Machine')
/
COMMIT
/
update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.PartsClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date <= add_months(sysdate,-6) and c.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Parts')
/
COMMIT
/
update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.CampaignClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date <= add_months(sysdate,-6) and c.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Field Modification')
/
COMMIT
/
update claim_audit ca set prev_claim_snapshot_string = (deletexml(XMLTYPE(prev_claim_snapshot_string),'/tavant.twms.domain.claim.MachineClaim/payment')).getclobval(),d_internal_comments = d_internal_comments || 'Payment Removed' where prev_claim_snapshot_string is not null and exists (select 1 from claim c where C.Id = Ca.For_Claim and c.filed_on_date <= add_months(sysdate,-6) and c.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED') AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0 and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0 and c.clm_type_name = 'Machine')
/
COMMIT
/