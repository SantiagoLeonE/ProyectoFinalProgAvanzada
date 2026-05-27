package co.edu.uniquindio.gestionacademica.security;

import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtService {

    //Leer la clave secreta desde application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    //Leer el tiempo de expiración desde application.properties
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    //Generar un token JWT para el usuario autenticado. El token contiene el email y el rol del usuario
    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .claim("id", usuario.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    //Extraer el email del usuario desde el token
    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    //Validar que el token sea válido y pertenezca al usuario
    public boolean tokenEsValido(String token, UserDetails userDetails) {
        String email = extraerEmail(token);
        return email.equals(userDetails.getUsername()) && !tokenEstaExpirado(token);
    }

    //Verificar si el token ha expirado
    private boolean tokenEstaExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    //Extraer toda la información del token
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //Convertir la clave secreta en formato seguro para firmar el token
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                Base64.getEncoder().encodeToString(secretKey.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
