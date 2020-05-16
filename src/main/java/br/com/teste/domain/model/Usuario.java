package br.com.teste.domain.model;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Usuario {

	private String nome;
	
	public Usuario() {}
	
	public Usuario(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		
		Usuario other = (Usuario) obj;
		return new EqualsBuilder().append(nome, other.nome).isEquals();
	}
	
}
