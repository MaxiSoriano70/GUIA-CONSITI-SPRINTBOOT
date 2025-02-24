package com.crud.consiti.consitiCrud.security.jwt;

import com.crud.consiti.consitiCrud.security.dto.JwtDto;
import com.crud.consiti.consitiCrud.security.dto.UsuarioPrincipal;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;
    /*
    * @param authentication
    * @return Un token que contiene username, los roles, fecha de expiracion y una firma
    * */
    public String generateToken(Authentication authentication){
        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
        List<String> roles = usuarioPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(usuarioPrincipal.getUsername())
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+expiration))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }
    /*
    *
    * @param token
    * @return el username en el token
    * */
    public String getNombreUsuarioFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(secret.getBytes())  // Convierte la clave secreta a bytes
                .parseClaimsJws(token);

        return claimsJws.getBody().getSubject(); // Extrae el nombre de usuario del token
    }

    /*
    * @param token
    * return
    * */
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Token mal formato");
        }catch (UnsupportedJwtException e){
            logger.error("Token no soporte");
        }catch (ExpiredJwtException e){
            logger.error("Token no expirado");
        }catch (IllegalArgumentException e){
            logger.error("Token vacio");
        }catch (SignatureException e){
            logger.error("Error en la firma");
        }
        return false;
    }
    /*
    * @param jwtDto
    * @return un nuevo token en caso de que se le pase como argumento ya expirado
    * */
    public String refreshToken(JwtDto jwtDto) throws ParseException{
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtDto.getToken());
        }catch (ExpiredJwtException e){
            JWT jwt = JWTParser.parse(jwtDto.getToken());
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String nombreUsuario = claims.getSubject();
            List <String> roles = (List<String>) claims.getClaim("roles");
            return Jwts.builder()
                    .setSubject(nombreUsuario)
                    .claim("roles",roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime()+expiration))
                    .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                    .compact();
        }
        return null;
    }
}
