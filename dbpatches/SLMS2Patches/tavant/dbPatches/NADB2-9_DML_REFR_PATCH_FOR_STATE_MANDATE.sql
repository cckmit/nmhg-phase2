-- PURPOSE    : PATCH TO ADD Handling Fee in Cost Category and Section table
-- AUTHOR     : Arpitha Nadig AR.
-- CREATED ON : 13-DEC-2013

--------------------------------------------------------
--  Constraints for Table STATE_MNDTE_COST_CTGY_MAPPING
--------------------------------------------------------


ALTER TABLE STATE_MANDATES ADD CONSTRAINT LABOR_RATE_TYPE_FK FOREIGN KEY ("LABOR_RATE_TYPE")
REFERENCES LIST_OF_VALUES ("ID") ENABLE
/
ALTER TABLE STATE_MNDTE_COST_CTGY_MAPPING ADD CONSTRAINT STATE_MANDATE_FK FOREIGN KEY ("STATE_MANDATE")
REFERENCES STATE_MANDATES ("ID") ENABLE
/
ALTER TABLE STATE_MNDTE_COST_CTGY_MAPPING ADD CONSTRAINT COST_CATEGORY_FK FOREIGN KEY ("COST_CATEGORY")
REFERENCES COST_CATEGORY ("ID") ENABLE
/
