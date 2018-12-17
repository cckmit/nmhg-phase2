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
package tavant.twms.domain.claim;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

/**
 * Comment object made by a user
 *
 * @author kannan.ekanath
 */
@Entity
@Filters({
        @Filter(name = "excludeInactive")
})
@AccessType("field")
public class UserComment implements Comparable<UserComment>, AuditableColumns {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private User madeBy;

    @Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint madeOn;

    @Column(name = "user_comment")
    private String comment;

    private boolean internalComment;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public UserComment() {
        // for hibernate
    }

    public UserComment(String comment) {
        this(null, comment);
    }

    public UserComment(User madeBy, String comment) {
        super();
        this.madeBy = madeBy;
        this.comment = comment;
        this.madeOn = Clock.now();
        this.internalComment = false;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setMadeBy(User madeBy) {
        this.madeBy = madeBy;
    }

    public void setMadeOn(TimePoint madeOn) {
        this.madeOn = madeOn;
    }

    public String getComment() {
        return this.comment;
    }

    public User getMadeBy() {
        return this.madeBy;
    }

    public TimePoint getMadeOn() {
        return this.madeOn;
    }

    public boolean isInternalComment() {
        return this.internalComment;
    }

    public void setInternalComment(boolean internalComment) {
        this.internalComment = internalComment;
    }

    public int compareTo(UserComment o) {
        // latest date first
        return new CompareToBuilder().append(o.getMadeOn(), getMadeOn()).toComparison();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("made on ", this.madeOn)
                .append("made by", this.madeBy).toString();
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public UserComment clone() {
        UserComment userComment = new UserComment();
        userComment.setComment(comment);
        userComment.setInternalComment(internalComment);
        userComment.setMadeBy(madeBy);
        userComment.setMadeOn(madeOn);
        return userComment;
    }
}
