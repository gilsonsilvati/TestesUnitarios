package br.com.teste.domain.service;

import static br.com.teste.domain.util.DataUtil.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.teste.domain.dao.LocacaoDAO;
import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;
import br.com.teste.domain.util.DataUtil;

public class LocacaoService {
	
	private LocacaoDAO locacaoDAO;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
		if (usuario == null) {
			throw new LocadoraException("Usuário vazio");
		}
		
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		
		boolean negativado;
		
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Serviço SPC fora do ar, tente novamente mais tarde");
		}
		
		if (negativado) {
			throw new LocadoraException("Usuário Negativado");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		double valorTotal = 0;
		
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			double valorFilme = filme.getPrecoLocacao();
			
			// Refatorar
			/* if (i == 2)
				valorFilme = valorFilme * 0.75;
			
			if (i == 3)
				valorFilme = valorFilme * 0.5;
			
			if (i == 4)
				valorFilme = valorFilme * 0.25;
			
			if (i == 5)
				valorFilme = 0.0; */
			
			// Refatoração
			switch (i) {
				case 2: valorFilme *= 0.75; break;
				case 3: valorFilme *= 0.5; break;
				case 4: valorFilme *= 0.25; break;
				case 5: valorFilme = 0.0; break;
			}
			
			valorTotal += valorFilme;
		}
		
		locacao.setValor(valorTotal);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		if (DataUtil.verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
			dataEntrega = adicionarDias(dataEntrega, 1);
		
		locacao.setDataRetorno(dataEntrega);
		
		// Salvando a locacao...	
		locacaoDAO.salvar(locacao);
		
		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = locacaoDAO.obterLocacoesPendentes();
		
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtrasos(locacao.getUsuario());
			}
		}
	}
	
	/* Injeções de Dependências */
//	public void setLocacaoDAO(LocacaoDAO locacaoDAO) {
//		this.locacaoDAO = locacaoDAO;
//	}
//	
//	public void setSPCService(SPCService spcService) {
//		this.spcService = spcService;
//	}
//	
//	public void setEmailService(EmailService emailService) {
//		this.emailService = emailService;
//	}

}
