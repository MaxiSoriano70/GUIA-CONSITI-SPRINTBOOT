package com.crud.consiti.consitiCrud.security.controller;

import com.crud.consiti.consitiCrud.dto.Mensaje;
import com.crud.consiti.consitiCrud.dto.NuevoUsuario;
import com.crud.consiti.consitiCrud.security.Rol;
import com.crud.consiti.consitiCrud.security.Usuario;
import com.crud.consiti.consitiCrud.security.dto.JwtDto;
import com.crud.consiti.consitiCrud.security.dto.LoginUsuario;
import com.crud.consiti.consitiCrud.security.jwt.JwtProvider;
import com.crud.consiti.consitiCrud.service.RolService;
import com.crud.consiti.consitiCrud.service.UsuarioService;
import com.crud.consiti.consitiCrud.util.ERolNombre;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolService rolService;
    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("")
    public ResponseEntity<Mensaje> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Verifique los datos introducidos"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            return new ResponseEntity<>(new Mensaje("El nombre " + nuevoUsuario.getNombre() + " ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return new ResponseEntity<>(new Mensaje("El email " + nuevoUsuario.getEmail() + " ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = new Usuario(
                nuevoUsuario.getNombre(),
                nuevoUsuario.getNombreUsuario(),
                nuevoUsuario.getEmail(),
                passwordEncoder.encode(nuevoUsuario.getPassword())
        );

        Set<Rol> roles = new HashSet<>();

        // Manejo de Optional para evitar NoSuchElementException
        Rol rolUser = rolService.getByRolNombre(ERolNombre.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado"));
        roles.add(rolUser);

        if (nuevoUsuario.getRoles().contains("admin")) {
            Rol rolAdmin = rolService.getByRolNombre(ERolNombre.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado"));
            roles.add(rolAdmin);
        }

        usuario.setRoles(roles);
        usuarioService.save(usuario);

        return new ResponseEntity<>(new Mensaje("Usuario registrado con éxito"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Usuario inválido"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        JwtDto jwt = new JwtDto(token);

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException {
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}
