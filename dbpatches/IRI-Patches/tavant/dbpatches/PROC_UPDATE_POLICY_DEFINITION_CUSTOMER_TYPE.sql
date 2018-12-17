--Purpose    : Insert Customer Type for each Policy Definition
--Author     : Jitesh Jain
--Created On : 30-Ssep-08

CREATE OR REPLACE PROCEDURE INSERT_CUST_TYPE_POLICY_DEF AS

CURSOR C1 IS
SELECT ID
FROM POLICY_DEFINITION;

BEGIN

FOR c1_rec IN c1 LOOP 

INSERT INTO APPLICABLE_CUSTOMER_TYPES VALUES (applicable_customer_types_seq.NEXTVAL, 'Dealer', c1_rec.ID);

INSERT INTO APPLICABLE_CUSTOMER_TYPES VALUES (applicable_customer_types_seq.NEXTVAL, 'EndCustomer', c1_rec.ID);

COMMIT;

END LOOP;
END;
/
BEGIN
INSERT_CUST_TYPE_POLICY_DEF();
END;
/
