--PURPOSE    : DeActivate State QU in MSA.
--AUTHOR     : Chetan
--CREATED ON : 24-APr-2014
update msa set d_active=0 where st='QU'
/
commit
/