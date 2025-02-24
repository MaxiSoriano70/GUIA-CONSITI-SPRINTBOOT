package com.crud.consiti.consitiCrud.security;

import com.crud.consiti.consitiCrud.util.ERolNombre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ERolNombre rolNombre;

    public Rol(ERolNombre rolNombre) {
        this.rolNombre = rolNombre;
    }

    public Rol() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ERolNombre getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(ERolNombre rolNombre) {
        this.rolNombre = rolNombre;
    }
}
