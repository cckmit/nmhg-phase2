--PURPOSE    : patch_to_change_campaign_items_to_sno_coverage
--AUTHOR     : Raghavendra
--CREATED ON : 01-OCT-13



DECLARE
v_rec1 NUMBER;
v_rec2 NUMBER;
v_seq_val NUMBER;
CURSOR c1
IS
	SELECT distinct(campaign_coverage) FROM campaign_coverage_items ;
CURSOR c2(v_rec1 number) IS
	SELECT items FROM campaign_coverage_items WHERE campaign_coverage=v_rec1;
BEGIN
	FOR i1 IN c1
	LOOP
		SELECT CAMPAIGN_SN_COVERAGE_SEQ.NEXTVAL INTO v_seq_val FROM dual;
		SELECT DISTINCT(campaign_coverage) INTO v_rec1 FROM campaign_coverage_items WHERE campaign_coverage=i1.campaign_coverage;
		INSERT INTO CAMPAIGN_SNO_COVERAGE VALUES(v_seq_val);
		UPDATE campaign_coverage SET SERIAL_NUMBER_COVERAGE=v_seq_val WHERE id=i1.campaign_coverage;
		FOR i2 IN c2(v_rec1)
			LOOP
				INSERT INTO campaign_sno_coverage_items VALUES(v_seq_val,i2.items);
			END LOOP;
	END LOOP;
END;