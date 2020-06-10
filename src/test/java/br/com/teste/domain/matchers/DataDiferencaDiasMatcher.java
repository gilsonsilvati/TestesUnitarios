package br.com.teste.domain.matchers;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.com.teste.domain.util.DataUtil;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {
	
	private Integer quantidadeDias;

	public DataDiferencaDiasMatcher(Integer quantidadeDias) {
		this.quantidadeDias = quantidadeDias;
	}

	@Override
	public void describeTo(Description description) {

	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtil.ehMesmaData(data, DataUtil.obterDataComDiferencaDias(quantidadeDias));
	}

}
