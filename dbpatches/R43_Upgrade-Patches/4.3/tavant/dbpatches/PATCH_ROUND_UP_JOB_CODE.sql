--Purpose    : Patch for Creating Round Up Job Code, changed as a part of 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 11-Oct-10


INSERT
INTO ASSEMBLY_DEFINITION
  (
    ID,
    CODE,
    NAME,
    VERSION,
    ASSEMBLY_LEVEL,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    ASSEMBLY_DEFINITION_SEQ.nextval,
    'RU',
    'ROUND UP LABOR',
    '0',
    '1',
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO ASSEMBLY
  (
    ID,
    TREAD_ABLE,
    VERSION,
    IS_PART_OF_ASSEMBLY,
    ASSEMBLY_DEFINITION,
    FAULT_CODE,
    ACTIVE,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    ASSEMBLY_SEQ.nextval,
    '0',
    '0',
    Null,
    (select id from assembly_definition where code = 'RU'),
    NULL,
    '1',
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO FAILURE_STRUCTURE
  (
    ID,
    VERSION,
    FOR_ITEM_GROUP,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    FAILURE_STRUCT_SEQ.nextval,
    '0',
    NULL,
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO ACTION_NODE
  (
    ID,
    VERSION,
    DEFINED_FOR,
    DEFINITION,
    ACTIVE,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    ACTION_NODE_SEQ.nextval,
    '0',
    (select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) ),
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    '1',
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROCEDURE_DEFINITION
  (
    ID,
    CODE,
    VERSION,
    ACTION_DEFINITION,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    BUSINESS_UNIT_INFO
  )
  VALUES
  (
    SERVICE_PROCEDUREDEF_SEQ.nextval,
    'RU-CC',
    '1',
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    Sysdate,
    'R4.3 Upgrade',
    sysdate,
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    '1',
    'AIR'
  )
/
INSERT
INTO SERVICE_PROCEDURE_DEFINITION
  (
    ID,
    CODE,
    VERSION,
    ACTION_DEFINITION,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    BUSINESS_UNIT_INFO
  )
  VALUES
  (
    SERVICE_PROCEDUREDEF_SEQ.nextval,
    'RU-CC',
    '1',
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    Sysdate,
    'R4.3 Upgrade',
    sysdate,
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    '1',
    'TFM'
  )
/
INSERT
INTO SERVICE_PROCEDURE_DEFINITION
  (
    ID,
    CODE,
    VERSION,
    ACTION_DEFINITION,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    BUSINESS_UNIT_INFO
  )
  VALUES
  (
    SERVICE_PROCEDUREDEF_SEQ.nextval,
    'RU-CC',
    '1',
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    Sysdate,
    'R4.3 Upgrade',
    sysdate,
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    '1',
    'Clubcar ESA'
  )
/
INSERT
INTO SERVICE_PROCEDURE_DEFINITION
  (
    ID,
    CODE,
    VERSION,
    ACTION_DEFINITION,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    BUSINESS_UNIT_INFO
  )
  VALUES
  (
    SERVICE_PROCEDUREDEF_SEQ.nextval,
    'RU-CC',
    '1',
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    Sysdate,
    'R4.3 Upgrade',
    sysdate,
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    '1',
    'Transport Solutions ESA'
  )
/
INSERT
INTO SERVICE_PROCEDURE_DEFINITION
  (
    ID,
    CODE,
    VERSION,
    ACTION_DEFINITION,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    BUSINESS_UNIT_INFO
  )
  VALUES
  (
    SERVICE_PROCEDUREDEF_SEQ.nextval,
    'RU-CC',
    '1',
    (SELECT id FROM action_definition WHERE name = 'ADJUST'
    ),
    Sysdate,
    'R4.3 Upgrade',
    sysdate,
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    TO_TIMESTAMP(sysdate, 'DD-MM-RR HH12:MI:SS.FF AM'),
    '1',
    'Hussmann'
  )
/
INSERT
INTO SERVICE_PROCEDURE
  (
    ID,
    FOR_CAMPAIGNS,
    SUGGESTED_LABOUR_HOURS,
    VERSION,
    DEFINITION,
    DEFINED_FOR,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    SERVICE_PROCEDURE_SEQ.nextval,
    '0',
    '0',
    '0',
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC' and BUSINESS_UNIT_INFO = 'AIR'),
    (select id from action_node where DEFINED_FOR = ((select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )) ),
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROCEDURE
  (
    ID,
    FOR_CAMPAIGNS,
    SUGGESTED_LABOUR_HOURS,
    VERSION,
    DEFINITION,
    DEFINED_FOR,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    SERVICE_PROCEDURE_SEQ.nextval,
    '0',
    '0',
    '0',
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC' and BUSINESS_UNIT_INFO = 'TFM'),
    (select id from action_node where DEFINED_FOR = ((select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )) ),
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROCEDURE
  (
    ID,
    FOR_CAMPAIGNS,
    SUGGESTED_LABOUR_HOURS,
    VERSION,
    DEFINITION,
    DEFINED_FOR,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    SERVICE_PROCEDURE_SEQ.nextval,
    '0',
    '0',
    '0',
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC' and BUSINESS_UNIT_INFO = 'Clubcar ESA'),
    (select id from action_node where DEFINED_FOR = ((select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )) ),
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROCEDURE
  (
    ID,
    FOR_CAMPAIGNS,
    SUGGESTED_LABOUR_HOURS,
    VERSION,
    DEFINITION,
    DEFINED_FOR,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    SERVICE_PROCEDURE_SEQ.nextval,
    '0',
    '0',
    '0',
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC' and BUSINESS_UNIT_INFO = 'Hussmann'),
    (select id from action_node where DEFINED_FOR = ((select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )) ),
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROCEDURE
  (
    ID,
    FOR_CAMPAIGNS,
    SUGGESTED_LABOUR_HOURS,
    VERSION,
    DEFINITION,
    DEFINED_FOR,
    D_INTERNAL_COMMENTS,
    D_ACTIVE
  )
  VALUES
  (
    SERVICE_PROCEDURE_SEQ.nextval,
    '0',
    '0',
    '0',
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC' and BUSINESS_UNIT_INFO = 'Transport Solutions ESA'),
    (select id from action_node where DEFINED_FOR = ((select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )) ),
    'R4.3 Upgrade',
    '1'
  )
/
INSERT
INTO SERVICE_PROC_DEF_COMPS
  (
    SERVICE_PROCEDURE_DEFINITION,
    COMPONENTS,
    LIST_INDEX
  )
  VALUES
  (
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC'  and BUSINESS_UNIT_INFO = 'Transport Solutions ESA'),
    (select id from assembly_definition where code = 'RU'),
    '0'
  )
/
INSERT
INTO SERVICE_PROC_DEF_COMPS
  (
    SERVICE_PROCEDURE_DEFINITION,
    COMPONENTS,
    LIST_INDEX
  )
  VALUES
  (
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC'  and BUSINESS_UNIT_INFO = 'Clubcar ESA'),
    (select id from assembly_definition where code = 'RU'),
    '0'
  )
/
INSERT
INTO SERVICE_PROC_DEF_COMPS
  (
    SERVICE_PROCEDURE_DEFINITION,
    COMPONENTS,
    LIST_INDEX
  )
  VALUES
  (
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC'  and BUSINESS_UNIT_INFO = 'Hussmann'),
    (select id from assembly_definition where code = 'RU'),
    '0'
  )
/
INSERT
INTO SERVICE_PROC_DEF_COMPS
  (
    SERVICE_PROCEDURE_DEFINITION,
    COMPONENTS,
    LIST_INDEX
  )
  VALUES
  (
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC'  and BUSINESS_UNIT_INFO = 'AIR'),
    (select id from assembly_definition where code = 'RU'),
    '0'
  )
/
INSERT
INTO SERVICE_PROC_DEF_COMPS
  (
    SERVICE_PROCEDURE_DEFINITION,
    COMPONENTS,
    LIST_INDEX
  )
  VALUES
  (
    (Select Id From Service_Procedure_Definition Where Code = 'RU-CC'  and BUSINESS_UNIT_INFO = 'TFM'),
    (select id from assembly_definition where code = 'RU'),
    '0'
  )
/
INSERT
INTO FAILURE_STRUCTURE_ASSEMBLIES
  (
    FAILURE_STRUCTURE,
    ASSEMBLIES
  )
  VALUES
  (
    (select id from failure_structure where for_item_group is null),
    (select id from assembly where ASSEMBLY_DEFINITION = ((select id from assembly_definition where code = 'RU')) )
  )
/
COMMIT
/