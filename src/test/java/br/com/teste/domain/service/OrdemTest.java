package br.com.teste.domain.service;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTest {
	
	public static int contador = 0;
	
	@Test
	public void iniciar() {
		contador = 1;
	}
	
	@Test
	public void verificar() {
		Assert.assertEquals(1, contador);
	}
	
	/**
	 * A melhor opção é deixar os testes independentes
	 */

}
