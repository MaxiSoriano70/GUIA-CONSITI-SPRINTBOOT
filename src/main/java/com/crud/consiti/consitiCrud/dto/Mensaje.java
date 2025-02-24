package com.crud.consiti.consitiCrud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mensaje {

    private String contenido;

    // Constructor adicional, si prefieres tener un constructor con solo el mensaje
    public Mensaje(String mensaje) {
        this.contenido = mensaje;
    }
}
