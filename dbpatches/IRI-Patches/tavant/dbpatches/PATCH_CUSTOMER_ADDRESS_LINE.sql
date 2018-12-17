--Purpose : Added line 2 & 3 to customer address
--Author : raghuram.d
--Date : 11-Jul-09

ALTER TABLE address_for_transfer ADD address_line2 VARCHAR2(255) NULL
/
ALTER TABLE address_for_transfer ADD address_line3 VARCHAR2(255) NULL
/
COMMIT
/
