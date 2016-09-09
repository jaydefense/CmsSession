package com.application.cms.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Content.
 */
@Entity
@Table(name = "content")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "content")
public class Content implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "alias")
    private String alias;

    @Column(name = "introtext")
    private String introtext;

    @Column(name = "alltext")
    private String alltext;

    @ManyToOne
    private Language language;

    @ManyToOne
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Content title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public Content alias(String alias) {
        this.alias = alias;
        return this;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIntrotext() {
        return introtext;
    }

    public Content introtext(String introtext) {
        this.introtext = introtext;
        return this;
    }

    public void setIntrotext(String introtext) {
        this.introtext = introtext;
    }

    public String getAlltext() {
        return alltext;
    }

    public Content alltext(String alltext) {
        this.alltext = alltext;
        return this;
    }

    public void setAlltext(String alltext) {
        this.alltext = alltext;
    }

    public Language getLanguage() {
        return language;
    }

    public Content language(Language language) {
        this.language = language;
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Category getCategory() {
        return category;
    }

    public Content category(Category category) {
        this.category = category;
        return this;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Content content = (Content) o;
        if(content.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Content{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", alias='" + alias + "'" +
            ", introtext='" + introtext + "'" +
            ", alltext='" + alltext + "'" +
            '}';
    }
}
