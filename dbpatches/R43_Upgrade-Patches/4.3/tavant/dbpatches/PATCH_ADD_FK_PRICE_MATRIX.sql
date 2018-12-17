--Name : Joseph Tharakan
--Pupose : to add missing foreign key t the price matrix table
ALTER TABLE PRICE_MATRIX ADD CONSTRAINT PM_MODEL_CAT_FK FOREIGN KEY (MODEL_CATEGORY) REFERENCES  MODEL_CATEGORY(ID)
/
commit
/