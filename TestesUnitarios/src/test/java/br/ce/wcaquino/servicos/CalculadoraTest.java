package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exception.DivisaoPorZeroException;

public class CalculadoraTest {

	private Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
	}
	
	@Test
	public void somarDoisValores() {
		
		// cenario
		int a = 5;
		int b = 3;
		Calculadora calc = new Calculadora();
		
		// acao
		int resultado = calc.somar(a, b);

		// verificacao
		assertEquals(8, resultado);
		
	}

	@Test
	public void subtrairDoisValores() {
		
		// cenario
		int a = 5;
		int b = 3;
		Calculadora calc = new Calculadora();
		
		// acao
		int resultado = calc.subtrair(a, b);

		// verificacao
		assertEquals(2, resultado);
	}
	
	@Test
	public void dividirDoisValores() throws DivisaoPorZeroException {

		// cenario
		int a = 6;
		int b = 3;
		Calculadora calc = new Calculadora();
		
		// acao
		double resultado = calc.dividir(a, b);

		// verificacao
		assertEquals(2, resultado, 0);
		
	}
	
	@Test(expected=DivisaoPorZeroException.class)
	public void deveLancarExceptionDivisaoPorZero() throws DivisaoPorZeroException {
		// cenario
		int a = 6;
		int b = 0;
		Calculadora calc = new Calculadora();
		
		// acao
		calc.dividir(a, b);

	}
}
