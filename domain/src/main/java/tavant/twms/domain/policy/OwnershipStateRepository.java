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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Apr 5, 2007
 * Time: 1:03:58 AM
 */

package tavant.twms.domain.policy;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface OwnershipStateRepository extends GenericRepository<OwnershipState, Long> {

    List<OwnershipState> findAllOwnershipStates();

    OwnershipState findOwnershipStateByName(String ownerShipName);

}
