--Purpose    : BUSINESS_UNIT_INFO Added to limit_of_authority_scheme
--Author     : Bharath
--Created On : 22/02/10
--Impact     : None

ALTER TABLE limit_of_authority_scheme ADD BUSINESS_UNIT_INFO VARCHAR2(255 CHAR)
/
COMMIT
/

