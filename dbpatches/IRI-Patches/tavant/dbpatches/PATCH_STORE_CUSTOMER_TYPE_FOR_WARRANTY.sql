--Purpose: Storing customer type for warranty
--Author: Prashanth 
--Created On: Date 06 Nov 2008
ALTER TABLE WARRANTY ADD(CUSTOMER_TYPE VARCHAR2(100))
/
UPDATE WARRANTY SET CUSTOMER_TYPE='EndCustomer'
/
COMMIT