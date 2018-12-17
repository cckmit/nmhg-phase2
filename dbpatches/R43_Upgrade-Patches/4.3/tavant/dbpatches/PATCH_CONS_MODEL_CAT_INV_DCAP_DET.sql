--Purpose    : Scripts for creating a constraint on MODEL_CATEGORY, as a part of Single Instance migration dev team request 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

ALTER TABLE INVENTORY_DCAP_DETAIL ADD CONSTRAINT INV_DCAP_DTL_MOD_CAT_FK FOREIGN KEY (MODEL_CATEGORY) REFERENCES MODEL_CATEGORY(ID)
/
commit
/
