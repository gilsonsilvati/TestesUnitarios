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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.com.teste.domain.dao.LocacaoDAO;
import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Locacao;
import br.com.teste.domain.model.Usuario;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
@PowerMockIgnore("jdk.internal.reflect.*") // Estava dando erro de: initializationError -> 
// Caused by: java.lang.IllegalAccessError: class jdk.internal.reflect.ConstructorAccessorImpl loaded by org.powermock.core.classloader.MockClassLoader @453da22c cannot access jdk/internal/reflect superclass jdk.internal.reflect.MagicAccessorImpl
public class LocacaoServiceTest {
	
	@InjectMocks
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
		
		locacaoService = PowerMockito.spy(locacaoService);
	}
	
	@Test
//	@Ignore
	public void deveAlugarFilme() throws Exception {
//		assumeFalse(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(26, 6, 2020));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 26);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2020);
		
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
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
//	@Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
//		assumeTrue(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(27, 6, 2020));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2020);
		
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
//		boolean ehSegunda = DataUtil.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		MatcherAssert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		MatcherAssert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
		PowerMockito.verifyStatic(Calendar.class, Mockito.atLeastOnce());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaUsuarioNegativado() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
//		var usuario2 = umUsuario().comNome("Usuário 2").agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
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
	
	@Test
	public void deveAlugarFilme_SemCacularValor() throws Exception {
		// cenario
		var usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(locacaoService, "calcularValorLocacao", filmes);
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(locacaoService).invoke("calcularValorLocacao", filmes);
	}
	
	// Executando métodos privados
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		double valor = (double) Whitebox.invokeMethod(locacaoService, "calcularValorLocacao", filmes);
		
		// verificacao
		assertThat(valor, is(4.0));
	}
	
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
	
}
