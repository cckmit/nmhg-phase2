--Purpose    : updating the data type of column DESCRIPTION
--Author     : Mayank Vikram
--Created On : 14/04/2010
--Impact     : None

ALTER TABLE ALARM_CODE MODIFY DESCRIPTION VARCHAR2(4000 BYTE)
/
commit
/


