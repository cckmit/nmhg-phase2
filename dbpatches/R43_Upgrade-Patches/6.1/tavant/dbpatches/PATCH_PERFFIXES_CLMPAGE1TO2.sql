--PURPOSE    : Indexes for Claim Page 1 to 2
--AUTHOR     : Ramalakshmi P
--CREATED ON : 10-MAY-11

CREATE INDEX PARTY_IDX_UPPNAME ON PARTY(UPPER(NAME))
/