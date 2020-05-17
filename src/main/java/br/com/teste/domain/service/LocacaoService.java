package br.com.teste.domain.service;

import static br.com.teste.domain.util.DataUtil.adicionarDias;

import java.util.Date;
import java.util.List;

import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
		if (usuario == null)
			throw new LocadoraException("Usuário vazio");
		
		if (filmes == null || filmes.isEmpty())
			throw new LocadoraException("Filme vazio");
		
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0)
				throw new FilmeSemEstoqueException();
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		double valorTotal = 0;
		
		for (Filme filme : filmes)
			valorTotal += filme.getPrecoLocacao();
		
		locacao.setValor(valorTotal);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		// Salvando a locacao...	
		// TODO adicionar método para salvar
		
		return locacao;
	}

}
