package br.com.teste.domain.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.teste.domain.exceptions.NaoPodeDividirPorZeroException;
import br.com.teste.domain.exceptions.NaoPodeSubtrairNumeroMenorPorMaiorException;

public class CalculadoraServiceTest {
	
	private int a;
	private int b;
	private int resultado;
	
	private CalculadoraService calculadoraService;
	
	@Before
	public void setup() {
		a = 0;
		b = 0;
		resultado = 0;
		
		calculadoraService = new CalculadoraService();
	}
	
	@Test
	public void deveSomaDoisValores() {
		// cenario
		a = 5;
		b = 3;
		
		// acao
		resultado = calculadoraService.somar(a, b);
		
		// verificacao
		assertEquals(8, resultado);
	}

	@Test(expected = NaoPodeSubtrairNumeroMenorPorMaiorException.class)
	public void deveLancarExceptionNaoPodeSubtrairNumeroMenorPorMaior() throws NaoPodeSubtrairNumeroMenorPorMaiorException {
		// cenario
		a = 5;
		b = 8;
		
		// acao
		calculadoraService.subtrair(a, b);
	}
	
	@Test
	public void deveSubtrairDoisValores() throws NaoPodeSubtrairNumeroMenorPorMaiorException {
		// cenario
		a = 8;
		b = 5;
		
		// acao
		resultado = calculadoraService.subtrair(a, b);
		
		// verificacao
		assertEquals(3, resultado);
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		// cenario
		a = 6;
		b = 3;
		
		// acao
		resultado = calculadoraService.dividir(a, b);
		
		// verificacao
		assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExceptionAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		// cenario
		a = 0;
		b = 6;
		
		// acao
		resultado = calculadoraService.dividir(a, b);
	}
	
}
