package br.com.teste.domain.builders;

import br.com.teste.domain.model.Filme;

public class FilmeBuilder {
	
	private Filme filme;
	
	private FilmeBuilder() { }
	
	public static FilmeBuilder umFilme() {
		var builder = new FilmeBuilder();
		builder.filme = new Filme("Filme XPTO", 2, 4.0);
		
		return builder;
	}
	
	public static FilmeBuilder umFilmeSemEstoque() {
		var builder = new FilmeBuilder();
		builder.filme = new Filme("Filme XPTO", 0, 4.0);
		
		return builder;
	}
	
	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		
		return this;
	}
	
	public FilmeBuilder comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		
		return this;
	}
	
	public Filme agora() {
		return filme;
	}
	
}
