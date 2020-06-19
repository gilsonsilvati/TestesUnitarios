package br.com.teste.domain.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {
	
	@Test
	public void teste() {
		// cenario
		var calculadora = mock(CalculadoraService.class);
		
		// acao
		when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		// verificacao
		System.out.println(calculadora.somar(1, 3));
	}

}