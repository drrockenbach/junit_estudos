package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class) // Precisa definir isso para que o Junit assuma as defini��es do PowerMockito
@PrepareForTest({LocacaoService.class, DataUtils.class}) // Tamb�m precisa definir isso para o PowerMock funcionar. Nesse caso apenas esse classe estar� no escopo
public class LocacaoServiceTest {
	
	/**
	 * Com as inje��es dos mocks, n�o precisa nem dos sets no LocacaoService.
	 */
	
	@InjectMocks
	static LocacaoService service;
	
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
		
//		service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		spcService = Mockito.mock(SPCService.class);
//		service.setSPCService(spcService);
//		emailService = Mockito.mock(EmailService.class);
//		service.setEmailService(emailService);
	}
	
	/**
	 * After � executado depois de cada m�todo de teste
	 */
	@After
	public void after() {
//		System.out.println("After");
	}
	
	/**
	 * Before � executado antes de cada m�todo de teste
	 */
	@BeforeClass
	public static void setupClass() {
//		System.out.println("Before class");
	}
	
	/**
	 * After � executado depois de cada m�todo de teste
	 */
	@AfterClass
	public static void afterClass() {
//		System.out.println("After class");
	}
	
	/**
	 * O ideal � que cada m�todo de teste, seja utilizado para validar um �nico ponto, e que tenha apenas uma assertiva, visto que o Junit para ao encontrar uma assertiva que falhou,
	 * e caso exista mais de um teste que vai falhar, apenas o primeiro ser� mostrado.
	 * Da pra contornar isso utilizando Rule, conforme exemplo abaixo.
	 * @throws Exception 
	 */
	
	@Test
	/**
	 * Considerar o m�todo deveAlugarFilmeComRule, ele est� mais completo
	 * @throws Exception
	 */
	public void deveAlugarFilme() throws Exception {
		
		/**
		 * Com o assume, posso definir que o meu teste s� vai executar em certas condi��es, como nesse exemplo, s� quando n�o for sabados
		 */
//		assumeThat(new Date(), not(Calendar.SATURDAY));
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// A��o
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertEquals(5.0, locacao.getValor(), 0.01);
		
		// Esses s�o mais para quest�o de leitura, conforme exemplos abaixo
		assertThat(locacao.getValor(), CoreMatchers.is(5.0));
		
		// A leitura ficaria verifique que o valor de loca��o � igual a 5
		assertThat(locacao.getValor(), is(equalTo(5.0)));
		// A leitura ficaria verifique que o valor de loca��o n�o � igual a 6.
		assertThat(locacao.getValor(), is(not(6.0)));
		
		
		// Utilizando o assertThat, daria para rescrever essa assertiva conforme abaixo
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		
		assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		
		// Reescrevendo esse tamb�m com assertThat
		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		
		assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		
	}
	
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
	
	/**
	 * � poss�vel passar para o teste o par�metro expected, conforme abaixo, dizendo que � experado que dispare um exception.
	 * Desta forma o teste passa caso dispare uma Exception. Isso pode ser um problema, pois Exception � muito gen�rico, e pode ser qualquer coisa.
	 * Se fosse uma exception bem espec�fica at� daria para utilizar essa abordagem.
	 * @throws Exception 
	 */
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoqueEvitarAssim() throws Exception {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		// A��o
		service.alugarFilme(usuario, filmes);
		
	}
	
	/**
	 * Quando a exce��o for mais gen�rica, � melhor validar assim.
	 */
	@Test
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoqueSegundaPreferencia() {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		try {
			// A��o
			service.alugarFilme(usuario, filmes);
			
			// Necess�rio esse assertfail, pois o objetivo aqui � validar se a regra de filme sem estoque est� funcional.
			// Ent�o esse teste deve lan�ar exception para ser considerado v�lido.
			fail("Deveria ter lan�ado exception");
		} catch (Exception e) {
			assertThat(e.getMessage(), is("Filme sem estoque"));
		}
		
	}

	@Rule
	public ExpectedException exException = ExpectedException.none();
	
	 
	
	@Test
	/**
	 * Preferir esse abordagem ao validar exception, pois da pra validar se veio a exception esperada e ainda � poss�vel validar a mensagem, 
	 * n�o sendo necess�rio colocar o fail caso n�o d� a expcetion como no anterior
	 * @throws Exception
	 */
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoquePreferirEsseTratamento() throws Exception {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		// Deve ser declarado antes da execu��o da a��o
		exException.expect(Exception.class);
		exException.expectMessage("Filme sem estoque");
		
		// A��o
		service.alugarFilme(usuario, filmes);

	}
	
	@Test
	public void naoDeveAlugarFilme_UsuarioVazio() throws FilmeSemEstoqueException {
	
		Usuario usuario = null;
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Neste caso, como est� sem esperado uma LocadoraException, qualquer outra exception, como o FilmeSemEstoqueException, 
		 * apenas � lan�ada para o Junit tratar. 
		 * S� tratar no catch o que � esperado.
		 */
		
		try {
			// A��o
			service.alugarFilme(usuario, filmes);
			fail("N�o est� validando usu�rio");
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usu�rio de loca��o n�o informado"));
		}
		
	}
	
	/**
	 * Preferir esse tratamento, utilizando a rule. Se precisar de mais controle, utilizar conforme testLocacao_UsuarioVazio()
	 * @throws LocadoraException
	 * @throws FilmeSemEstoqueException
	 */
	@Test
	public void naoDeveAlugarFilme_FilmeVazio_PreferirEsseTratamento() throws LocadoraException, FilmeSemEstoqueException {
		
		Usuario usuario = umUsuario().agora();
		
		// Deve ser declarado antes da execu��o da a��o
		exException.expect(LocadoraException.class);
		exException.expectMessage("Filme de loca��o n�o informado");
		
		// A��o
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void devePagar75porcentoNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora());
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3
		
		assertThat(locacao.getValor(), is(11.0));
	}
	
	@Test
	public void devePagar50porcentoNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora());
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3+2
		
		assertThat(locacao.getValor(), is(13.0));
	}
	
	@Test
	public void devePagar25porcentoNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora());
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3+2+1
		
		assertThat(locacao.getValor(), is(14.0));
	}
	
	@Test
	public void naoDevePagar0porcentoNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora(), umFilme().comValor(4.0).agora());
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3+2+1
		
		assertThat(locacao.getValor(), is(14.0));
	}
	
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
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 04, 2017)); // S�bado
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2017);
		
		/**
		 * Para mostrar como fazer com m�todos est�ticos, foi modificado no m�todo alugar, para pegar a data a partir do calendar.
		 * E para mockar m�todos est�ticos, antes precisa dessa linha
		 */
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
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
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//topzera
		Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		/**
		 * Neste caso, como � necess�rio verifica��o posterior, n�o da pra utilizar o tratamento abaixo, pois o teste ir� parar ao lan�ar a exception
		 * 		 */
		
