package br.ce.wcaquino.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

// O tipo do generics do TypeSafeMatcher deve ser o tipo do primeiro parâmetro que se espera passar no assertThat
public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

	private Integer diaSemana;
	
	// Será o segundo parâmetro do assertThat
	public DiaSemanaMatcher(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	@Override
	public void describeTo(Description description) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, diaSemana);

		String dataExtenso = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		
		description.appendText(dataExtenso);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.verificarDiaSemana(data, diaSemana);
	}

}
