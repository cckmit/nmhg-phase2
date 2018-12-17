--Purpose    : Created few more columns for Migration of Data from R3
--Author     : Jhulfikar Ali A
--Created On : 21-May-08

ALTER TABLE REQUEST_WNTY_CVG
ADD (ORDER_NUMBER VARCHAR(255), D_INTERNAL_COMMENTS VARCHAR2(1000))
/
ALTER TABLE request_wnty_cvg_audit
MODIFY (comments varchar2(2000))
/
COMMIT
/