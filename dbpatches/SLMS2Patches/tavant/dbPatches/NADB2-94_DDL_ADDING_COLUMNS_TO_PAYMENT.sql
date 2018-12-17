--Purpose    : Patch TO ADD COLUMN IN Payment
--Author     : AJIT KUMAR SINGH
--Created On : 12-MAR-2014

alter table payment add
     (
	  state_mandate_active NUMBER(1)
     )
/