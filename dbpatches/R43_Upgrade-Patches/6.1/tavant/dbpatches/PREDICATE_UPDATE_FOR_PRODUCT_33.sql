--Purpose    : As 6.0 Latest code base data will  create itemReference.model.isPartOf.name as itemReference.model,so to replace the existing one.
--Created On : 27-MAY-2011
--Created By : Surendra Varma

create or replace
PACKAGE domain_xml_update
is
  function read_line (p_clob in clob, p_start in out integer)
    return varchar2;
PROCEDURE PREDICATE_UPDATE_FOR_PRODUCT;
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
  
  PROCEDURE PREDICATE_UPDATE_FOR_PRODUCT AS 
  
CURSOR PREDICATES IS
select * from domain_predicate
where instr(predicate_asxml, 'itemReference.model.isPartOf.name') > 0 ;
                
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

  if (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isOneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
    new_predicate_xml := 
    replace(
      replace(predicate_xml, 
        '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isOneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
          '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><isOneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
         ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
          '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
      );
	  dbms_output.put_line('1 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isSet><operand context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></operand></isSet></predicates><oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all>') >0 ) then 
    new_predicate_xml := 
    replace(predicate_xml,'<all isForOneToOne="true" isQueryPredicate="false"><predicates><isSet><operand context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></operand></isSet></predicates><oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all>'
    ,'<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><isSet><operand context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></operand></isSet></predicates></conditionToBeSatisfied></forAny>');    
	dbms_output.put_line('2 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isNotSet><operand context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></operand></isNotSet></predicates><oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all>') >0) then
    new_predicate_xml := 
     replace(predicate_xml,'<all isForOneToOne="true" isQueryPredicate="false"><predicates><isNotSet><operand context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></operand></isNotSet></predicates><oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all>'
    ,'<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><isSet><operand context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></operand></isSet></predicates></conditionToBeSatisfied></forAny>');
	dbms_output.put_line('3 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isNoneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
    new_predicate_xml := 
    replace(
      replace(predicate_xml, 
        '<all isForOneToOne="true" isQueryPredicate="false"><predicates><isNoneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
          '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><isNoneOf><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
         ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
          '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
      );
	  dbms_output.put_line('4 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.Contains><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
	new_predicate_xml := 
	replace(
	  replace(predicate_xml, 
		'<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.Contains><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
		  '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.Contains><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
		 ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
		  '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
	  );
	  dbms_output.put_line('5 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><equals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
	new_predicate_xml := 
	replace(
	  replace(predicate_xml, 
		'<all isForOneToOne="true" isQueryPredicate="false"><predicates><equals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
		  '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><equals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
		 ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
		  '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
	  );
	  dbms_output.put_line('6 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.NotEquals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
	new_predicate_xml := 
	replace(
	  replace(predicate_xml, 
		'<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.NotEquals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
		  '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.NotEquals><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
		 ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
		  '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
	  );
	  dbms_output.put_line('7 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  elsif (dbms_lob.instr(predicate_xml, '<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.StartsWith><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>') >0 ) then
	new_predicate_xml := 
	replace(
	  replace(predicate_xml, 
		'<all isForOneToOne="true" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.StartsWith><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model.isPartOf.name</fieldName></lhs>', 
		  '<forAny><collectionValuedVariable context="ClaimRules"><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></collectionValuedVariable><conditionToBeSatisfied class="all" isForOneToOne="false" isQueryPredicate="false"><predicates><tavant.twms.domain.rules.StartsWith><lhs class="domainVariable" context="ClaimRules"><accessedFromType>ItemGroup</accessedFromType><fieldName>getProductNameForUnserializedItem()</fieldName></lhs>'
		 ), '<oneToOneVariable><accessedFromType>ClaimedItem</accessedFromType><fieldName>itemReference.model</fieldName></oneToOneVariable></all></predicates></conditionToBeSatisfied></forAny>', 
		  '</conditionToBeSatisfied></forAny></predicates></conditionToBeSatisfied></forAny>'
	  );
	  dbms_output.put_line('8 - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  else
    new_predicate_xml := predicate_xml;
	dbms_output.put_line('No Change - '||EACH_PREDICATE.business_unit_info||'-'||EACH_PREDICATE.name);
  end if;

  update domain_predicate set predicate_asxml = 
  XMLSERIALIZE(Document XMLTYPE (new_predicate_xml) as CLOB INDENT SIZE = 2)
  where id = EACH_PREDICATE.id;
  commit;
  
  dbms_lob.freetemporary(predicate_xml);
  dbms_lob.freetemporary(new_predicate_xml);

  END;
END LOOP;

END PREDICATE_UPDATE_FOR_PRODUCT;

END domain_xml_update;
/
begin
domain_xml_update.PREDICATE_UPDATE_FOR_PRODUCT();
end;
/
update domain_predicate 
set predicate_asxml=replace(predicate_asxml,'<fieldName>pricePerUnit</fieldName>',
	'<fieldName>pricePerUnit.breachEncapsulationOfAmount()</fieldName>')
where instr(predicate_asxml,'<fieldName>pricePerUnit</fieldName>') > 0
/
COMMIT
/