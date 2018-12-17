--Patches to fix bugs in IRI
--TWMS4.3U-43 Config values with d_active as null
update config_value
set d_active = 1
where d_active is null
/
commit
/
--TWMS4.3U-25 Unable to view Question of existing Reports
UPDATE CUSTOM_REPORT CR
SET CR.REPORT_TYPE = (SELECT LOV.ID 
                      FROM LIST_OF_VALUES LOV
                      WHERE CR.BUSINESS_UNIT_INFO = LOV.BUSINESS_UNIT_INFO
                      AND   UPPER(CR.NAME) LIKE LOV.CODE||'%'
                      AND   LOV.TYPE = 'REPORTTYPE')
WHERE CR.REPORT_TYPE IS NULL
/
commit
/
declare
/*
  This patch required for proper managment of custom reports
*/
  cursor all_rec is
    select cr.name, rfq.id as question_id  
    from custom_report cr,
          report_sections rs,
          report_form_question rfq
    where  cr.id = rs.for_report
    and   rfq.for_section = rs.sections
    and   rfq.pre_instructions is null and rfq.post_instructions is null;       
    
    v_rec             all_rec%ROWTYPE;   
    v_pre_ins_id      NUMBER;
    v_post_ins_id     NUMBER;    
    
begin
    for v_rec in all_rec loop
      begin
      -- This is for preinstructions
      select CUST_REPORT_INSTRUCTION.NEXTVAL INTO v_pre_ins_id 
      from dual;
         
      INSERT INTO CUSTOM_REPORT_INSTRUCTIONS (
        ID,
        INSTRUCTIONS,
        ATTACHMENT,
        D_CREATED_ON,
        D_CREATED_TIME,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_UPDATED_TIME,
        D_LAST_UPDATED_BY,
        D_ACTIVE)
        VALUES (
          v_pre_ins_id,
          NULL,
          NULL,
          SYSDATE,
          CAST(SYSDATE AS TIMESTAMP),
          'This record added by patch due to preinstruction is null of custom report: '||v_rec.name,
          SYSDATE,
          CAST(SYSDATE AS TIMESTAMP),
          NULL,
          1
        );
        
    -- This is for postinstructions  
      select CUST_REPORT_INSTRUCTION.NEXTVAL INTO v_post_ins_id 
      from dual;
         
      INSERT INTO CUSTOM_REPORT_INSTRUCTIONS (
        ID,
        INSTRUCTIONS,
        ATTACHMENT,
        D_CREATED_ON,
        D_CREATED_TIME,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_UPDATED_TIME,
        D_LAST_UPDATED_BY,
        D_ACTIVE)
        VALUES (
          v_post_ins_id,
          NULL,
          NULL,
          SYSDATE,
          CAST(SYSDATE AS TIMESTAMP),
          'This record added by patch due to postinstruction is null of custom report: '||v_rec.name,
          SYSDATE,
          CAST(SYSDATE AS TIMESTAMP),
          NULL,
          1
        );
        
      -- Updating report_form_question
      
      UPDATE REPORT_FORM_QUESTION
      SET
        INCLUDE_OTHER_AS_AN_OPTION = 0,
        PRE_INSTRUCTIONS = v_pre_ins_id,
        POST_INSTRUCTIONS = v_post_ins_id,
        D_UPDATED_ON = SYSDATE,
        D_UPDATED_TIME = CAST(SYSDATE AS TIMESTAMP),
        D_INTERNAL_COMMENTS = D_INTERNAL_COMMENTS||' - patch for TWMS4.3U-25 '
      WHERE ID = v_rec.question_id;
      
      -- This update is needed because for some of the records other_option is null (It should be either true or false)
      UPDATE REPORT_FORM_ANSWER_OPTION
      SET OTHER_OPTION = 0 -- bydefault, false
      WHERE ID IN (SELECT rfao.id
                    FROM report_form_question rfq,
                         section_question_answers sqa,
                         report_form_answer_option rfao
                    WHERE rfq.id = v_rec.question_id
                    AND   rfq.id = sqa.for_question
                    AND   rfao.id = sqa.answer_options
                    AND   rfao.other_option is null);
      
      -- dbms_output.put_line(v_rec.name||' '||v_rec.question_id);      
      commit;      
      exception
      when others then
        rollback;   
        -- dbms_output.put_line('exception');
      end;
    end loop;    
