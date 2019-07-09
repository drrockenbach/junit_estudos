package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculadoraMockTest {

	@Test
	public void teste() {
		
		// Cenario
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		// Aqui � poss�vel capturar os valores passados para a execu��o do m�todo
		ArgumentCaptor<Integer> argCapt= ArgumentCaptor.forClass(Integer.class); 
		
		// Os m�todos eq e anyInt(), servem para simular valores. Eles s�o matchers.
		// Se for utilizado um matcher para passar um par�metro, todos os par�metros esperados tamb�m devem ser um matcher
		// Por isso que tem o Mockito.eq(1), que no final da contas vai passar 1.
		// Esse teste � apenas um exemplo, que se o somar recever o valor 1 e qualquer outro valor, representado ali pelo matcher anyInt(), deve retornar 5

		//		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		
		// Aqui o argCapt.capture() � como se estivesse passando um matcher esperando qualquer integer
		// e sempre retornar� 5, pois est� fixo o retorno 
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5,  calc.somar(1,  1000));
		
		// Valores capturados na execu��o do m�todo. No caso 1 e 1000
		System.out.println(argCapt.getAllValues());
	}

}
