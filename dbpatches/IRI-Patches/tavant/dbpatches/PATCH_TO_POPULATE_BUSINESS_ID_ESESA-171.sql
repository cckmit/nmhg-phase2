update sync_tracker set BUSINESS_ID = trim(dbms_lob.substr(BODXML,(dbms_lob.instr(BODXML,'</LogicalId>',1,1)-dbms_lob.instr(BODXML,'<LogicalId>',1,1)-11),dbms_lob.instr(BODXML,'<LogicalId>',1,1)+11))
/
COMMIT
/