package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class) // Precisa definir isso para que o Junit assuma as defini��es do PowerMockito
@PrepareForTest({LocacaoService.class, DataUtils.class}) // Tamb�m precisa definir isso para o PowerMock funcionar. Nesse caso apenas esse classe estar� no escopo
public class LocacaoServiceComPowerMockTest {
	
	/**
	 * Com as inje��es dos mocks, n�o precisa nem dos sets no LocacaoService.
	 */
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spcService;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService emailService; 
	
	/**
	 * Before � executado antes de cada m�todo de teste
	 */
	@Before
	public void setup() {
		
		/**
		 * Isso, juntamente com as anota��es nos services e dao, far�o o trabalho de instanciar e setar os mocks no LocacaoService, 
		 * substituindo as linhas comentadas abaixo.
		 * A anota��o @Mock define que ser� um mock, e a anota��o @InjectMocks onde ser�o injetados os mocks
		 * Com as inje��es dos mocks, n�o precisa nem dos sets no LocacaoService.
		 */
		MockitoAnnotations.initMocks(this);
		
		/**
		 * Isso � pra poder mockar os m�todos est�ticos com o PowerMockito.
		 * Vai garantir tamb�m que com isso os outros testes continuaram funcionando, pois o comportamento padr�o do spy � executar os m�todos
		 */
		service = PowerMockito.spy(service);
	}
	
	/**
	 * O ideal � que cada m�todo de teste, seja utilizado para validar um �nico ponto, e que tenha apenas uma assertiva, visto que o Junit para ao encontrar uma assertiva que falhou,
	 * e caso exista mais de um teste que vai falhar, apenas o primeiro ser� mostrado.
	 * Da pra contornar isso utilizando Rule, conforme exemplo abaixo.
	 * @throws Exception 
	 */
	
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	/**
	 * Utilizando esse ErrorCollector, ao inv�s de utilizar assertThat, se utiliza checkThat, e caso ocorra erro, ele vai ser coletado e continuar o teste
	 * @throws Exception 
	 */
	@Test
	public void deveAlugarFilmeComRule() throws Exception {
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/**
		 * Nesse teste, al�m de precisar anotar @PrepareForTest com a classe LocacaoService, foi necess�rio tamb�m colocar na anota��o
		 * a classe DataUtils, visto que nas assertivas � utilizado o m�todo ehHoje, que internamente utiliza o DataUtils, que por sua vez usa new Date
		 * Foi necess�rio nesse teste setar o dia fixo, pois essa regra n�o se aplica a filmes alugados no s�bado. 
		 * Para o s�bado tem um m�todo espec�fico
		 */
		
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 04, 2017)); // Sexta
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 28);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2017);
		
		/**
		 * Para mostrar como fazer com m�todos est�ticos, foi modificado no m�todo alugar, para pegar a data a partir do calendar.
		 * E para mockar m�todos est�ticos, antes precisa dessa linha
		 */
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// A��o
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		error.checkThat(locacao.getValor(), CoreMatchers.is(5.0));
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getValor(), is(not(6.0)));
//		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		
		
		
	}

	@Rule
	public ExpectedException exException = ExpectedException.none();
	
	@Test
//	@Ignore // Se quiser ignorar algum teste
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		
		/**
		 * Com o assume, posso definir que o meu teste s� vai executar em certas condi��es, como nesse exemplo, s� em sabados
		 */
//		assumeThat(new Date(), is(Calendar.SATURDAY));
		
		//Cenario 
		Usuario u = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora());
		
		/**
		 * Com o PowerMockito da pra mocar construtores. Nesse caso, est� mocando a instancia��o Date().
		 * Ent�o nesse execu��o, sempre quando for instanciado um Date, sem par�metros, ir� retornar a data definida.
		 * Com isso, da pra simular a execu��o de um aluguel no s�bado.
		 * 
		 * Para isso funcionar, ainda precisa anotar a classe com @RunWith(PowerMockRunner.class) // Precisa definir isso para que o Junit assuma as defini��es do PowerMockito
		 * Tamb�m precisa da anota��o @PrepareForTest
		 */
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 04, 2017)); // S�bado
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 29);
//		calendar.set(Calendar.MONTH, Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
		
		/**
		 * Para mostrar como fazer com m�todos est�ticos, foi modificado no m�todo alugar, para pegar a data a partir do calendar.
		 * E para mockar m�todos est�ticos, antes precisa dessa linha
		 */
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// acao
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		assertThat(ehSegunda, is(true));
		
		assertThat(locacao.getDataRetorno(), caiNaSegunda());
//		assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		
		/**
		 * Apenas para verificar se o m�todo new Date foi mesmo chamado
		 */
//		PowerMockito.verifyNew(Date.class, Mockito.atLeastOnce()).withNoArguments();
		
		/**
		 * PAra verificar a execu��o de m�todos est�ticos
		 */
		PowerMockito.verifyStatic(Mockito.atLeast(2));
		// e s� coloca a chamada ao m�todo statico
		Calendar.getInstance();
				
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Da pra mockar m�todos privados.
		 * Quando no service, ao chamar o m�todo calcularValorLocacao, que � privado, for chamado com a lista de filmes, 
		 * deve retornar 1.0, conforme est� especificado, e n�o o valor que seria esperado que ele retorne. Nesse caso o m�todo nem � chamado.
		 */
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		
		// Verificar que o m�todo abaixo foi chamado n execu��o acima.
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
		
	}
	
	/**
	 * Testando m�todos privados 
	 * @throws Exception
	 */
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		// Precisa do cast, por que invokeMethod retorna object
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		// verificacao
		assertThat(valor, is(5.0));
		
	}
}

