--Purpose     Added table policy_for_itemconditions to capture item condition for which policy is applicable
--Author      Jitesh Jain
--Created On  19-OCT-08

CREATE TABLE POLICY_FOR_ITEMCONDITIONS (policy_defn NUMBER(19,0) NOT NULL, for_itemcondition VARCHAR2(20 CHAR) NULL)
/
ALTER TABLE POLICY_FOR_ITEMCONDITIONS ADD CONSTRAINT POLICY_ITEM_COND_POLICYDEF_FK FOREIGN KEY (policy_defn) REFERENCES policy_definition(ID)
/
ALTER TABLE POLICY_FOR_ITEMCONDITIONS ADD CONSTRAINT POLICY_ITEM_COND_ITEMCOND_FK FOREIGN KEY (for_itemcondition) REFERENCES inventory_item_condition(ITEM_CONDITION)
/


