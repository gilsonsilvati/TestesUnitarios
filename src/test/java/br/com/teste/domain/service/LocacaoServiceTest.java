package br.com.teste.domain.service;

import static br.com.teste.domain.builders.FilmeBuilder.umFilme;
import static br.com.teste.domain.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.teste.domain.builders.LocacaoBuilder.umLocacao;
import static br.com.teste.domain.builders.UsuarioBuilder.umUsuario;
import static br.com.teste.domain.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.teste.domain.matchers.MatchersProprios.ehHoje;
import static br.com.teste.domain.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.com.teste.domain.util.DataUtil.ehMesmaData;
import static br.com.teste.domain.util.DataUtil.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import br.com.teste.domain.dao.LocacaoDAO;
import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;

public class LocacaoServiceTest {
	
	@InjectMocks @Spy
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO locacaoDAO;
	
	@Mock
	private SPCService spcService;
	
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		initMocks(this);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(obterData(26, 6, 2020)).when(locacaoService).obterData();
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(ehMesmaData(locacao.getDataLocacao(), obterData(26, 6, 2020)), is(true));
		error.checkThat(ehMesmaData(locacao.getDataRetorno(), obterData(27, 6, 2020)), is(true));
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
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(obterData(27, 6, 2020)).when(locacaoService).obterData();
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaUsuarioNegativado() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
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
		var usuario2 = umUsuario().comNome("Usuário em dia").agora();
		var usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasada().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora()
		);
		
		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);
		
		// acao
		locacaoService.notificarAtrasos();
		
		// verificacao
		verify(emailService, times(3)).notificarAtrasos(Mockito.any(Usuario.class));
		verify(emailService).notificarAtrasos(usuario);
		verify(emailService, atLeastOnce()).notificarAtrasos(usuario3);
		verify(emailService, never()).notificarAtrasos(usuario2);
		verifyNoMoreInteractions(emailService);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		// verificacao
		// Nova
		exception.expect(LocadoraException.class);
		exception.expectMessage("Serviço SPC fora do ar, tente novamente mais tarde");
		
		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarLocacao() {
		// cenario
		var locacao = umLocacao().agora();
		
		// acao
		locacaoService.prorrogarLocacao(locacao, 3);
		
		// verificacao
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		verify(locacaoDAO).salvar(argumentCaptor.capture());
		var locacaoRetornada = argumentCaptor.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(equalTo(12.0)));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}
	
	// Executando métodos privados
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method method = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		method.setAccessible(true);
		
		double valor = (double) method.invoke(locacaoService, filmes);
		
		// verificacao
		assertThat(valor, is(4.0));
	}
	
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
	
}
