package org.ironrhino.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.hibernate.CreationUser;
import org.ironrhino.core.hibernate.UpdateUser;
import org.ironrhino.core.hibernate.convert.StringSetConverter;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.NotInCopy;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;
import org.ironrhino.core.model.Ordered;
import org.ironrhino.core.search.elasticsearch.annotations.Index;
import org.ironrhino.core.search.elasticsearch.annotations.Searchable;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Searchable
@AutoConfig
@Entity
@Table(name = "common_page")
@Richtable(searchable = true)
public class Page extends BaseEntity implements Ordered<Page> {

	private static final long serialVersionUID = 4688382703803043164L;

	@SearchableProperty(index = Index.NOT_ANALYZED)
	@Column(nullable = false)
	@NaturalId
	@UiConfig(alias = "path")
	private String path;

	@SearchableProperty
	private String title;

	@JsonIgnore
	@Lob
	@UiConfig(hidden = true, excludedFromCriteria = true)
	private String head;

	@JsonIgnore
	@SearchableProperty
	@Lob
	@Column(nullable = false)
	@UiConfig(hiddenInList = @Hidden(true))
	private String content;

	@SearchableProperty
	@UiConfig(width = "100px")
	private int displayOrder;

	@JsonIgnore
	@Lob
	@UiConfig(hidden = true)
	private String draft;

	@UiConfig(hidden = true)
	private Date draftDate;

	@SearchableProperty(index = Index.NOT_ANALYZED)
	@Convert(converter = StringSetConverter.class)
	private Set<String> tags = new LinkedHashSet<>(0);

	@NotInCopy
	@SearchableProperty
	@Column(updatable = false)
	@CreationTimestamp
	private Date createDate;

	@NotInCopy
	@SearchableProperty
	@Column(insertable = false)
	@UpdateTimestamp
	private Date modifyDate;

	@NotInCopy
	@SearchableProperty(include_in_all = false)
	@UiConfig(hidden = true)
	@Column(updatable = false)
	@CreationUser
	private String createUser;

	@NotInCopy
	@SearchableProperty(include_in_all = false)
	@UiConfig(hidden = true)
	@Column(insertable = false)
	@UpdateUser
	private String modifyUser;

	@Version
	private int version = -1;

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDraft() {
		return draft;
	}

	public void setDraft(String draft) {
		this.draft = draft;
	}

	public Date getDraftDate() {
		return draftDate;
	}

	public void setDraftDate(Date draftDate) {
		this.draftDate = draftDate;
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

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
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

	public String abbreviate(int size) {
		return StringUtils.isNotBlank(content) ? StringUtils.abbreviate(Jsoup.parse(content).text().trim(), size).trim()
				: null;
	}

	public List<Image> getImages() {
		List<Image> images = new ArrayList<>();
		if (StringUtils.isNotBlank(content)) {
			Elements elements = Jsoup.parse(content).select("img");
			for (int i = 0; i < elements.size(); i++) {
				Element img = elements.get(i);
				Image image = new Image();
				image.setSrc(img.attr("src"));
				image.setAlt(img.attr("alt"));
				image.setTitle(img.attr("title"));
				images.add(image);
			}
		}
		return images;
	}

	public static class Image implements Serializable {

		private static final long serialVersionUID = -3425565099362299759L;
		private String src;
		private String alt;
		private String title;

		public String getSrc() {
			return src;
		}

		public void setSrc(String src) {
			this.src = src;
		}

		public String getAlt() {
			return alt;
		}

		public void setAlt(String alt) {
			this.alt = alt;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	@Override
	public int compareTo(Page page) {
		if (page == null)
			return 1;
		if (this.getDisplayOrder() != page.getDisplayOrder())
			return this.getDisplayOrder() - page.getDisplayOrder();
		return this.toString().compareTo(page.toString());
	}

	@Override
	public String toString() {
		return StringUtils.defaultString(this.path);
	}
}
