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
package tavant.twms.domain.orgmodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tavant.twms.infra.DomainRepositoryTestCase;

public class UserRepositoryImplTest extends DomainRepositoryTestCase {
    UserRepository userRepository;
    
    public void testFindAllUsers() {
        Set<User> users = this.userRepository.findAllUsers();
        assertEquals(35, users.size());
    }
    
    public void testUsersUserGroup() {
        User user = this.userRepository.findByName("alissa");
        assertEquals("alissa", user.getName());
        Set<UserGroup> userGroup = user.getUserGroups();
        assertEquals(2, userGroup.size());
        user = this.userRepository.findByName("ann");
        assertEquals("ann", user.getName());
        userGroup = user.getUserGroups();
        assertEquals(1, userGroup.size());
        assertEquals("Excavator Processors", userGroup.iterator().next().getName());
    }

    public void testfindUsersBelongingToRole() {
        Set<User> users = this.userRepository.findUsersBelongingToRole("dealer");
        assertEquals(8, users.size());
        assertTrue(containsUser(users, "bishop"));
        assertTrue(containsUser(users, "sandy"));
    }

    public void testFindUserBelongingToRoleWithNonExistentRole() {
        Set<User> users = this.userRepository.findUsersBelongingToRole("abcdefg");
        assertEquals(0, users.size());
    }

    public void testFindUserByName() {
        User user = this.userRepository.findByName("alissa");
        assertEquals("alissa", user.getName());
        assertEquals(2, user.getRoles().size());
        assertTrue(containsRole(user.getRoles(), "processor"));
        assertTrue(containsRole(user.getRoles(), "admin"));
    }
    public void testFindAllBUs() {
        User user = userRepository.findByName("alissa");
        assertEquals(3, user.getBusinessUnits().size());
    }

    public void testFindUserByNameNonExistentUser() {
        User user = this.userRepository.findByName("xyzpqr");
        assertNull(user);
    }

    public void testFindUserByNameUserDoesntHaveRoleAssigned() {
        User user = this.userRepository.findByName("lynn");
        assertNotNull(user);
        assertEquals("lynn", user.getName());
        assertEquals(0, user.getRoles().size());
    }

    public void testFindUserByIdVerifySupervisor() {
        User user = this.userRepository.findById(new Long(3));
        assertNotNull(user);
        assertEquals("ann", user.getName());
        assertNotNull(user.getSupervisor());
        assertEquals("alissa", user.getSupervisor().getName());
    }

    public void testFindUserByIdUserWithoutSupervisor() {
        User user = this.userRepository.findById(new Long(4));
        assertNotNull(user);
        assertEquals("alissa", user.getName());
        assertNull(user.getSupervisor());
    }

    public void testUserAttributes() {
        User ann = this.userRepository.findByName("ann");
        assertTrue(ann.hasAttribute("Language", "English"));
        assertTrue(ann.hasAttribute("Language", "Spanish"));

        User alissa = this.userRepository.findByName("alissa");
        assertTrue(alissa.hasAttribute("Language", "English"));
        assertTrue(alissa.hasAttribute("Language", "German"));
        assertFalse(alissa.hasAttribute("Language", "Spanish"));
    }

    private boolean containsRole(Set<Role> roles, String roleName) {
        for (Iterator iter = roles.iterator(); iter.hasNext();) {
            Role role = (Role) iter.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsUser(Set<User> users, String userName) {
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            if (((User) iter.next()).getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }
    
    public void testFindUserNamesStartingWith() {
    	List<String> names = this.userRepository.findUsersWithNameLike("a", 0, 10,"Club Car");
    	assertEquals(5, names.size());
    }
    
/*    public void _testFindTechnicianForDealer() {
        List<User> technicians= this.userRepository.findTechnicianForDealer(new Long(10));
        assertEquals(1,technicians.size());
    }
    
    public void _testFindTechnicianForDealers() {
    	Set<Long> ids = new HashSet<Long>();
    	ids.add(new Long(10));
        List<User> technicians= this.userRepository.findTechnicianForDealers(ids);
        assertEquals(1,technicians.size());
    }*/

    /**
     * @param userRepository
     *            the userRepository to set
     */
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
