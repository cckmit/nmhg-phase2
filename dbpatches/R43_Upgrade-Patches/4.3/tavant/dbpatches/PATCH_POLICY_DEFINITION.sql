--Purpose    : Changes Made to include Certification Status to a Policy Definition,Changed As a Part of 4.3 Upgrade
--Author     : Lavin Hawes
--Created On : 05-Jan-10
--Updated On : 20-Apr-2011, by Shilpi Singh, for adding update script specific to AIR BU.

Alter table Policy_Definition add Certification_Status VARCHAR2(255) DEFAULT 'NOTCERTIFIED'
/
UPDATE Policy_Definition SET Certification_Status = 'ANY' WHERE Certification_Status = 'NOTCERTIFIED'
/
update policy_definition set certification_status = 'CERTIFIED' where business_unit_info = 'AIR' and certification_status = 'ANY'
/
COMMIT
/