end;
/
--TWMS4.3U-74 and TWMS4.3U-116 technical error if transferred inbox is opened (Payment refactoring changes)
update inbox_view
set field_names = replace(field_names, 'claim.amountAsked', 'claim.payment.claimedAmount')
/
commit
/
update inbox_view
set field_names = replace(field_names, 'amountCredited', 'payment.totalAmount')
/
update inbox_view 
set field_names = replace(field_names, 'amountAsked', 'payment.claimedAmount')
/
commit
/
-- TWMS4.3U-19 Admin when clicks on deactivate button to deactivate claim processing and DCAP validation rule , gives an exception
update domain_predicate 
set predicate_asxml = replace(predicate_asxml, 'claim.state.state', 'claim.state')
/
update domain_rule_audit 
set rule_snapshot_string = replace(rule_snapshot_string, 'claim.state.state', 'claim.state')
/
commit
/
-- TWMS4.3U-64 :: Dealer has the ability to search based on Manufacturing site even if BU flag is turned off for the same
Insert Into I18nlov_Text(id, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES)
select I18n_lov_Text_seq.nextval, 'en_GB', x.description, x.list_of_i18n_values  from (
Select b.* From List_Of_Values A, I18nlov_Text B 
Where Type = 'MANUFACTURINGSITEINVENTORY' And A.Id = B.List_Of_I18n_Values 
And B.Locale = 'en_US' 
and not exists (select 1 from I18nlov_Text c where c.list_of_i18n_values = a.id and c.locale = 'en_GB')) x
/
commit
/
--TWMS 4.3 Upgrade :: Unable to close claims from pending payment submission state
UPDATE INVENTORY_ITEM SET HOURS_ON_MACHINE = 2147483647 WHERE HOURS_ON_MACHINE > 2147483647
/
commit
/
-- TWMS4.3U-109 Admin System is displaying the Technical Error when Admin Deactivating the Entry validation Rule
update domain_predicate 
set predicate_asxml = replace(predicate_asxml, 'pricePerUnit.breachEncapsulationOfAmount()', 'pricePerUnit')
/
update domain_rule_audit 
set rule_snapshot_string = replace(rule_snapshot_string, 'pricePerUnit.breachEncapsulationOfAmount()', 'pricePerUnit')
/
commit
/
-- TWMS4.3U-29 Unable to update an existing Rule
update domain_predicate 
set predicate_asxml = replace(predicate_asxml, 'claim.serviceInformation.faultFound.nameInEnglish', 'claim.serviceInformation.faultFound.name')
/
commit
/
-- 	TWMS4.3U-38 There is no DCAP allowance on this unit" is not working. When dealer tries to file dcap claim on such unit, it gives an exception
update inventory_dcap_detail 
set sea_paid_flag=0  where sea_paid_flag IS NULL
/
update inventory_dcap_detail 
set sca_paid_flag=0  where sca_paid_flag IS NULL
/
update inventory_dcap_detail 
set sca_Purge_Flag=0  where sca_Purge_Flag IS NULL
/
update inventory_dcap_detail 
set sea_Purge_Flag=0  where sea_Purge_Flag IS NULL
/
commit
/
--TWMS 4.3U-10 :: On upload management screen click on upload templates throws 404 error
update UPLOAD_MGT UP1 set TEMPLATE_PATH = replace(TEMPLATE_PATH, '\', '/')
/
commit
/

--TWMS4.3U-21 :: Part description is not displayed on calim page 1 and 2

Declare
Cursor V_All_Rec Is
    Select I.Id 
    From  Item I    
    Where I.Owned_By = 1
    And   I.Item_Type = 'PART'
    And   I.D_Active = 1
    And Not Exists (Select 1 From I18nitem_Text I18n Where I18n.Item = I.Id And I18n.Locale = 'en_US');
  
  V_Item_Id                       Number;
  V_Item_Description              Varchar2(255);
  V_Engb_Exists                   Boolean := True; 
begin
  
  For V_Each_Rec In V_All_Rec Loop
    Begin
      V_Item_Id := V_Each_Rec.Id;
      V_Item_Description := null;
      BEGIN
        Select Description 
        Into   V_Item_Description
        From   I18nitem_Text 
        Where  Item = V_Item_Id
        and    LOCALE = 'en_GB';
      Exception
        When No_Data_Found Then
          v_enGB_exists := false;
          Begin
            Select nvl(description,name)
            Into   V_Item_Description
            From   item 
            Where  id = V_Item_Id;         
          END;          
      End;    
      -- add i18n item text for en_US
      Insert Into I18nitem_Text(
        Id,
        Description,
        Locale,
        ITEM
      )
      Values (
        I18n_item_Text_Seq.Nextval,
        V_Item_Description,
        'en_US',
        v_item_id
      );
      
      If (V_Engb_Exists <> True) Then 
      -- add i18n item text for en_GB
        Insert Into I18nitem_Text(
          Id,
          Description,
          Locale,
          ITEM
        )
        Values (
          I18n_item_Text_Seq.Nextval,
          V_Item_Description,
          'en_GB',
          V_Item_Id
        );
      End If;      
      Commit;      
    Exception 
    When Others Then      
      Rollback;      
    End;
    End Loop;
end;
/
commit
/

-- TWMS4.3U-211 :: Unable to login using user id "fabbri"

Update Org_User	Set D_Active = 0 Where Login = 'fabbri'
/

-- TWMS4.3U-305 :: Claim Type is set as PART in Part Return Configuration, when compared with Production data the set up is as " Claim Type : ALL" 
UPDATE PART_RETURN_DEFINITION SET CLAIM_TYPE = 'ALL' WHERE CLAIM_TYPE IS NULL
/
commit
/