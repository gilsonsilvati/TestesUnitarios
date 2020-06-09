package br.com.teste.domain.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.com.teste.domain.util.DataUtil;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {
	
	private Integer diaSemana;
	
	public DiaSemanaMatcher(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}

	@Override
	public void describeTo(Description description) {
		Calendar data = Calendar.getInstance();
		data.set(Calendar.DAY_OF_WEEK, diaSemana);
		
		String dataPorExtenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(dataPorExtenso);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtil.verificarDiaSemana(data, diaSemana);
	}

}
