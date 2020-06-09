package br.com.teste.domain.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.com.teste.domain.exceptions.FilmeSemEstoqueException;
import br.com.teste.domain.exceptions.LocadoraException;
import br.com.teste.domain.model.Filme;
import br.com.teste.domain.model.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value = 1)
	public Double valorLocacao;
	
	@Parameter(value = 2)
	public String cenario;
	
	private LocacaoService locacaoService;
	
	@Before
	public void setup() {
		locacaoService = new LocacaoService();
	}
	
	private static Filme filme1 = new Filme("Filme 1", 2, 4.0);
	private static Filme filme2 = new Filme("Filme 2", 2, 4.0);
	private static Filme filme3 = new Filme("Filme 3", 2, 4.0);
	private static Filme filme4 = new Filme("Filme 4", 2, 4.0);
	private static Filme filme5 = new Filme("Filme 5", 2, 4.0);
	private static Filme filme6 = new Filme("Filme 6", 2, 4.0);
	private static Filme filme7 = new Filme("Filme 7", 2, 4.0);
	
	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{ Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem Desconto" },
			{ Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem Desconto" }
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		var usuario = new Usuario("Usuario 1");
		
		// acao
		var locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificacao
		MatcherAssert.assertThat(locacao.getValor(), is(equalTo(valorLocacao)));
	}

}
