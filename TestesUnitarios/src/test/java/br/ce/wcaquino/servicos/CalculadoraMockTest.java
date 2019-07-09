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
	 * Isso não funciona com spy, por que spy não funciona com interfaces.
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
		 * A diferença de Spy para Mock, é que quando o spy recebe parâmetros diferentes do esperado, ao invés de retornar os valores padrões do objeto,
		 * ele executa o método com os valores passados.
		 * No caso abaixo, mock e spy esperam receber 1 e 2. Na execução está sendo passado 1 e 5. Como o mock recebeu um parâmetro diferente do esperado,
		 * ele vai retornar o valor default de int, que é zero.
		 * Já o spy, vai executar o método com o valor passado. Como o spy executa o método, ele não pode ser utilizado com interfaces, apenas com classes
		 * É possível fazer o Mock chamar a implementação real também, como pode ser visto no test mockDeveChamarExecucaoReal()
		 */
		
		Mockito.when(calcMock.somar(1,  2)).thenReturn(8);
		
		/**
		 * Quando é definido dessa forma, o método somar é executado neste momento mesmo. Isso ocorre por que o java interpreta calcSpy.somar(1,  2) como uma chamada e o executa.
		 */
		//Mockito.when(calcSpy.somar(1,  2)).thenReturn(8);
		
		/**
		 * Essa é uma forma de definir para que o método não seja executado no momento da gravação da expectativa.
		 * Desta forma, o método só irá executar mesmo, caso seja chamado com parâmetros, diferentes da expectativa, no caso 1 e 2. Se chamar com 1 e 3 vai executar o método
		 */
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
//		System.out.println(calcMock.somar(1, 5));
		System.out.println(calcSpy.somar(1, 2)); // Assim não vai chamar o método, em função da expectativa gravada acima.
		System.out.println(calcSpy.somar(1, 3)); // Assim vai chamar o método, em função da expectativa gravada acima não ter sido atendida.
		
		/**
		 * O mock, por default não executa os metodos, logo o imprime abaixo não será executado.
		 * Já o spy vai executar o método imprime
		 */
		
		System.out.println("mock: ");
		calcMock.imprime();
		System.out.println("spy: ");
		calcSpy.imprime();
		
	}
	
	@Test
	public void mockDeveChamarExecucaoReal() {
		
		/**
		 * É possível fazer o Mock chamar a implementação real também, como pode ser visto abaixo
		 */
		
		Mockito.when(calcMock.somar(Mockito.anyInt(),  Mockito.anyInt())).thenCallRealMethod();
		
		assertThat(calcMock.somar(2, 5), is(7));
		
	}
	
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
//		System.out.println(argCapt.getAllValues());
	}

	
	
}
