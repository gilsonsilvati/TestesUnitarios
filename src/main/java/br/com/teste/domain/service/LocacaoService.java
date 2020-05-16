package br.com.teste.domain.service;

import static br.com.teste.domain.util.DataUtil.adicionarDias;

import java.util.Date;

import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;
import br.com.teste.domain.util.DataUtil;

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

	public static void main(String[] args) {
		
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);
		
		// verificacao
		System.out.println(locacao.getValor() == 5.0);
		System.out.println(DataUtil.isMesmaData(locacao.getDataLocacao(), new Date()));
		System.out.println(DataUtil.isMesmaData(locacao.getDataRetorno(), DataUtil.obterDataComDiferencaDias(1)));
		
	}
	
}
