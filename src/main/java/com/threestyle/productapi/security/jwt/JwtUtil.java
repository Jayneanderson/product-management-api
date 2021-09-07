package com.threestyle.productapi.security.jwt;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;;

//esta classe utiliza a lib JSON Web Token
public class JwtUtil {
	/*
	 * Esta classe tem métodos para gerar, validar um token
	 */

	/*
	 * Esta chave foi gerada com algoritmo HS512 nesse site:
	 * https://www.allkeysgenerator.com/
	 */
	public static final String JWT_SECRET = "!A%D*G-KaPdSgVkYp2s5v8y/B?E(H+MbQeThWmZq4t6w9z$C&F)J@NcRfUjXn2r5";

	// método para pegar as credenciais
	public static Claims getClaims(String token) {
		byte[] bytesSigningKey = getSigningKey();
		token = token.replace("Bearer", "");

		return Jwts.parser()// pego a chave que foi gerada e faço um parse
				.setSigningKey(bytesSigningKey).parseClaimsJws(token).getBody();
	}

	public static String getLogin(String token) {
		Claims claims = getClaims(token);
		return !isNull(claims) ? claims.getSubject() : null;
	}

	// método para converter dias em milisegundos
	private static final Date getExpiration() {
		int days = 10;
		long time = days * 24 /* horas */ * 60 /* minutos */
				* 60 /* segundos */ * 1000 /* milisegundos */;
		return new Date(System.currentTimeMillis() + time);
	}

	public static List<GrantedAuthority> getRoles(String token) {
		Claims claims = getClaims(token);

		if (claims == null) {
			return null;
		}
		return ((List<?>) claims.get("rol")).stream().map(authority -> new SimpleGrantedAuthority((String) authority))
				.collect(Collectors.toList());

	}

	public static boolean isTokenValid(String token) {
		Claims claims = getClaims(token);
		if (nonNull(claims)) {
			String login = claims.getSubject();
			Date expiration = claims.getExpiration();
			Date now = new Date(System.currentTimeMillis());
			return login != null && expiration != null && now.before(expiration);
		}

		return false;
	}

	private static byte[] getSigningKey() {
		return JWT_SECRET.getBytes();
	}

	public static String createToken(UserDetails user) {

		// aqui eu pego a lita de roles
		List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		// aqui eu pego os bytes da minha chave gerada
		byte[] bytesSigningKey = getSigningKey();

		// aqui é a API da lib JSON Web Token
		return Jwts.builder() // chamo o builder
				// assino com minha chave gerada e coloco que ela é de 512 bits
				.signWith(Keys.hmacShaKeyFor(bytesSigningKey), SignatureAlgorithm.HS512)
				// aqui eu digo que o sujeito atual é o usuário passado no parâmetro lá em cima
				.setSubject(user.getUsername())
				// aqui eu passo a data de inspiração
				.setExpiration(getExpiration())
				// aqui eu coloco as roles dentro do token jwt
				.claim("rol", roles)
				// agora mando gerar
				.compact();
	}

		public static String getAuthLogin() {
			UserDetails user = getUserDetails();
			if (user != null) {
				return user.getUsername();
			}
			return null;
		}
	
		public static UserDetails getUserDetails() {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.getPrincipal() != null) {
				return (UserDetails) auth.getPrincipal();
			}
			return null;
		}
}