package com.badbadcode.application.oauth.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.badbadcode.application.commons.usuarios.models.entity.Usuario;
import com.badbadcode.application.oauth.services.IUsuarioService;

import feign.FeignException;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

	private Logger log = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);
	@Autowired
	private IUsuarioService usuarioService;
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		if(authentication.getName().equalsIgnoreCase("frontendapp")){
		    return; 
		}
		UserDetails user = (UserDetails) authentication.getPrincipal();
		log.info( "Success login: ".concat(user.getUsername()));
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		if(usuario.getIntentos() != null && usuario.getIntentos() > 0) {
			usuario.setIntentos(0);
			usuarioService.update(usuario, usuario.getId());
		}
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		
		String mensaje = "Error en el login: " + exception.getMessage();
		log.error(mensaje);
		
		try {
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			if(usuario.getIntentos() == null) {
				usuario.setIntentos(0);
			}
			
			usuario.setIntentos(usuario.getIntentos()+1);
			log.info("intentos actuales :".concat(usuario.getIntentos().toString()));
			
			if(usuario.getIntentos() >= 3) {
				log.error("El usuario se ha suspendido");
				usuario.setEnabled(false);
			}
			usuarioService.update(usuario, usuario.getId());
			
		}catch(FeignException e) {
			log.error("No existe ese usuario");
		}
	}

}
