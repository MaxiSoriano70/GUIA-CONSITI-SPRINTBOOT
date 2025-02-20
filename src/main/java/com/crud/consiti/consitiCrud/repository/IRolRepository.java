package com.crud.consiti.consitiCrud.repository;

import com.crud.consiti.consitiCrud.security.Rol;
import com.crud.consiti.consitiCrud.util.ERolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolNombre(ERolNombre rolNombre);
}
