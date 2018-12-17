--Patch to remove all unwanted columns and tables related to payment.
ALTER TABLE LINE_ITEM_GROUP set unused (TOTAL_AMT,TOTAL_CURR,DISBURSED_AMT,DISBURSED_CURR,COSTPRICE_AMT,COSTPRICE_CURR,DLR_CLAIM_AMT,DLR_CLAIM_CURR,TOTAL_DLR_CLAIM_AMT,TOTAL_DLR_CLAIM_CURR,DISPLAY_AMT,DISPLAY_CURR,PERCENTAGE_ACCEPTANCE_FOR_CP)
/
ALTER TABLE PAYMENT set unused (PREVIOUS_PAID_AMOUNT_AMT,PREVIOUS_PAID__AMOUNT_CURR,CLAIMED_TOTAL_AMT,CLAIMED_TOTAL_CURR)
/
alter table DCAP_CLAIM_AUDIT drop column D_D_INTERNAL_COMMENTS
/
alter table STG_INSTALL_BASE drop column DCAP_FLAG
/
--ALTER TABLE MODIFIERS set unused (LINE_ITEM_GROUP_AUDIT)
--/
--ALTER TABLE LABOR_SPLIT_DETAILS set unused (LINE_ITM_GRP_AUDIT,SPLIT_DTL_AUDIT)
--/
DROP TABLE "PAYMENT_PREVIOUS_CREDIT_MEMOS" CASCADE CONSTRAINTS
/
DROP TABLE "PREVIOUS_PART_INFO" CASCADE CONSTRAINTS
/
DROP TABLE "LINE_ITEM_GROUP_AUDIT" CASCADE CONSTRAINTS
/
DROP TABLE "PAYMENT_COMPONENT" CASCADE CONSTRAINTS
/
DROP TABLE "PAYMENT_AUDIT" CASCADE CONSTRAINTS
/