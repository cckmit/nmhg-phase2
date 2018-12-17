--Purpose    : Patch for creating CLAIM_AUDIT_TO_PUT_ON_HOLD,CLAIM_AUDIT_TO_REQ_FROM_USER,CLAIM_AUDIT_TO_REJECT_REASON tables
--Author     : Sumesh kumar
--Created On : 20-JAN-2014

CREATE TABLE CLAIM_AUDIT_TO_PUT_ON_HOLD(claim_audit_id number(19,0),put_on_hold_id number(19,0), CONSTRAINT fk_claims_audit_put_id FOREIGN KEY (claim_audit_id) REFERENCES CLAIM_AUDIT(ID) ,CONSTRAINT fk_put_on_hold FOREIGN KEY (put_on_hold_id) REFERENCES LIST_OF_VALUES(ID))
/
CREATE TABLE CLAIM_AUDIT_TO_REQ_FROM_USER(claim_audit_id number(19,0),req_from_user_id number(19,0), CONSTRAINT fk_claims_audit_req_id FOREIGN KEY (claim_audit_id) REFERENCES CLAIM_AUDIT(ID) ,CONSTRAINT fk_req_from_user_id FOREIGN KEY (req_from_user_id) REFERENCES LIST_OF_VALUES(ID))
/
CREATE TABLE CLAIM_AUDIT_TO_REJECT_REASON(claim_audit_id number(19,0),reject_reason_id number(19,0), CONSTRAINT fk_claims_audit_rej_id FOREIGN KEY (claim_audit_id) REFERENCES CLAIM_AUDIT(ID) ,CONSTRAINT fk_reject_reason_id FOREIGN KEY (reject_reason_id) REFERENCES LIST_OF_VALUES(ID))
/