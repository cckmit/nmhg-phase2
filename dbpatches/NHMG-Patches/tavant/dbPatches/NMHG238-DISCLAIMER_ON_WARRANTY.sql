ALTER TABLE WARRANTY ADD DIESEL_TIER_WAIVER NUMBER(19,0)
/
ALTER TABLE WARRANTY ADD CONSTRAINT WNTY_DIESEL_TIER_WAIVER_FK FOREIGN KEY (DIESEL_TIER_WAIVER) 
	REFERENCES DIESEL_TIER_WAIVER (ID) ENABLE
/