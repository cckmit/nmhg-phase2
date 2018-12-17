--PURPOSE    : PATCH TO CHANGE ERROR MESSAGE FOR UPLOAD FROM IR TO OEM
--AUTHOR     : Raghavendra
--CREATED ON : 21-MAY-13
UPDATE
    i18Nupload_error_text
SET
    description = Replace(description, ' IR ', ' OEM ')
/
commit
/