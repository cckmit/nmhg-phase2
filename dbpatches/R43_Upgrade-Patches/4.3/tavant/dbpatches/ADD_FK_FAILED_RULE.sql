--Name : Joseph Tharakan
--Pupose : to add missing foreign key to the FAILED_RULE table
ALTER TABLE FAILED_RULE ADD CONSTRAINT FK_FAILED_RULE_Z1 FOREIGN KEY (RULE_DETAIL) REFERENCES RULE_FAILURE(ID)
/
commit
/