create or replace
PACKAGE                 "COMMON_UTILS" 
AS

  CONSTANT_OEM_NAME       VARCHAR2(100)   :=   'OEM';


 PROCEDURE ParseSeparatedList
 (
  p_String_To_Parse  IN  VARCHAR2,
  p_separator   IN    VARCHAR2,
  p_parsed_list   OUT  DBMS_UTILITY.UNCL_ARRAY,
  p_count      OUT  NUMBER
 );

 PROCEDURE ParseAnySeperatorList
 (
  p_String_To_Parse  IN  VARCHAR2,
  p_separator   IN    VARCHAR2,
  p_parsed_list   OUT  DBMS_UTILITY.UNCL_ARRAY,
  p_count      OUT  NUMBER
 );

 FUNCTION addError(p_error_code VARCHAR2, new_error_code VARCHAR2)
 RETURN VARCHAR2;


 FUNCTION isValidDate(p_date VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION isNumber(p_number VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION isPositiveNumber(p_number VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION isPositiveInteger(p_number VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION hasSpaces(p_string VARCHAR2)
    RETURN BOOLEAN;


 FUNCTION isValidPhoneNum(p_string VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION isValidEmail(p_string VARCHAR2)
 RETURN BOOLEAN;


 FUNCTION isBefore(fromDate DATE,tillDate DATE)
 RETURN BOOLEAN;


 FUNCTION addErrorMessage(p_error_msg VARCHAR2, new_error_msg VARCHAR2)
 RETURN VARCHAR2;

 FUNCTION count_delimited_values(p_value VARCHAR2, p_delimiter VARCHAR2)
 RETURN NUMBER;

 FUNCTION get_delimited_value(p_value VARCHAR2, p_delimiter VARCHAR2, p_index NUMBER)
 RETURN VARCHAR2;

 FUNCTION getValidJobCode(p_job_code VARCHAR2)
 RETURN VARCHAR2;

 FUNCTION getValidFaultCode(p_fault_code VARCHAR2)
 RETURN VARCHAR2;
 
 FUNCTION count_delimited_values_new(p_value VARCHAR2, p_delimiter VARCHAR2)
 RETURN NUMBER;

END Common_Utils;