/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.partreturn;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author kiran.sg
 * 
 */
public class ShipmentRepositoryImpl extends GenericRepositoryImpl<Shipment, Long>  implements ShipmentRepository {

    public void updateShipment(Shipment shipment) {
        update(shipment);
    }
    
    public void reloadShipments(final List<Shipment> shipments) {
		getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						session.flush();
						for (Shipment shipment : shipments) {
							session.refresh(shipment);
						}
						return null;
					};
				});
    }

    public void updateShipments(List<Shipment> shipments) {
        updateAll(shipments);
    }

}
