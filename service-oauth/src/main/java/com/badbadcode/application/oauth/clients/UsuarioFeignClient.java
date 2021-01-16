package com.badbadcode.application.oauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.badbadcode.application.commons.usuarios.models.entity.Usuario;

@FeignClient(name = "servicio-usuarios")
public interface UsuarioFeignClient {

	@GetMapping("/usuarios/search/find")
	public Usuario findByUsername(@RequestParam(name="username") String username);
	
	@PutMapping("/usuarios/{id}")
	public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id);
}
