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

@RunWith(PowerMockRunner.class) // Precisa definir isso para que o Junit assuma as definições do PowerMockito
@PrepareForTest({LocacaoService.class, DataUtils.class}) // Também precisa definir isso para o PowerMock funcionar. Nesse caso apenas esse classe estará no escopo
public class LocacaoServiceComPowerMockTest {
	
	/**
	 * Com as injeções dos mocks, não precisa nem dos sets no LocacaoService.
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
	 * Before é executado antes de cada método de teste
	 */
	@Before
	public void setup() {
		
		/**
		 * Isso, juntamente com as anotações nos services e dao, farão o trabalho de instanciar e setar os mocks no LocacaoService, 
		 * substituindo as linhas comentadas abaixo.
		 * A anotação @Mock define que será um mock, e a anotação @InjectMocks onde serão injetados os mocks
		 * Com as injeções dos mocks, não precisa nem dos sets no LocacaoService.
		 */
		MockitoAnnotations.initMocks(this);
		
		/**
		 * Isso é pra poder mockar os métodos estáticos com o PowerMockito.
		 * Vai garantir também que com isso os outros testes continuaram funcionando, pois o comportamento padrão do spy é executar os métodos
		 */
		service = PowerMockito.spy(service);
	}
	
	/**
	 * O ideal é que cada método de teste, seja utilizado para validar um único ponto, e que tenha apenas uma assertiva, visto que o Junit para ao encontrar uma assertiva que falhou,
	 * e caso exista mais de um teste que vai falhar, apenas o primeiro será mostrado.
	 * Da pra contornar isso utilizando Rule, conforme exemplo abaixo.
	 * @throws Exception 
	 */
	
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	/**
	 * Utilizando esse ErrorCollector, ao invés de utilizar assertThat, se utiliza checkThat, e caso ocorra erro, ele vai ser coletado e continuar o teste
	 * @throws Exception 
	 */
	@Test
	public void deveAlugarFilmeComRule() throws Exception {
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/**
		 * Nesse teste, além de precisar anotar @PrepareForTest com a classe LocacaoService, foi necessário também colocar na anotação
		 * a classe DataUtils, visto que nas assertivas é utilizado o método ehHoje, que internamente utiliza o DataUtils, que por sua vez usa new Date
		 * Foi necessário nesse teste setar o dia fixo, pois essa regra não se aplica a filmes alugados no sábado. 
		 * Para o sábado tem um método específico
		 */
		
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 04, 2017)); // Sexta
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 28);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2017);
		
		/**
		 * Para mostrar como fazer com métodos estáticos, foi modificado no método alugar, para pegar a data a partir do calendar.
		 * E para mockar métodos estáticos, antes precisa dessa linha
		 */
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// Ação
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
		 * Com o assume, posso definir que o meu teste só vai executar em certas condições, como nesse exemplo, só em sabados
		 */
//		assumeThat(new Date(), is(Calendar.SATURDAY));
		
		//Cenario 
		Usuario u = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora());
		
		/**
		 * Com o PowerMockito da pra mocar construtores. Nesse caso, está mocando a instanciação Date().
		 * Então nesse execução, sempre quando for instanciado um Date, sem parâmetros, irá retornar a data definida.
		 * Com isso, da pra simular a execução de um aluguel no sábado.
		 * 
		 * Para isso funcionar, ainda precisa anotar a classe com @RunWith(PowerMockRunner.class) // Precisa definir isso para que o Junit assuma as definições do PowerMockito
		 * Também precisa da anotação @PrepareForTest
		 */
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 04, 2017)); // Sábado
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 29);
//		calendar.set(Calendar.MONTH, Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
		
		/**
		 * Para mostrar como fazer com métodos estáticos, foi modificado no método alugar, para pegar a data a partir do calendar.
		 * E para mockar métodos estáticos, antes precisa dessa linha
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
		 * Apenas para verificar se o método new Date foi mesmo chamado
		 */
//		PowerMockito.verifyNew(Date.class, Mockito.atLeastOnce()).withNoArguments();
		
		/**
		 * PAra verificar a execução de métodos estáticos
		 */
		PowerMockito.verifyStatic(Mockito.atLeast(2));
		// e só coloca a chamada ao método statico
		Calendar.getInstance();
				
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Da pra mockar métodos privados.
		 * Quando no service, ao chamar o método calcularValorLocacao, que é privado, for chamado com a lista de filmes, 
		 * deve retornar 1.0, conforme está especificado, e não o valor que seria esperado que ele retorne. Nesse caso o método nem é chamado.
		 */
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		
		// Verificar que o método abaixo foi chamado n execução acima.
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
		
	}
	
	/**
	 * Testando métodos privados 
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

