--Purpose    : In case both Async Payment and external credit submission is enabled, if claim is closed from Async Payment, it will update sync tracker to cancelled.
--Created On : 21-May-2010
--Created By : Rahul Katariya
--Impact     : AsyncPayment

Insert Into Sync_Status (Status,D_Created_On,D_Internal_Comments,D_Updated_On,D_Last_Updated_By,D_Created_Time,D_Updated_Time,D_Active) Values ('Cancelled',Sysdate,'TSA-Migration',Sysdate,1,Sysdate,Sysdate,1)
/
commit
/