package br.com.teste.domain.dao;

import java.util.List;

import br.com.teste.domain.model.Locacao;

public interface LocacaoDAO {
	
	void salvar(Locacao locacao);

	List<Locacao> obterLocacoesPendentes();

}
