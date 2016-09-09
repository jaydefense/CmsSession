package com.application.cms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Menu.
 */
@Entity
@Table(name = "menu")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "access")
    private Integer access;

    @Column(name = "alias")
    private String alias;

    @Column(name = "home")
    private Boolean home;

    @Column(name = "img")
    private String img;

    @Column(name = "level")
    private Integer level;

    @Column(name = "link")
    private String link;

    @Column(name = "params")
    private String params;

    @Column(name = "path")
    private String path;

    @Column(name = "published")
    private Boolean published;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Menu menu;

    @OneToMany(mappedBy = "menu")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Menu> submenus = new HashSet<>();

    @ManyToOne
    private MenuType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAccess() {
        return access;
    }

    public Menu access(Integer access) {
        this.access = access;
        return this;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public String getAlias() {
        return alias;
    }

    public Menu alias(String alias) {
        this.alias = alias;
        return this;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean isHome() {
        return home;
    }

    public Menu home(Boolean home) {
        this.home = home;
        return this;
    }

    public void setHome(Boolean home) {
        this.home = home;
    }

    public String getImg() {
        return img;
    }

    public Menu img(String img) {
        this.img = img;
        return this;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getLevel() {
        return level;
    }

    public Menu level(Integer level) {
        this.level = level;
        return this;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLink() {
        return link;
    }

    public Menu link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getParams() {
        return params;
    }

    public Menu params(String params) {
        this.params = params;
        return this;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPath() {
        return path;
    }

    public Menu path(String path) {
        this.path = path;
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean isPublished() {
        return published;
    }

    public Menu published(Boolean published) {
        this.published = published;
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public Menu title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Menu description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Menu getMenu() {
        return menu;
    }

    public Menu menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Set<Menu> getSubmenus() {
        return submenus;
    }

    public Menu submenus(Set<Menu> menus) {
        this.submenus = menus;
        return this;
    }

    public Menu addMenu(Menu menu) {
        submenus.add(menu);
        menu.setMenu(this);
        return this;
    }

    public Menu removeMenu(Menu menu) {
        submenus.remove(menu);
        menu.setMenu(null);
        return this;
    }

    public void setSubmenus(Set<Menu> menus) {
        this.submenus = menus;
    }

    public MenuType getType() {
        return type;
    }

    public Menu type(MenuType menuType) {
        this.type = menuType;
        return this;
    }

    public void setType(MenuType menuType) {
        this.type = menuType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        if(menu.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Menu{" +
            "id=" + id +
            ", access='" + access + "'" +
            ", alias='" + alias + "'" +
            ", home='" + home + "'" +
            ", img='" + img + "'" +
            ", level='" + level + "'" +
            ", link='" + link + "'" +
            ", params='" + params + "'" +
            ", path='" + path + "'" +
            ", published='" + published + "'" +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
