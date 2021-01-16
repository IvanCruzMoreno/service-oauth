package com.badbadcode.application.oauth.services;

import org.springframework.stereotype.Service;
import com.badbadcode.application.commons.usuarios.models.entity.Usuario;

@Service
public interface IUsuarioService {

	public Usuario findByUsername(String username);
	public Usuario update(Usuario usuario, Long id);
}
