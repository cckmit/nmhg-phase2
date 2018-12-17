--Purpose    : Updating status to 'PART_TO_BE_SHIPPED' in base_part_return table for particular part in which status is showing part_accepted   
--Author     : ajitkumar.singh
--Created On : 26/04/2011

UPDATE base_part_return
SET status='PART_TO_BE_SHIPPED'
WHERE id IN
  (SELECT bpr.id
  FROM jbpm_taskinstance ti,
    jbpm_moduleinstance mi,
    jbpm_variableinstance vi,
    base_part_return bpr
  WHERE ti.taskmgmtinstance_ = mi.id_
  AND mi.processinstance_    = vi.processinstance_
  AND vi.longidclass_        = 'tavant.twms.domain.partreturn.PartReturn'
  AND vi.longvalue_          = bpr.id
  AND bpr.status             = 'PART_ACCEPTED'
  AND ti.name_               = 'Due Parts Receipt'
  AND isopen_                = 1
  )
/
commit
/