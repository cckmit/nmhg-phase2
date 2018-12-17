create or replace
PACKAGE BODY                 "COMMON_UTILS" 
AS
PROCEDURE ParseSeparatedList
  (
    p_String_To_Parse IN VARCHAR2,
    p_separator       IN VARCHAR2,
    p_parsed_list OUT DBMS_UTILITY.UNCL_ARRAY,
    p_count OUT NUMBER )
                                    IS
  v_remaining_string VARCHAR2(4000) := '';
  v_separator_count  NUMBER         := 0;
  v_jthEndPosition   NUMBER         := 0;
  v_jthString        VARCHAR2(4000) := '';
BEGIN
  IF LENGTH(p_separator) != 1 THEN
    RAISE_APPLICATION_ERROR(-20101,'Only one character separators are handled!');
  END IF;
  FOR i IN 1..LENGTH(p_String_To_Parse)
  LOOP
    IF SUBSTR(p_String_To_Parse,i,1) = p_separator THEN
      v_separator_count             := v_separator_count + 1;
    END IF;
  END LOOP;
  p_count            := v_separator_count + 1;
  v_remaining_string := p_String_To_Parse;
  FOR j              IN 1..(v_separator_count+1)
  LOOP
    v_jthEndPosition   := INSTR(v_remaining_string,p_separator,1,1);
    IF v_jthEndPosition > 0 THEN
      v_jthString      := SUBSTR(v_remaining_string,1,(v_jthEndPosition-1));
    ELSE
      v_jthString := SUBSTR(v_remaining_string,1);
    END IF;
    p_parsed_list(j)   := v_jthString;
    v_remaining_string := SUBSTR(v_remaining_string,v_jthEndPosition+1);
  END LOOP;
END ParseSeparatedList;
PROCEDURE ParseAnySeperatorList
  (
    p_String_To_Parse IN VARCHAR2,
    p_separator       IN VARCHAR2,
    p_parsed_list OUT DBMS_UTILITY.UNCL_ARRAY,
    p_count OUT NUMBER )
                                    IS
  v_remaining_string VARCHAR2(4000) := '';
  v_separator_count  NUMBER         := 0;
  v_jthEndPosition   NUMBER         := 0;
  v_jthString        VARCHAR2(4000) := '';
  v_len_seperator    NUMBER := LENGTH(p_separator);
BEGIN
  FOR i IN 1..LENGTH(p_String_To_Parse)
  LOOP
    IF SUBSTR(p_String_To_Parse,i,v_len_seperator) = p_separator THEN
      v_separator_count             := v_separator_count + 1;
    END IF;
  END LOOP;
  p_count            := v_separator_count + 1;
  v_remaining_string := p_String_To_Parse;
  FOR j              IN 1..(v_separator_count+1)
  LOOP
    v_jthEndPosition   := INSTR(v_remaining_string,p_separator,1,1);
    IF v_jthEndPosition > 0 THEN
      v_jthString      := SUBSTR(v_remaining_string,1,(v_jthEndPosition-1));
    ELSE
      v_jthString := SUBSTR(v_remaining_string,1);
    END IF;
    p_parsed_list(j)   := v_jthString;
    v_remaining_string := SUBSTR(v_remaining_string,v_jthEndPosition+v_len_seperator);
  END LOOP;
END ParseAnySeperatorList;
FUNCTION addError
  (
    p_error_code   VARCHAR2,
    new_error_code VARCHAR2)
  RETURN VARCHAR2
                              IS
  v_error_code VARCHAR2(4000) := NULL;
BEGIN
  IF (p_error_code IS NULL) THEN
    v_error_code   := new_error_code;
  ELSE
    v_error_code := p_error_code || ',' || new_error_code;
  END IF;
  RETURN v_error_code;
END addError;
FUNCTION isValidDate
  (
    p_date VARCHAR2)
  RETURN BOOLEAN
              IS
  v_date DATE := NULL;
BEGIN
   SELECT TO_DATE(p_date,'YYYYMMDD') INTO v_date FROM DUAL;
  RETURN TRUE;
EXCEPTION
WHEN OTHERS THEN
  RETURN FALSE;
END isValidDate;
FUNCTION isNumber
  (
    p_number VARCHAR2)
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
FUNCTION isPositiveNumber
  (
    p_number VARCHAR2)
  RETURN BOOLEAN
                  IS
  v_number NUMBER := NULL;
