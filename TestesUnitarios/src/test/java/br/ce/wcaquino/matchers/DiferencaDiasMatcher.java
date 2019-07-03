package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer diferencaDias;

	public DiferencaDiasMatcher(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}
	
	@Override
	public void describeTo(Description description) {
		Date experada = DataUtils.obterDataComDiferencaDias(diferencaDias);
		
		description.appendValue(experada);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, obterDataComDiferencaDias(diferencaDias));
	}

}
