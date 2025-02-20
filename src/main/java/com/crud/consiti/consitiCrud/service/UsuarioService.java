package com.crud.consiti.consitiCrud.service;

import com.crud.consiti.consitiCrud.repository.IUsuarioRepository;
import com.crud.consiti.consitiCrud.security.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    public Optional<Usuario> getByNombreUsuario(String nombre){
        return usuarioRepository.findByNombreUsuario(nombre);
    }

    public boolean existsByNombreUsuario(String nombre){
        return usuarioRepository.existsByNombreUsuario(nombre);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }
}
