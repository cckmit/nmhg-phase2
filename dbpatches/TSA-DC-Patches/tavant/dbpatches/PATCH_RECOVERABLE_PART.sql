alter table recoverable_part drop column part
/
alter table recoverable_part add(OEM_PART NUMBER(19))
/
alter table recoverable_part add(INSTALLED_PART NUMBER(19))
/
ALTER TABLE recoverable_part ADD (
	CONSTRAINT FK_REC_OEM_PART
	FOREIGN KEY (OEM_PART) 
	REFERENCES oem_part_replaced (ID)
)
/
ALTER TABLE recoverable_part ADD (
	CONSTRAINT FK_REC_INSTALLED_PART
	FOREIGN KEY (INSTALLED_PART) 
	REFERENCES installed_parts (ID)
)
/
