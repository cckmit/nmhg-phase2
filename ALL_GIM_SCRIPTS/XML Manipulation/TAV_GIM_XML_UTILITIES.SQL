CREATE OR REPLACE PACKAGE              TAV_GIM_XML_UTILITIES
/*
|| Package Name   : TAV_GIM_XML_UTILITIES
|| Purpose        : Package to bundle reusable code related to xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

AS

/*
|| Function Name  : READ_LINE
|| Purpose        : Function used to read the XML content in line
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION read_line(p_clob  IN CLOB,p_start IN OUT INTEGER) RETURN VARCHAR2;

END TAV_GIM_XML_UTILITIES;
/


CREATE OR REPLACE PACKAGE BODY TAV_GIM_XML_UTILITIES
/*
|| Package Name   : TAV_GIM_XML_UTILITIES
|| Purpose        : Package to bundle reusable code related to xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

AS

/*
|| Function Name  : READ_LINE
|| Purpose        : Function used to read the XML content in line
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

FUNCTION read_line(p_clob IN CLOB, p_start IN OUT INTEGER)
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
                           amount    => end_pos - p_start,
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
      p_start   := 0; -- End of XML indication
    END IF;
    RETURN r_record; 
  END read_line;
  
END TAV_GIM_XML_UTILITIES;
/
