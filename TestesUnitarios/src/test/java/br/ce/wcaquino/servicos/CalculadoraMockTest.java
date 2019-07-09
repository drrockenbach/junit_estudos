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
		
		// Aqui é possível capturar os valores passados para a execução do método
		ArgumentCaptor<Integer> argCapt= ArgumentCaptor.forClass(Integer.class); 
		
		// Os métodos eq e anyInt(), servem para simular valores. Eles são matchers.
		// Se for utilizado um matcher para passar um parâmetro, todos os parâmetros esperados também devem ser um matcher
		// Por isso que tem o Mockito.eq(1), que no final da contas vai passar 1.
		// Esse teste é apenas um exemplo, que se o somar recever o valor 1 e qualquer outro valor, representado ali pelo matcher anyInt(), deve retornar 5

		//		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		
		// Aqui o argCapt.capture() é como se estivesse passando um matcher esperando qualquer integer
		// e sempre retornará 5, pois está fixo o retorno 
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5,  calc.somar(1,  1000));
		
		// Valores capturados na execução do método. No caso 1 e 1000
		System.out.println(argCapt.getAllValues());
	}

}
