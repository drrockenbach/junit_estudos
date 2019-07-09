package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	/**
	 * Isso n�o funciona com spy, por que spy n�o funciona com interfaces.
	 */
//	@Spy
//	private EmailService emailService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockESpy() {
		
		/**
		 * A diferen�a de Spy para Mock, � que quando o spy recebe par�metros diferentes do esperado, ao inv�s de retornar os valores padr�es do objeto,
		 * ele executa o m�todo com os valores passados.
		 * No caso abaixo, mock e spy esperam receber 1 e 2. Na execu��o est� sendo passado 1 e 5. Como o mock recebeu um par�metro diferente do esperado,
		 * ele vai retornar o valor default de int, que � zero.
		 * J� o spy, vai executar o m�todo com o valor passado. Como o spy executa o m�todo, ele n�o pode ser utilizado com interfaces, apenas com classes
		 * � poss�vel fazer o Mock chamar a implementa��o real tamb�m, como pode ser visto no test mockDeveChamarExecucaoReal()
		 */
		
		Mockito.when(calcMock.somar(1,  2)).thenReturn(8);
		
		/**
		 * Quando � definido dessa forma, o m�todo somar � executado neste momento mesmo. Isso ocorre por que o java interpreta calcSpy.somar(1,  2) como uma chamada e o executa.
		 */
		//Mockito.when(calcSpy.somar(1,  2)).thenReturn(8);
		
		/**
		 * Essa � uma forma de definir para que o m�todo n�o seja executado no momento da grava��o da expectativa.
		 * Desta forma, o m�todo s� ir� executar mesmo, caso seja chamado com par�metros, diferentes da expectativa, no caso 1 e 2. Se chamar com 1 e 3 vai executar o m�todo
		 */
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
//		System.out.println(calcMock.somar(1, 5));
		System.out.println(calcSpy.somar(1, 2)); // Assim n�o vai chamar o m�todo, em fun��o da expectativa gravada acima.
		System.out.println(calcSpy.somar(1, 3)); // Assim vai chamar o m�todo, em fun��o da expectativa gravada acima n�o ter sido atendida.
		
		/**
		 * O mock, por default n�o executa os metodos, logo o imprime abaixo n�o ser� executado.
		 * J� o spy vai executar o m�todo imprime
		 */
		
		System.out.println("mock: ");
		calcMock.imprime();
		System.out.println("spy: ");
		calcSpy.imprime();
		
	}
	
	@Test
	public void mockDeveChamarExecucaoReal() {
		
		/**
		 * � poss�vel fazer o Mock chamar a implementa��o real tamb�m, como pode ser visto abaixo
		 */
		
		Mockito.when(calcMock.somar(Mockito.anyInt(),  Mockito.anyInt())).thenCallRealMethod();
		
		assertThat(calcMock.somar(2, 5), is(7));
		
	}
	
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
//		System.out.println(argCapt.getAllValues());
	}

	
	
}
