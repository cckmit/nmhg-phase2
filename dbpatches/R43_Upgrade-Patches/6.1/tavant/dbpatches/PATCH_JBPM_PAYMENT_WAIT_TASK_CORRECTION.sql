--Purpose    : Patch for correcting the the PaymentWaitTask migrated as PartReturnScheduler,changes made as a part of 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 05-July-2011

UPDATE JBPM_TASKINSTANCE TI
SET NAME_ = 'PaymentWaitTask',
  task_   =
  (SELECT id_ FROM jbpm_task WHERE name_ = 'PaymentWaitTask'
  )
WHERE NOT EXISTS
  (SELECT tim.taskinstance_
  FROM JBPM_TIMER TIM
  WHERE TI.ID_   = TIM.TASKINSTANCE_
  )
  AND TI.ISOPEN_ = 1
  AND TI.NAME_   = 'PartsReturnScheduler'
  AND TI.ID_     > 110000000000000
/
UPDATE JBPM_TOKEN TOKEN
SET NODE_ =
  ( SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'WaitForPaymentResponse'
  )
WHERE EXISTS
  (SELECT ti.token_
  FROM JBPM_TASKINSTANCE TI
  WHERE TI.token_ = TOKEN.id_
  AND TI.ISOPEN_  = 1
  AND TI.NAME_    = 'PaymentWaitTask'
  AND TI.ID_      > 110000000000000
  )
/
commit
/