package br.com.teste.domain.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.teste.domain.service.CalculadoraServiceTest;
import br.com.teste.domain.service.CalculoValorLocacaoTest;
import br.com.teste.domain.service.LocacaoServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraServiceTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao { } // Não muito recomendado: pode acabar esquecendo alguma classe de teste.
