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

package tavant.twms.integration.adapter.mockerp;

import org.openapplications.oagis.x9.ApplicationAreaType;
import tavant.oagis.AddressDTO;
import tavant.oagis.AttributeDTO;
import tavant.oagis.SyncUserDTO;
import tavant.oagis.SyncUserDataAreaDTO;
import tavant.oagis.SyncUserDocumentDTO;
import tavant.oagis.UserDTO;
import tavant.oagis.UserTypeDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SyncUser {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<User> users = new ArrayList<User>();
        for (SyncTracker syncTracker : syncTrackers) {
            users.add((User) genericDao.findById(User.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(users);
    }

    private String transform(List<User> users) {
        SyncUserDocumentDTO syncUserDocumentDTO = SyncUserDocumentDTO.Factory.newInstance();
        createSyncUser(syncUserDocumentDTO, users);
        return syncUserDocumentDTO.toString();
    }

    private void createSyncUser(SyncUserDocumentDTO syncUserDocumentDTO, List<User> users) {
        SyncUserDTO syncUserDTO = syncUserDocumentDTO.addNewSyncUser();
        createApplicationArea(syncUserDTO);
        createDataArea(syncUserDTO, users);
    }

    private void createApplicationArea(SyncUserDTO syncUserDTO) {
        ApplicationAreaType applicationArea = syncUserDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncUserDTO syncUserDTO, List<User> users) {
        SyncUserDataAreaDTO dataArea = syncUserDTO.addNewDataArea();
        createSync(dataArea);
        createUsers(users, dataArea);
    }

    private void createSync(SyncUserDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createUsers(List<User> users, SyncUserDataAreaDTO dataArea) {
        for (User user : users) {
            UserDTO userDTO = dataArea.addNewUser();
            userDTO.setBusinessId(""+user.getId());
            userDTO.setName(user.getName());
            userDTO.setUserId(user.getUserId());
            userDTO.setEmail(user.getEmail());

            if ("Dealer".equals(user.getUserType())) {
                userDTO.setUserType(UserTypeDTO.DEALER);
                userDTO.setDealerNumber(user.getDealerNumber());
            } else {
                userDTO.setUserType(UserTypeDTO.OEM);
            }

            AddressDTO addressDTO = userDTO.addNewAddress();
            addressDTO.setAddressline1(user.getAddressLine1());
            addressDTO.setAddressline2(user.getAddressLine2());
            addressDTO.setCity(user.getCity());
            addressDTO.setCountry(user.getCountry());
            addressDTO.setEmail(user.getEmail());
            addressDTO.setPhone(user.getPhone());
            addressDTO.setSecondaryemail(user.getSecondaryEmail());
            addressDTO.setSecondaryphone(user.getSecondaryPhone());
            addressDTO.setState(user.getState());
            addressDTO.setZipcode(user.getZipcode());

            if (user.getAttributeName() != null && user.getAttributeValue() != null) {
                AttributeDTO attributeDTO = userDTO.addNewAttribute();
                attributeDTO.setName(user.getAttributeName());
                attributeDTO.setValue(user.getAttributeValue());
            }
        }
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
}
