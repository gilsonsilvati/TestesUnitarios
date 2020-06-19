package br.com.teste.domain.builders;

import static br.com.teste.domain.builders.FilmeBuilder.umFilme;
import static br.com.teste.domain.builders.UsuarioBuilder.umUsuario;
import static br.com.teste.domain.util.DataUtil.obterDataComDiferencaDias;

import java.util.Arrays;
import java.util.Date;

import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;
import br.com.teste.domain.util.DataUtil;

public class LocacaoBuilder {
	
	private Locacao locacao;
	
	private LocacaoBuilder() { }

	public static LocacaoBuilder umLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		
		inicializarDadosPadroes(builder);
		
		return builder;
	}

	public static void inicializarDadosPadroes(LocacaoBuilder builder) {
		builder.locacao = new Locacao();
		Locacao locacao = builder.locacao;

		
		locacao.setUsuario(umUsuario().agora());
		locacao.setFilmes(Arrays.asList(umFilme().agora()));
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(DataUtil.obterDataComDiferencaDias(1));
		locacao.setValor(4.0);
	}

	public LocacaoBuilder comUsuario(Usuario param) {
		locacao.setUsuario(param);
		return this;
	}

	public LocacaoBuilder comListaFilmes(Filme... params) {
		locacao.setFilmes(Arrays.asList(params));
		return this;
	}

	public LocacaoBuilder comDataLocacao(Date param) {
		locacao.setDataLocacao(param);
		return this;
	}

	public LocacaoBuilder comDataRetorno(Date param) {
		locacao.setDataRetorno(param);
		return this;
	}
	
	public LocacaoBuilder atrasada() {
		locacao.setDataLocacao(obterDataComDiferencaDias(-4));
		locacao.setDataRetorno(obterDataComDiferencaDias(-2));
		
		return this;
	}

	public LocacaoBuilder comValor(Double param) {
		locacao.setValor(param);
		return this;
	}

	public Locacao agora() {
		return locacao;
	}
	
}
