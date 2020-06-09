package br.com.teste.domain.service;

import static br.com.teste.domain.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.teste.domain.util.DataUtil.isMesmaData;
import static br.com.teste.domain.util.DataUtil.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Usuario;
import br.com.teste.domain.util.DataUtil;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		locacaoService = new LocacaoService();
	}
	
	@Test
//	@Ignore
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = new Usuario("Usuario 1");
		var<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	// Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
		var usuario = new Usuario("Usuario 1");
		var<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));

		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	// Robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		var<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));

		// acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			MatcherAssert.assertThat(e.getMessage(), is(equalTo("Usuário vazio")));
		}
	}
	
	// Nova
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		var usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage(is(equalTo("Filme vazio")));
		
		// acao
		locacaoService.alugarFilme(usuario, null);
	}
	
	@Test
//	@Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtil.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		var usuario = new Usuario("Usuario 1");
		var<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
//		boolean ehSegunda = DataUtil.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		MatcherAssert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		MatcherAssert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		MatcherAssert.assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
}
