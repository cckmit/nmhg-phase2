-- Index to make the warranty task queries (executed during login) perform better
-- Author: Nandakumar Devi
-- Oct 08 2009

CREATE INDEX WARRANTY_I3 ON WARRANTY (FOR_DEALER)
/