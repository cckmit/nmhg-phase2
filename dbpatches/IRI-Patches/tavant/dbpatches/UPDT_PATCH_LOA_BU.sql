--Purpose    : Updating Processor availability rules business units
--Author     : Jhulfikar Ali. A
--Created On : 06-Apr-09

update PURPOSE set business_unit_info = 'Transport Solutions ESA'
where name='Processor Authority' and business_unit_info = 'Transport Solutions'
/
update PURPOSE set business_unit_info = 'Clubcar ESA'
where name='Processor Authority' and business_unit_info = 'IRI Club Car'
/
COMMIT
/