BEGIN
  v_number   := SIGN(p_number);
  IF v_number = -1 THEN
    RETURN FALSE;
  ELSE
    RETURN TRUE;
  END IF;
EXCEPTION
WHEN OTHERS THEN
  RETURN FALSE;
END isPositiveNumber;
FUNCTION isPositiveInteger
  (
    p_number VARCHAR2)
  RETURN BOOLEAN
                  IS
  v_number NUMBER := NULL;
BEGIN
  v_number   := SIGN(p_number);
  IF v_number = -1 THEN
    RETURN FALSE;
  ELSIF ( TO_NUMBER(p_number) - FLOOR(TO_NUMBER(p_number)) <> 0 ) THEN
    RETURN FALSE;
  ELSE
    RETURN TRUE;
  END IF;
EXCEPTION
WHEN OTHERS THEN
  RETURN FALSE;
END isPositiveInteger;
FUNCTION hasSpaces
  (
    p_string VARCHAR2)
  RETURN BOOLEAN
                  IS
  v_number NUMBER := NULL;
BEGIN
  v_number    := INSTR(p_string,' '); -- CHECKING FOR SPACES
  IF (v_number = 0) THEN
    v_number  := INSTR(p_string,' '); -- CHECKING FOR TAB
  END IF;
  IF (v_number > 0) THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END hasSpaces;
FUNCTION isValidPhoneNum
  (
    p_string VARCHAR2)
  RETURN BOOLEAN
                  IS
  v_number NUMBER := NULL;
BEGIN
  v_number                       := LENGTH(p_string); -- CHECKING FOR LENGTH
  IF (v_number                    = 12) THEN
    IF (INSTR(p_string,'-',1,1)   = 4) THEN
      IF (INSTR(p_string,'-',1,2) = 8) THEN
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
FUNCTION isValidEmail
  (
    p_string VARCHAR2)
  RETURN BOOLEAN
                   IS
  v_number1 NUMBER := NULL;
  v_number2 NUMBER := NULL;
BEGIN
  v_number1      := INSTR(p_string,'@',1,1);
  v_number1      := INSTR(p_string,'.',1,1);
  IF (v_number1   > 0) THEN
    IF (v_number1 > 0) THEN
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  ELSE
    RETURN FALSE;
  END IF;
END isValidEmail;
FUNCTION isBefore
  (
    fromDate DATE,
    tillDate DATE)
  RETURN BOOLEAN
IS
BEGIN
  IF fromDate > tillDate THEN
    RETURN FALSE;
  ELSE
    RETURN TRUE;
  END IF;
END isBefore;
FUNCTION addErrorMessage
  (
    p_error_msg   VARCHAR2,
    new_error_msg VARCHAR2)
  RETURN VARCHAR2
                             IS
  v_error_msg VARCHAR2(4000) := NULL;
BEGIN
  IF (p_error_msg IS NULL) THEN
    v_error_msg   := SUBSTR(new_error_msg, 1, 4000);
  ELSE
    v_error_msg := SUBSTR(p_error_msg || ';' || new_error_msg, 1, 4000);
  END IF;
  RETURN v_error_msg;
END addErrorMessage;
FUNCTION count_delimited_values
  (
    p_value     VARCHAR2,
    p_delimiter VARCHAR2)
  RETURN NUMBER
                 IS
  v_count        NUMBER := 0;
  v_index        NUMBER := 1;
  v_cur_idx      NUMBER := 1;
  v_delim_length NUMBER;
  v_value_length NUMBER;
BEGIN
  v_delim_length := LENGTH(p_delimiter);
  v_value_length := LENGTH(p_value);
  WHILE v_index  != 0 AND v_cur_idx <= v_value_length
  LOOP
    v_index      := INSTR(p_value, p_delimiter, v_cur_idx);
    IF v_index    = v_cur_idx THEN
      v_cur_idx  := v_cur_idx + v_delim_length;
    ELSIF v_index > v_cur_idx THEN
      v_cur_idx  := v_index + v_delim_length;
      v_count    := v_count +1;
    END IF;
  END LOOP;
  IF v_cur_idx <= v_value_length THEN
    v_count    := v_count+1;
  END IF;
  RETURN v_count;
