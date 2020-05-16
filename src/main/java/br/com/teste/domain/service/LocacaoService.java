package br.com.teste.domain.service;

import static br.com.teste.domain.util.DataUtil.adicionarDias;
import static br.com.teste.domain.util.DataUtil.isMesmaData;
import static br.com.teste.domain.util.DataUtil.obterDataComDiferencaDias;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) {
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		// Salvando a locacao...	
		// TODO adicionar método para salvar
		
		return locacao;
	}

	@Test
	public void teste() {
		
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);
		
		// verificacao
		assertTrue(locacao.getValor() == 5.0);
		assertTrue(isMesmaData(locacao.getDataLocacao(), new Date()));
		assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));
	}
	
}
