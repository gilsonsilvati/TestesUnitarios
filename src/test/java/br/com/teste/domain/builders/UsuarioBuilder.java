package br.com.teste.domain.builders;

import br.com.teste.domain.model.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;

	private UsuarioBuilder() { }
	
	public static UsuarioBuilder umUsuario() {
		var builder = new UsuarioBuilder();
		builder.usuario = new Usuario("Usuário 1");
		
		return builder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}

}
