--Purpose    : Patch to implement business unit info for Reduced Wnty Cvg
--Author     : Ramalakshmi P
--Created On : 20-Apr-09

ALTER TABLE REQUEST_WNTY_CVG ADD BUSINESS_UNIT_INFO VARCHAR(255)
/
ALTER TABLE REQUEST_WNTY_CVG ADD CONSTRAINT REQUEST_WNTY_CVG_BUSI_UNIT_FK FOREIGN KEY (business_unit_info) REFERENCES BUSINESS_UNIT
/
COMMIT
/