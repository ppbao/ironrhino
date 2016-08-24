package org.ironrhino.common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.common.record.RecordAware;
import org.ironrhino.core.aop.PublishAware;
import org.ironrhino.core.hibernate.CreationUser;
import org.ironrhino.core.hibernate.UpdateUser;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.CaseInsensitive;
import org.ironrhino.core.metadata.NotInCopy;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;
import org.ironrhino.core.search.elasticsearch.annotations.Searchable;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableProperty;

@RecordAware
@PublishAware
@AutoConfig
@Searchable
@Entity
@Table(name = "common_setting")
@Richtable(searchable = true, readonly = @Readonly(expression = "entity.readonly"), order = "key asc", exportable = true, importable = true)
public class Setting extends BaseEntity {

	private static final long serialVersionUID = -8352037603261222984L;

	@UiConfig(width = "300px")
	@SearchableProperty(boost = 3)
	@CaseInsensitive
	@NaturalId(mutable = true)
	@Column(name = "`key`", nullable = false)
	private String key = "";

	@UiConfig(type = "textarea", width = "400px")
	@SearchableProperty
	@Column(length = 4000)
	private String value = "";

	@UiConfig(type = "textarea")
	@SearchableProperty
	@Column(length = 4000)
	private String description = "";

	@UiConfig(hidden = true)
	private boolean readonly;

	@UiConfig(hidden = true)
	private boolean hidden;

	@NotInCopy
	@UiConfig(hidden = true)
	@Column(updatable = false)
	@CreationTimestamp
	private Date createDate;

	@NotInCopy
	@UiConfig(hidden = true)
	@Column(insertable = false)
	@UpdateTimestamp
	private Date modifyDate;

	@NotInCopy
	@UiConfig(hidden = true)
	@Column(updatable = false)
	@CreationUser
	private String createUser;

	@NotInCopy
	@UiConfig(hidden = true)
	@Column(insertable = false)
	@UpdateUser
	private String modifyUser;

	@Version
	private int version = -1;

	public Setting() {

	}

	public Setting(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
