package br.com.teste.domain.dao;

import java.util.List;

import br.com.teste.domain.model.Locacao;

public class LocacaoDAOFake implements LocacaoDAO {

	@Override
	public void salvar(Locacao locacao) {

	}

	@Override
	public List<Locacao> obterLocacoesPendentes() {
		return null;
	}

}
