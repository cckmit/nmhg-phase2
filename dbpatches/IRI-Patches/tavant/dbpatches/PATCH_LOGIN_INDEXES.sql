--Purpose    : Indexes for optimize queries executed during login
--Author     : Nandakumar Devi
--Created On : 03-AUG-09

CREATE INDEX ORG_USER_I2 ON ORG_USER (UPPER(LOGIN))
/
CREATE INDEX WARRANTY_TASK_INSTANCE_I1 ON WARRANTY_TASK_INSTANCE (STATUS)
/