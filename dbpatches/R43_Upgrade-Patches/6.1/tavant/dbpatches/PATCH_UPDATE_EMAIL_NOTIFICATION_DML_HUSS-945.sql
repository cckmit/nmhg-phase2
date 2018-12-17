--PURPOSE    : PATCH FOR UPDATING tables Notification_Event & Abstract_Notification_Message. 
-- In Notification_Event table updating for records where pending is true and for Abstract_Notification_Message table updating for records for message_state in ('FAILED','PENDING') and Number_Of_Trials less than
--AUTHOR     : Varun K
--CREATED ON : 28-NOV-11

Update Notification_Event Set Number_Of_Trials=3, D_Internal_Comments = D_Internal_Comments || 'HUSS-945', Pending=0 Where D_Created_On < sysdate and pending=1
/
Update Abstract_Notification_Message Set Number_Of_Trials=3 Where Creation_Date < Sysdate And Message_State In ('FAILED','PENDING') And Number_Of_Trials<3
/
COMMIT
/