END count_delimited_values;
FUNCTION get_delimited_value
  (
    p_value     VARCHAR2,
    p_delimiter VARCHAR2,
    p_index     NUMBER)
  RETURN VARCHAR2
IS
  v_start NUMBER;
  v_end   NUMBER;
BEGIN
  IF p_index = 1 THEN
    v_start := 1;
  ELSE
    v_start := INSTR(p_value, p_delimiter, 1, p_index-1) + LENGTH(p_delimiter);
  END IF;
  v_end   := INSTR(p_value, p_delimiter, 1, p_index);
  IF v_end = 0 THEN
    v_end := LENGTH(p_value)+1;
  END IF;
  RETURN SUBSTR(p_value, v_start, v_end - v_start);
END get_delimited_value;
FUNCTION getValidJobCode
  (
    p_job_code VARCHAR2)
  RETURN VARCHAR2
IS
  v_count  NUMBER;
  v_part   VARCHAR2(255);
  v_result VARCHAR2(255);
BEGIN
  v_count   := common_utils.count_delimited_values(p_job_code, '-');
  v_result  := '';
  IF v_count < 2 OR v_count > 5 THEN
    RETURN p_job_code;
  END IF;
  IF v_count    > 1 THEN
    v_part     := common_utils.get_delimited_value(p_job_code, '-', 1);
    IF v_part  != '0000' THEN
      v_result := v_part;
    END IF;
  END IF;
  IF v_count    > 2 THEN
    v_part     := common_utils.get_delimited_value(p_job_code, '-', 2);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  IF v_count    > 3 THEN
    v_part     := common_utils.get_delimited_value(p_job_code, '-', 3);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  IF v_count    > 4 THEN
    v_part     := common_utils.get_delimited_value(p_job_code, '-', 4);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  v_part   := common_utils.get_delimited_value(p_job_code, '-', v_count);
  v_result := v_result || '-' || v_part;
  RETURN v_result;
EXCEPTION
WHEN OTHERS THEN
  RETURN p_job_code;
END getValidJobCode;
FUNCTION getValidFaultCode
  (
    p_fault_code VARCHAR2)
  RETURN VARCHAR2
IS
  v_count  NUMBER;
  v_part   VARCHAR2(255);
  v_result VARCHAR2(255);
BEGIN
  v_count   := common_utils.count_delimited_values(p_fault_code, '-');
  v_result  := '';
  IF v_count < 1 OR v_count > 4 THEN
    RETURN p_fault_code;
  END IF;
  IF v_count    > 0 THEN
    v_part     := common_utils.get_delimited_value(p_fault_code, '-', 1);
    IF v_part  != '0000' THEN
      v_result := v_part;
    END IF;
  END IF;
  IF v_count    > 1 THEN
    v_part     := common_utils.get_delimited_value(p_fault_code, '-', 2);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  IF v_count    > 2 THEN
    v_part     := common_utils.get_delimited_value(p_fault_code, '-', 3);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  IF v_count    > 3 THEN
    v_part     := common_utils.get_delimited_value(p_fault_code, '-', 4);
    IF v_part  != '0000' THEN
      v_result := v_result || '-' || v_part;
    END IF;
  END IF;
  RETURN v_result;
EXCEPTION
WHEN OTHERS THEN
  RETURN p_fault_code;
END getValidFaultCode;

--This is will count the empty values as well.
FUNCTION count_delimited_values_new(p_value VARCHAR2, p_delimiter VARCHAR2)
RETURN NUMBER
IS
  v_count         NUMBER := 0;
  v_index         NUMBER := 1;
  v_cur_idx       NUMBER := 1;
  v_delim_length  NUMBER;
  v_value_length  NUMBER;
BEGIN

  v_delim_length := LENGTH(p_delimiter);
  v_value_length := LENGTH(p_value);

  WHILE v_index != 0 AND v_cur_idx <= v_value_length LOOP
    v_index := INSTR(p_value, p_delimiter, v_cur_idx);
    IF v_index = v_cur_idx THEN
      v_cur_idx := v_cur_idx + v_delim_length;
      v_count := v_count+1;
    ELSIF v_index > v_cur_idx THEN
      v_cur_idx := v_index + v_delim_length;
      v_count := v_count+1;
    END IF;
  END LOOP;
  
  v_count := v_count+1;

  RETURN v_count;
END count_delimited_values_new;

END Common_Utils;