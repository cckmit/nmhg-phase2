--To remove dutch from the list of locales used in profile management
--Missed the patch for function getFaultCodeName for download management

delete from product_locale where locale='nl_NL'
/
create or replace function getFaultCodeName(p_fault_code NUMBER)
RETURN VARCHAR2
IS
  CURSOR components(v_fault_code NUMBER) IS
    SELECT a.name
    FROM fault_code fc, fault_code_def_comps comps, assembly_definition a
    WHERE fc.id = v_fault_code
      AND fc.definition = comps.fault_code_definition
      AND comps.components = a.id
    ORDER BY comps.list_index;
  v_name  VARCHAR2(255) := NULL;
BEGIN
  FOR comp IN components(p_fault_code) LOOP
    IF v_name IS NULL THEN
      v_name := comp.name;
    ELSE
      v_name := v_name || ' - ' || comp.name;
    END IF;
  END LOOP;
  RETURN v_name;
END getFaultCodeName;
/
commit
/