--PURPOSE    : PATCH TO UPDATE PENDING_RECOVERY
--AUTHOR     : GHANASHYAM DAS
--CREATED ON : 31-MAY-12


update claim set pending_recovery = 1 where id in (
    select
        claim1_.id 
    from
        jbpm_taskinstance taskinstan0_ cross 
    join
        claim claim1_ 
    inner join
        claim_claimed_items claimedite2_ 
            on claim1_.id=claimedite2_.claim 
    inner join
        claimed_item claimedite3_ 
            on claimedite2_.claimed_items=claimedite3_.id cross 
    join
        jbpm_task task4_ 
    where
        claim1_.business_unit_info in (
            'Thermo King TSA'
        ) 
        and taskinstan0_.task_=task4_.id_ 
        and taskinstan0_.isopen_=1 
        and taskinstan0_.claim_id=claim1_.id 
        and task4_.name_ = 'Pending Recovery Initiation'
        and (
            taskinstan0_.actorid_='towlejs' 
            or taskinstan0_.actorid_ in (
                'dealer', 'dealerSalesAdministration', 'processor', 'inspector', 'inventorylisting', 'SDataAdmin', 'dealerAdministrator', 'baserole', 'dsm', 'internalUserAdmin', 'dealerWarrantyAdmin', 'admin', 
                'receiver', 'inventoryFullView', 'supplierRecoveryInitiator', 'reducedCoverageRequestsApprover', 'inventoryAdmin', 'partshipper', 'dcapAdmin', 'dsmAdvisor', 'inventorysearch', 'readOnly'
            )
        ) 
      )
/
COMMIT
/