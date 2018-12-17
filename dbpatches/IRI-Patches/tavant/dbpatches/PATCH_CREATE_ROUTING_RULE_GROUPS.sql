--Purpose : Create Routing rule groups
--Author : raghuram.d
--Date : 19-Jul-09

CREATE OR REPLACE PROCEDURE create_default_rule_group (
            p_bu_info VARCHAR2,
            p_context VARCHAR2,
            p_name VARCHAR2)

AS
    CURSOR rules_rec(group_id NUMBER) IS
    SELECT id FROM domain_rule WHERE rule_group = group_id;

    v_count             NUMBER;
    v_rule_group_id     NUMBER;
    v_priority          NUMBER;
    v_rule_grp_priority NUMBER;
    v_execute           NUMBER(1);
BEGIN
    v_count := 0;
    v_execute := 0;

    SELECT COUNT(*) INTO v_count FROM domain_rule 
    WHERE business_unit_info = p_bu_info 
        AND context=p_context
        AND rule_group IS NULL;
    IF v_count > 0 THEN
        v_execute := 1;
    END IF;

    v_count := 0;
    SELECT COUNT(*) INTO v_count FROM domain_rule_group
    WHERE business_unit_info = p_bu_info 
        AND context=p_context;
    IF v_count = 0 THEN
        v_execute := 1;
    END IF;

    IF v_execute = 1 THEN
        SELECT domain_rule_group_seq.NEXTVAL INTO v_rule_group_id FROM DUAL;
        BEGIN
            SELECT COALESCE(MAX(priority)+1,1) INTO v_rule_grp_priority FROM domain_rule_group
            WHERE business_unit_info=p_bu_info AND context=p_context;
        EXCEPTION 
            WHEN OTHERS THEN
                v_rule_grp_priority := 1;
        END;
        INSERT INTO domain_rule_group (id, priority, stop_rule_proc_on_success, stop_rule_proc_on_first_succ, use_as_default, 
                context, name, description, business_unit_info,stop_rule_proc_on_no_result,stop_rule_proc_on_multi_result,
                d_active,status)
        VALUES(v_rule_group_id , v_rule_grp_priority, 0, 0, 0,
                p_context, p_name, p_name, p_bu_info, 1, 0,
                1,'ACTIVE');

        UPDATE domain_rule SET rule_group = v_rule_group_id, context = NULL 
        WHERE context = p_context AND business_unit_info = p_bu_info AND rule_group IS NULL;

        COMMIT;

        v_priority := 1;
        FOR each_rec IN rules_rec(v_rule_group_id)
        LOOP
            UPDATE domain_rule SET priority = v_priority WHERE id = each_rec.id;
            v_priority := v_priority + 1;
        END LOOP;
        COMMIT;
    END IF;
END create_default_rule_group;
/
BEGIN
    create_default_rule_group('AIR', 'ClaimProcessorRouting', 'PROCESSOR ROUTING RULE GROUP');
    create_default_rule_group('AIR', 'DSMRouting', 'DSM ROUTING RULE GROUP');
    create_default_rule_group('AIR', 'DSMAdvisorRouting', 'ADVISOR ROUTING RULE GROUP');

    create_default_rule_group('Transport Solutions ESA', 'ClaimProcessorRouting', 'PROCESSOR ROUTING RULE GROUP');
    create_default_rule_group('Transport Solutions ESA', 'DSMRouting', 'DSM ROUTING RULE GROUP');
    create_default_rule_group('Transport Solutions ESA', 'DSMAdvisorRouting', 'ADVISOR ROUTING RULE GROUP');

    create_default_rule_group('TFM', 'ClaimProcessorRouting', 'PROCESSOR ROUTING RULE GROUP');
    create_default_rule_group('TFM', 'DSMRouting', 'DSM ROUTING RULE GROUP');
    create_default_rule_group('TFM', 'DSMAdvisorRouting', 'ADVISOR ROUTING RULE GROUP');

    create_default_rule_group('Clubcar ESA', 'ClaimProcessorRouting', 'PROCESSOR ROUTING RULE GROUP');
    create_default_rule_group('Clubcar ESA', 'DSMRouting', 'DSM ROUTING RULE GROUP');
    create_default_rule_group('Clubcar ESA', 'DSMAdvisorRouting', 'ADVISOR ROUTING RULE GROUP');

    create_default_rule_group('Hussmann', 'ClaimProcessorRouting', 'PROCESSOR ROUTING RULE GROUP');
    create_default_rule_group('Hussmann', 'DSMRouting', 'DSM ROUTING RULE GROUP');
    create_default_rule_group('Hussmann', 'DSMAdvisorRouting', 'ADVISOR ROUTING RULE GROUP');
END;
/



