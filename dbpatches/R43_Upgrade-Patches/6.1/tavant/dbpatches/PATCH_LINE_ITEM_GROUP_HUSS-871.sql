--Purpose : Data fix patch for HUSS-871. Updating payment of claim_audit for "Non OEM Parts" to negative value as in payment of claim.
--Author  : Varun Bhat
--Date    : 07/Nov/2011

Declare

V_Claim_ID Number;
V_Claim_Audit Number;
V_Accepted_Amt Number;
v_line_item_group_id number;

Cursor All_Claim_Audit_Id Is
Select c.id as V_Claim_ID, ca.id as v_claim_audit, Lig.Accepted_Amt v_accepted_amt From Claim C, Line_Item_Groups Ligs, Line_Item_Group Lig, Claim_Audit ca
Where Lig.Name='Non Oem Parts' And Lig.Accepted_Amt<0 And Lig.Id = Ligs.Line_Item_Groups And Ligs.For_Payment = C.Payment
and C.Business_Unit_Info='Hussmann' and c.id = ca.for_claim;

Cursor Line_Item_For_Claim_Audit(Claim_Audit_Id Number) Is
    Select Lig.id as v_line_item_group_id From Line_Item_Groups Ligs, Line_Item_Group Lig, Claim_Audit Ca
Where Lig.Name='Non Oem Parts' And Lig.Accepted_Amt=0 And Lig.Id = Ligs.Line_Item_Groups And Ligs.For_Payment = Ca.Payment
and ca.id = CLAIM_AUDIT_ID and Ca.Previous_State not in ('DENIED_AND_CLOSED','DENIED');

BEGIN
 For Each_Rec In All_Claim_Audit_Id Loop
	    For Each_Line_Item_Group_Id In Line_Item_For_Claim_Audit(Each_Rec.V_Claim_Audit) Loop
		update Line_Item_Group set Accepted_Amt=Each_Rec.V_Accepted_Amt, D_Internal_Comments='HUSS-871'  where id=Each_Line_Item_Group_Id.v_line_item_group_id;
	    END LOOP; 	 
 End Loop;	 
End;  
/
COMMIT
/