package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ce.wcaquino.exception.DivisaoPorZeroException;
import br.ce.wcaquino.runners.ParallelRunner;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {

	private Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
		System.out.println("Iniciando....");
	}
	
	@After
	public void after() {
		System.out.println("Finalizando....");
	}
	
	@Test
	public void somarDoisValores() {
		
		// cenario
		int a = 5;
		int b = 3;
		Calculadora calc = new Calculadora();
		
		// acao
		int resultado = calc.somar(a, b);

		// Pra testar paralelismo
		esperar(500);
		
		// verificacao
		assertEquals(8, resultado);
		
		
	}

	private void esperar(long esperar)  {
		try {
			Thread.sleep(esperar);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void subtrairDoisValores() {
		
		// cenario
		int a = 5;
		int b = 3;
		Calculadora calc = new Calculadora();
		
		// acao
		int resultado = calc.subtrair(a, b);

		esperar(300);
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
		esperar(100);
	}
	
	@Test(expected=DivisaoPorZeroException.class)
	public void deveLancarExceptionDivisaoPorZero() throws DivisaoPorZeroException {
		// cenario
		int a = 6;
		int b = 0;
		Calculadora calc = new Calculadora();
		
		esperar(800);
		// acao
		calc.dividir(a, b);
	}
}
