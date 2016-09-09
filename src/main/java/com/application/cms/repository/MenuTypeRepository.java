package com.application.cms.repository;

import com.application.cms.domain.MenuType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the MenuType entity.
 */
@SuppressWarnings("unused")
public interface MenuTypeRepository extends JpaRepository<MenuType,Long> {

}
