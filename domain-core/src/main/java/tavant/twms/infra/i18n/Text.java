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

package tavant.twms.infra.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.MapKey;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Text implements AuditableColumns{

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    private String defaultText;

    private String fieldName;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Text() {
    }

    public Text(String fieldName) {
        this.fieldName = fieldName;
    }

    @OneToMany
    @MapKey(columns = @Column(name = "locale"))
    @JoinColumn(name = "text_id", nullable = false)
    @Cascade(CascadeType.ALL)
    private Map<Locale, I18nText> i18nTexts = new HashMap<Locale, I18nText>();

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

    public String getText() {
        if (UserLocale.isDefaultLocale()) {
            return this.defaultText;
        } else {
            return this.i18nTexts.get(UserLocale.getUserLocale()).getText();
        }
    }

    public void setText(String text) {
        if (UserLocale.isDefaultLocale()) {
            this.defaultText = text;
        } else {
            I18nText i18nText = this.i18nTexts.get(UserLocale.getUserLocale());
            if (i18nText == null) {
                this.i18nTexts.put(UserLocale.getUserLocale(), new I18nText(UserLocale
                        .getUserLocale(), text, this.fieldName));
            } else {
                i18nText.setText(text);
            }
        }
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
