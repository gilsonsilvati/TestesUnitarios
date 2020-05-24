package br.com.teste.domain.service;

import br.com.teste.domain.exceptions.NaoPodeDividirPorZeroException;
import br.com.teste.domain.exceptions.NaoPodeSubtrairNumeroMenorPorMaiorException;

public class CalculadoraService {

	public int somar(int a, int b) {
		return a + b;
	}

	public int subtrair(int a, int b) throws NaoPodeSubtrairNumeroMenorPorMaiorException {
		if (a < b)
			throw new NaoPodeSubtrairNumeroMenorPorMaiorException();
		
		return a - b;
	}

	public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {
		if (a == 0 || b == 0)
			throw new NaoPodeDividirPorZeroException();
		
		return a / b;
	}

}
