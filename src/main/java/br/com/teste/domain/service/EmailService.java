package br.com.teste.domain.service;

import br.com.teste.domain.model.Usuario;

public interface EmailService {
	
	void notificarAtrasos(Usuario usuario);

}
