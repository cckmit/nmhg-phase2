--Purpose	 :	For including servicing location for payment modifiers
--Author	 :  PARTHASARATHY R
--Created On : 	25-Mar-2013

ALTER TABLE PAYMENT_MODIFIER ADD (Servicing_Location NUMBER(19,0))
/
ALTER TABLE PAYMENT_MODIFIER ADD CONSTRAINT MODIFIER_SERVICING_LOCATION_FK FOREIGN KEY (Servicing_Location) REFERENCES ADDRESS(ID)
/