//		exException.expect(LocadoraException.class);
//		exException.expectMessage("Usu�rio Negativado");
		
		// acao
		try {
			service.alugarFilme(usuario, filmes);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usu�rio Negativado"));
		}
		
		//verificacao
		// Verifica se na a��o, o m�todo possuiNegataivacao foi chamado com o usu�rio em quest�o
		Mockito.verify(spcService).possuiNegativacao(usuario);
		
			
	}

	@Test
	public void deveEnvialEmailParaLocacoesAtrasadas() {
		
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usu�rio em dia").agora();
		
		List<Locacao> locacoes = Arrays.asList(umLocacao().comUsuario(usuario).atrasada().agora(),
				umLocacao().comUsuario(usuario).atrasada().agora(), umLocacao().comUsuario(usuario2).agora()); 
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		service.notificarAtrasos();
		
		// Verificacao
		// Vai verificar se o m�todo notificarAtraso foi chamado com o usu�rio em quest�o
//		Mockito.verify(emailService).notificarAtraso(usuario);
		
		/**
		 * Da pra determinar o n�mero de vezes que certo m�todo vai deve ser chamado com determinado par�metro
		 * Tamb�m tem os m�todos atLeast(x) (pelo menos x vezes), atLeastOnce() (pelo menos uma vez), atMost(x).
		 *  
		 */
		Mockito.verify(emailService, Mockito.times(2)).notificarAtraso(usuario);
		
		/**
		 * Ainda da pra verificar se um determinado m�todo, foi chamado uma quantidade x de vezes, independente do par�metro passado, desde que atenda a assinatura.
		 * Ent�o deve executar duas vezes, idependente do usu�rio recebido 
		 */
//		Mockito.verify(emailService, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
		
		// Verifica que notificarAtraso com o usuario2 nunca foi chamado
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		
		// Garantir que depois das execu��es acima, n�o haver� mais nenhuma invoca��o ao service EmailService
		Mockito.verifyNoMoreInteractions(emailService);
		
		// Garantir que n�o houve intera��es com o spc service nessa execu��o
		// Isso � s� um exemplo, n�o seria necess�rio testar isso, pois spcService n�o � utilizado nesse m�todo notificarAtraso
		Mockito.verifyZeroInteractions(spcService);
		
	}
	
	@Test
	public void deveTratarErroSPC() throws Exception {
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Quando o m�todo possuiNegativacao for chamado com o usuario em quest�o, simplemente ele retorna uma exception
		 * Isso serve para poder validar o tratamento de excessoes, pois o esperado � que quando esse m�todo retornar uma exception,
		 * o alugarFilme deve tratar e lan�ar uma  LocadoraException("Problemas com SPC, tente novamente")
		 */

		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastr�fica"));

		// verificacao		
		exException.expect(LocadoraException.class);
		exException.expectMessage("Problemas com SPC, tente novamente");
		
		// acao
		service.alugarFilme(usuario, filmes);
		

	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		
		// cenario
		Locacao locacao = umLocacao().agora();
		int dias = 3;
		
		//acao
		service.prorrogarLocacao(locacao, dias);
		
		// verificacao
		
		/**
		 * ArgumentCaptor serve para capturar um valor passado para um m�todo. Conforme abaixo, est� capturando o objeto locacao que est� sendo passado para o m�todo salvar.
		 * Como o m�todo prorrogarLocacao n�o retorna a locacao nova gerada pelo m�todo salvar, n�o teria como avaliar o novo objeto gerado sem isso.
		 */
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		
		Mockito.verify(dao).salvar(argCapt.capture());
		
		Locacao locacaoRetornada = argCapt.getValue();
		
		// Utilizando os asserts como abaixo, caso mais de um falhe, mostrara apenas o primeiro
		// para avaliar todos, utilizar a rule ErrorCollector
//		assertThat(locacaoRetornada.getValor(), is(4.0 * dias));
//		assertThat(locacaoRetornada.getDataLocacao(), ehHoje());
//		assertThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(dias));
		
		error.checkThat(locacaoRetornada.getValor(), is(4.0 * dias));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(dias));
		
	}
}

