package com.crud.consiti.consitiCrud.service;

import com.crud.consiti.consitiCrud.repository.IRolRepository;
import com.crud.consiti.consitiCrud.security.Rol;
import com.crud.consiti.consitiCrud.util.ERolNombre;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RolService {
    @Autowired
    private IRolRepository rolRepository;

    public Optional<Rol> getByRolNombre(ERolNombre rolNombre){
        return rolRepository.findByRolNombre(rolNombre);
    }

    public void save(Rol rol){
        rolRepository.save(rol);
    }
}
