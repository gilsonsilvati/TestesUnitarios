package br.com.teste.domain.service;

import static br.com.teste.domain.builders.FilmeBuilder.umFilme;
import static br.com.teste.domain.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.teste.domain.builders.LocacaoBuilder.umLocacao;
import static br.com.teste.domain.builders.UsuarioBuilder.umUsuario;
import static br.com.teste.domain.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.teste.domain.matchers.MatchersProprios.ehHoje;
import static br.com.teste.domain.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.com.teste.domain.util.DataUtil.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.com.teste.domain.dao.LocacaoDAO;
import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.util.DataUtil;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;
	
	private LocacaoDAO locacaoDAO;
	private SPCService spcService;
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		locacaoService = new LocacaoService();
		
		locacaoDAO = Mockito.mock(LocacaoDAO.class);
		spcService = Mockito.mock(SPCService.class);
		emailService = Mockito.mock(EmailService.class);
		
		locacaoService.setLocacaoDAO(locacaoDAO);
		locacaoService.setSPCService(spcService);
		locacaoService.setEmailService(emailService);
	}
	
	@Test
//	@Ignore
	public void deveAlugarFilme() throws Exception {
		assumeFalse(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}
	
	// Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	// Robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// acao
		try {
			locacaoService.alugarFilme(null, filmes);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is(equalTo("Usuário vazio")));
		}
	}
	
	// Nova
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		var usuario = umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage(is(equalTo("Filme vazio")));
		
		// acao
		locacaoService.alugarFilme(usuario, null);
	}
	
	@Test
//	@Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		assumeTrue(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
//		boolean ehSegunda = DataUtil.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		MatcherAssert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		MatcherAssert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaUsuarioNegativado() throws FilmeSemEstoqueException {
		// cenario
		var usuario = umUsuario().agora();
//		var usuario2 = umUsuario().comNome("Usuário 2").agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		// Nova
//		exception.expect(LocadoraException.class);
//		exception.expectMessage("Usuário Negativado");
		
		// acao
		// Robusta
		try {
			locacaoService.alugarFilme(usuario, filmes);
		// verificacao
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is(equalTo("Usuário Negativado")));
		}
		
		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		var usuario = umUsuario().agora();
//		var usuario2 = umUsuario().comNome("Usuário 2").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao()
					.comUsuario(usuario)
					.comDataRetorno(obterDataComDiferencaDias(-2))
					.agora());
		
		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);
		
		// acao
		locacaoService.notificarAtrasos();
		
		// verificacao
		verify(emailService).notificarAtrasos(usuario);
	}
	
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
	
}
