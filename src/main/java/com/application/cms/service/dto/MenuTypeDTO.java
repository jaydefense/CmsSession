package com.application.cms.service.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the MenuType entity.
 */
public class MenuTypeDTO implements Serializable {

    private Long id;

    private String type;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuTypeDTO menuTypeDTO = (MenuTypeDTO) o;

        if ( ! Objects.equals(id, menuTypeDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MenuTypeDTO{" +
            "id=" + id +
            ", type='" + type + "'" +
            '}';
    }
}
