--Purpose    : As 6.0 Latest code base data will  create  pricePerUnit as  pricePerUnit.breachEncapsulationOfAmount,so to replace the existing one.
--Created On : 09-june-2011
--Created By : Surendra Varma

create or replace
PACKAGE domain_xml_update
is
  function read_line (p_clob in clob, p_start in out integer)
    return varchar2;
PROCEDURE PATCH_REPLACE_PRICE_PER_UNIT;
END domain_xml_update;
/
create or replace
PACKAGE BODY domain_xml_update
IS
  FUNCTION read_line (p_clob IN CLOB, p_start IN OUT INTEGER)
    RETURN VARCHAR2
  IS
    r_record     VARCHAR2 (4000);
    end_pos      INTEGER;
    file_length  BINARY_INTEGER;
  BEGIN
    file_length  := sys.DBMS_LOB.getlength (p_clob);
 
    end_pos      :=
      DBMS_LOB.INSTR (lob_loc => p_clob, pattern => CHR (10), offset => p_start);
    IF end_pos > 0 THEN
      r_record  :=
        RTRIM (
          DBMS_LOB.SUBSTR (lob_loc   => p_clob,
                           amount    => LEAST (end_pos - p_start, 240),
                           offset    => p_start
                          ),
          CHR (13) || CHR (10)
        );
      p_start  := end_pos + 1;
    ELSE
      r_record  := DBMS_LOB.SUBSTR (lob_loc   => p_clob,
                         amount    => file_length - p_start + 1,
                         offset    => p_start
                        );
      p_start   := 0;
    END IF;
    RETURN r_record;
  end read_line;
  
  PROCEDURE PATCH_REPLACE_PRICE_PER_UNIT AS 

CURSOR PREDICATES IS
select * from domain_predicate 
where instr(predicate_asxml, 'pricePerUnit') > 0 ;
                
  predicate_xml clob;
  new_predicate_xml clob;
  str varchar(4000);
  new_str varchar(4000);
  has_more_data number := 1 ;
  line_number number := 0;

BEGIN 

FOR EACH_PREDICATE IN PREDICATES LOOP
BEGIN
	dbms_lob.createtemporary(predicate_xml,true);
    dbms_lob.createtemporary(new_predicate_xml,true);
--dbms_output.put_line(EACH_PREDICATE.id);
    has_more_data := 1;
		if (dbms_lob.getlength(EACH_PREDICATE.predicate_asxml) > 0) then
		  while (has_more_data > 0) loop
			str := domain_xml_update.read_line(EACH_PREDICATE.predicate_asxml, has_more_data);
	--        line_number := line_number + 1;        
	--        dbms_output.put_line(line_number || '  ' ||has_more_data);
			new_str := ltrim(str);
			dbms_lob.writeappend(predicate_xml, LENGTH(new_str), new_str);
		  END LOOP;
		end if;

  
  if (dbms_lob.instr(predicate_xml, '<lhs class="domainVariable" context="ClaimRules"><accessedFromType>NonOEMPartReplaced</accessedFromType><fieldName>pricePerUnit</fieldName></lhs>') >0 ) then 
    new_predicate_xml := 
    replace(predicate_xml,'<lhs class="domainVariable" context="ClaimRules"><accessedFromType>NonOEMPartReplaced</accessedFromType><fieldName>pricePerUnit</fieldName></lhs>'
    ,'<lhs class="domainVariable" context="ClaimRules"><accessedFromType>NonOEMPartReplaced</accessedFromType><fieldName>pricePerUnit.breachEncapsulationOfAmount()</fieldName></lhs>');    

  end if;

--  dbms_output.put_line(new_predicate_xml);              
  update domain_predicate set predicate_asxml = new_predicate_xml where id = EACH_PREDICATE.id;
		
  dbms_lob.freetemporary(predicate_xml);
  dbms_lob.freetemporary(new_predicate_xml);

COMMIT;
  END;
END LOOP;
COMMIT ;
EXCEPTION WHEN OTHERS THEN
  dbms_lob.freetemporary(predicate_xml);
  dbms_lob.freetemporary(new_predicate_xml);
  ROLLBACK;
  raise;
END PATCH_REPLACE_PRICE_PER_UNIT;
END domain_xml_update;
/
begin
domain_xml_update.PATCH_REPLACE_PRICE_PER_UNIT();
end;
/