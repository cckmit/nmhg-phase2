--Purpose : Merge TSESA-346. 
--          Orphan flag on documents attached while doing an ETR is set to false.
--Author
--Date

update document set 
  orphan=0, 
  d_updated_on=sysdate,
  d_updated_time=systimestamp,
  d_internal_comments=d_internal_comments||':TSESA-346'
where id in (
  select d.id
  from document d,warranty_attachments wa
  where d.id= wa.attachments and d.orphan=1
)
/
commit
/