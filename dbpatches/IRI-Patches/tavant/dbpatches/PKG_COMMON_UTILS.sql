--Purpose    : Package for common activities
--Author     : Jhulfikar Ali. A
--Created On : 11-Feb-09

CREATE OR REPLACE
PACKAGE Common_Utils
AS

  CONSTANT_OEM_NAME       VARCHAR2(100)   :=   'OEM';

 --TO CREATE A ARRAY FROM A LIST SEPARATED BY DELIMITERS
 PROCEDURE ParseSeparatedList
 (
  p_String_To_Parse  IN  VARCHAR2,
  p_separator   IN    VARCHAR2,
  p_parsed_list   OUT  DBMS_UTILITY.UNCL_ARRAY,
  p_count      OUT  NUMBER
 );

 --TO ADD ANOTHER ERROR CODE OR VALUE INTO A COMMON VARIABLE
 FUNCTION addError(p_error_code VARCHAR2, new_error_code VARCHAR2)
 RETURN VARCHAR2;

 --TO CHECK IF PROVIDED DATE IS A VALID DATE
 FUNCTION isValidDate(p_date VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE PASSED STRING IS A NUMBER
 FUNCTION isNumber(p_number VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE PASSED STRING A POSITIVE NUMBER
 FUNCTION isPositiveNumber(p_number VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE PASSED STRING IS A POSITIVE INTEGER
 FUNCTION isPositiveInteger(p_number VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE PASSED VALUE HAS SPACES IN IT
 FUNCTION hasSpaces(p_string VARCHAR2)
    RETURN BOOLEAN;

 --TO CHECK IF THE PASSED VALUE IS A VALID PHONE NUMBER
 FUNCTION isValidPhoneNum(p_string VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE PASSED VALUE IS A VALID EMAIL ADDRESS
 FUNCTION isValidEmail(p_string VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE FROM DATE IS BEFORE TILL DATE
 FUNCTION isBefore(fromDate DATE,tillDate DATE)
 RETURN BOOLEAN;
 
 --TO ADD ANOTHER ERROR MESSAGE OR VALUE INTO A COMMON VARIABLE
 FUNCTION addErrorMessage(p_error_msg VARCHAR2, new_error_msg VARCHAR2)
 RETURN VARCHAR2;
 
--END OF THE PACKAGE
END Common_Utils;
/
CREATE OR REPLACE
PACKAGE BODY Common_Utils
AS

 --TO CREATE ARRAY OF A STRING CONTAINING MULTIPLE DELIMITED VALUES
 PROCEDURE ParseSeparatedList
 (
  p_String_To_Parse IN   VARCHAR2,
  p_separator   IN     VARCHAR2,
  p_parsed_list   OUT  DBMS_UTILITY.UNCL_ARRAY,
  p_count         OUT  NUMBER
 )
 IS
  v_remaining_string VARCHAR2(4000) := '';
  v_separator_count  NUMBER := 0;
  v_jthEndPosition NUMBER := 0;
  v_jthString  VARCHAR2(4000) := '';
 BEGIN
  IF LENGTH(p_separator) != 1
  THEN
   RAISE_APPLICATION_ERROR(-20101,'Only one character separators are handled!');
  END IF;
 
  FOR i IN 1..LENGTH(p_String_To_Parse)
  LOOP
   IF SUBSTR(p_String_To_Parse,i,1) = p_separator
   THEN
    v_separator_count := v_separator_count + 1;
   END IF;
  END LOOP;
 
  p_count := v_separator_count + 1;
  v_remaining_string := p_String_To_Parse;
 
  FOR j IN 1..(v_separator_count+1)
  LOOP
   v_jthEndPosition := INSTR(v_remaining_string,p_separator,1,1);
   IF v_jthEndPosition > 0
   THEN
    v_jthString := SUBSTR(v_remaining_string,1,(v_jthEndPosition-1));
   ELSE
    v_jthString := SUBSTR(v_remaining_string,1);
   END IF;
   p_parsed_list(j) := v_jthString;
   v_remaining_string := SUBSTR(v_remaining_string,v_jthEndPosition+1);
  END LOOP;
 END ParseSeparatedList;
 
 
 --TO ADD ANOTHER ERROR CODE TO ERROR STRING
 FUNCTION addError (p_error_code VARCHAR2, new_error_code VARCHAR2)
 RETURN VARCHAR2
 IS
  v_error_code VARCHAR2(4000) := NULL;
  BEGIN
   IF (p_error_code IS NULL)
   THEN
    v_error_code := new_error_code;
   ELSE
    v_error_code := p_error_code || ',' || new_error_code;
   END IF;
   RETURN v_error_code;
  END addError;
 
 
 --TO CHECK IF PASSED DATE IS  A VALID DATE. FORMAT OF DATE SHOULD BE YYYYMMDD
 FUNCTION isValidDate (p_date VARCHAR2)
 RETURN BOOLEAN
 IS
  v_date DATE := NULL;
  BEGIN
   SELECT TO_DATE(p_date,'YYYYMMDD')
   INTO   v_date
   FROM   DUAL;
   RETURN TRUE;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isValidDate;
 
 
 --TO CHECK IF THE PASSED STRING IS A NUMBER OR NOT
 FUNCTION isNumber (p_number VARCHAR2)
 RETURN BOOLEAN
 IS
  v_number VARCHAR2(100) := NULL;
  BEGIN
   v_number := SIGN(p_number);
   RETURN TRUE;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isNumber;
 
 
 --TO CHECK IF THE PASSED STRING IS A POSITIVE NUMBER
 FUNCTION isPositiveNumber (p_number VARCHAR2)
 RETURN BOOLEAN
 IS
  v_number NUMBER := NULL;
  BEGIN
   v_number := SIGN(p_number);
   IF v_number = -1
   THEN
    RETURN FALSE;
   ELSE
    RETURN TRUE;
   END IF;
  EXCEPTION

 WHEN OTHERS THEN
   RETURN FALSE;
  END isPositiveNumber;
 
 
 --TO CHECK IF THE PASSED STRING IS A POSITIVE INTEGER
 FUNCTION isPositiveInteger (p_number VARCHAR2)
 RETURN BOOLEAN
 IS
  v_number NUMBER := NULL;
  BEGIN
   v_number := SIGN(p_number);
   IF v_number = -1
   THEN
    RETURN FALSE;
   ELSIF ( TO_NUMBER(p_number) - FLOOR(TO_NUMBER(p_number)) <> 0 )
   THEN
    RETURN FALSE;
   ELSE
    RETURN TRUE;
   END IF;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isPositiveInteger;
 
 
 --TO CHECK IF WE HAVE SPACES IN PASSED STRING
 FUNCTION hasSpaces(p_string VARCHAR2)

 RETURN BOOLEAN
 IS
  v_number NUMBER := NULL;
  BEGIN
   v_number := INSTR(p_string,' ');  -- CHECKING FOR SPACES
   IF (v_number = 0)
   THEN
    v_number := INSTR(p_string,' ');  -- CHECKING FOR TAB
   END IF;
 
   IF (v_number > 0)
   THEN
    RETURN TRUE;
   ELSE
    RETURN FALSE;
   END IF;
  END hasSpaces;
 
 --TO CHECK IF THE PASSED VALUE IS A VALID PHONE NUMBER
 FUNCTION isValidPhoneNum(p_string VARCHAR2)
 RETURN BOOLEAN
 IS
  v_number NUMBER := NULL;
  BEGIN
   v_number := LENGTH(p_string);  -- CHECKING FOR LENGTH
 
   IF (v_number = 12)
   THEN
    IF (INSTR(p_string,'-',1,1) = 4)
       THEN
        IF (INSTR(p_string,'-',1,2) = 8)
        THEN
            RETURN TRUE;
      ELSE
       RETURN FALSE;
      END IF;
     ELSE
      RETURN FALSE;
    END IF;
   ELSE
    RETURN FALSE;
   END IF;
 
  END isValidPhoneNum;
 
 --TO CHECK IF THE PASSED VALUE IS A VALID EMAIL ADDRESS
 FUNCTION isValidEmail(p_string VARCHAR2)
 RETURN BOOLEAN
 IS
  v_number1 NUMBER := NULL;
  v_number2 NUMBER := NULL;
  BEGIN
   v_number1 := INSTR(p_string,'@',1,1);
   v_number1 := INSTR(p_string,'.',1,1);
 
   IF  (v_number1 > 0)
   THEN
    IF (v_number1 > 0)
    THEN
     RETURN TRUE;
    ELSE
     RETURN FALSE;
    END IF;
   ELSE
    RETURN FALSE;
   END IF;
  END isValidEmail;
 
 --TO CHECK IF THE FROM DATE IS BEFORE TILL DATE
 FUNCTION isBefore(fromDate DATE,tillDate DATE)
 RETURN BOOLEAN
 IS
   BEGIN
       IF fromDate > tillDate
     THEN
         RETURN FALSE;
   ELSE
    RETURN TRUE;
   END IF;
 END isBefore;

 --TO ADD ANOTHER ERROR MESSAGE OR VALUE INTO A COMMON VARIABLE
 FUNCTION addErrorMessage(p_error_msg VARCHAR2, new_error_msg VARCHAR2)
 RETURN VARCHAR2
 IS
  v_error_msg VARCHAR2(4000) := NULL;
  BEGIN
   IF (p_error_msg IS NULL)
   THEN
    v_error_msg := SUBSTR(new_error_msg, 1, 4000);
   ELSE    
    v_error_msg := SUBSTR(p_error_msg || ';' || new_error_msg, 1, 4000);
   END IF;
   RETURN v_error_msg;
 END addErrorMessage;
 
END Common_Utils;
/
COMMIT
/