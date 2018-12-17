/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.reports;

import java.util.List;
import java.util.Map;

/**
 * @author kamal.govindraj
 *
 */
public interface TREADReportRepository {

	List<Map<String, Object>> getClaimsInfo(int year, int quater);

	List<Map<String, Object>> getProductionInfo(int year, int quater);

	List<Map<String, Object>> getConsumerComplaintsInfo(int year, int quarter);

	List<Map<String, Object>> getFieldReportsInfo(int year, int quarter);

	List<Map<String, Object>> getPropertyDamageInfo(int year, int quarter);
}
