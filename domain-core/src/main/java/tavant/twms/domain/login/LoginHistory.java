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

package tavant.twms.domain.login;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.orgmodel.User;

@SuppressWarnings("serial")
@Entity
public class LoginHistory implements Serializable{
	@Id
	@GeneratedValue(generator = "LoginHistory")
	@GenericGenerator(name = "LoginHistory", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "Login_History_SEQ"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User loggedInUser;

	private Date loggedInTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public Date getLoggedInTime() {
		return loggedInTime;
	}

	public void setLoggedInTime(Date loggedInTime) {
		this.loggedInTime = loggedInTime;
	}
	
}
