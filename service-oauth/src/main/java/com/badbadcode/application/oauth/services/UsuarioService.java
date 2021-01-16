package com.badbadcode.application.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.badbadcode.application.commons.usuarios.models.entity.Usuario;
import com.badbadcode.application.oauth.clients.UsuarioFeignClient;

@Service
public class UsuarioService implements UserDetailsService, IUsuarioService {

	private Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	private UsuarioFeignClient clientFeign;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = clientFeign.findByUsername(username);
		if(usuario == null) {
			log.error("No existe el usuario indicado");
			throw new UsernameNotFoundException("No existe el usuario indicado");
		}
		List<GrantedAuthority> authorities = usuario.getRoles()
													.stream()
													.map( rol -> new SimpleGrantedAuthority(rol.getNombre()))
													.peek(roleAuthority -> log.info("Role" + roleAuthority.getAuthority()))
													.collect(Collectors.toList());
		log.info("Usuario autenticado"+ usuario.getUsername());
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), 
						true, true, true, authorities);
	}

	@Override
	public Usuario findByUsername(String username) {
		return clientFeign.findByUsername(username);
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return clientFeign.update(usuario, id);
	}

}
