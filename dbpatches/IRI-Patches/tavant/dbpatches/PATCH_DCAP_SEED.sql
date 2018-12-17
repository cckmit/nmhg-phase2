--Purpose    : Seed Data for DCAP 
--Author     : rashmi.malik
--Created On : 13-Aug-08


INSERT INTO SPLIT_TYPE
VALUES
(SPLIT_TYPE_SEQ.NEXTVAL, 'SEA', 0)
/

INSERT INTO SPLIT_TYPE
VALUES
(SPLIT_TYPE_SEQ.NEXTVAL, 'SCA', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'A', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'B', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'C', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'D', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'E', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'F', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'G', 0)
/

INSERT INTO MODEL_CATEGORY
VALUES
(MODEL_CATEGORY_SEQ.NEXTVAL, 'X', 0)
/

insert into purpose
(id, name, version)
values
((select max(id) from purpose) + 1, 'DCAP', 0)
/


insert into purpose
(id, name, version)
values
((select max(id) from purpose) + 1, 'SEA', 0)
/

insert into purpose
(id, name, version)
values
((select max(id) from purpose) + 1, 'SCA', 0)
/
INSERT INTO DOMAIN_RULE_ACTION (ID, CONTEXT, NAME, STATE, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY)
VALUES
(DOMAIN_RULEACTION_SEQ.NEXTVAL, 'DcapValidationRules', 'Reject Claim', 'rejected', 0, SYSDATE, NULL, NULL, NULL)
/

insert into role 
(id, name, version)
values
((select max(id) from role) + 1, 'dcapAdmin', 0)
/

insert into user_roles
values
((select id from org_user where login  = 'sysAdmin'), (select id from role where name = 'dcapAdmin'))
/

---for pattern type

INSERT INTO CLAIM_NUMBER_PATTERN (ID, IS_ACTIVE, NUMBERING_PATTERN, TEMPLATE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, PATTERN_TYPE)
VALUES
(2, 1, 1, 'TYP-NNNNNNN',SYSDATE, 'Created for DCAP', SYSDATE, NULL, 'Club Car', 'DCAP' )
/
commit
/
