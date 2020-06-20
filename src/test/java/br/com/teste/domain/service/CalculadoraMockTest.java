package br.com.teste.domain.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CalculadoraMockTest {
	
	@Test
	public void teste() {
		// cenario
		var calculadora = mock(CalculadoraService.class);
		
		// acao
		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
//		when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(5);
		
		// verificacao
		assertEquals(5, calculadora.somar(1, 100000));
		System.out.println(argumentCaptor.getAllValues());
	}

}
