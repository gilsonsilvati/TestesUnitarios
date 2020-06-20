package br.com.teste.domain.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		Date dataEsperada = DataUtil.obterDataComDiferencaDias(quantidadeDias);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtil.ehMesmaData(data, DataUtil.obterDataComDiferencaDias(quantidadeDias));
	}

}
