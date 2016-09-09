package com.application.cms.service.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the Role entity.
 */
public class RoleDTO implements Serializable {

    private Long id;

    private String role;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleDTO roleDTO = (RoleDTO) o;

        if ( ! Objects.equals(id, roleDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
            "id=" + id +
            ", role='" + role + "'" +
            '}';
    }
}
