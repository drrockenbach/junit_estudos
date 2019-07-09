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

import java.lang.reflect.Executable;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	/**
	 * Com as injeções dos mocks, não precisa nem dos sets no LocacaoService.
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
		
//		service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		spcService = Mockito.mock(SPCService.class);
//		service.setSPCService(spcService);
//		emailService = Mockito.mock(EmailService.class);
//		service.setEmailService(emailService);
	}
	
	/**
	 * After é executado depois de cada método de teste
	 */
	@After
	public void after() {
//		System.out.println("After");
	}
	
	/**
	 * Before é executado antes de cada método de teste
	 */
	@BeforeClass
	public static void setupClass() {
//		System.out.println("Before class");
	}
	
	/**
	 * After é executado depois de cada método de teste
	 */
	@AfterClass
	public static void afterClass() {
//		System.out.println("After class");
	}
	
	/**
	 * O ideal é que cada método de teste, seja utilizado para validar um único ponto, e que tenha apenas uma assertiva, visto que o Junit para ao encontrar uma assertiva que falhou,
	 * e caso exista mais de um teste que vai falhar, apenas o primeiro será mostrado.
	 * Da pra contornar isso utilizando Rule, conforme exemplo abaixo.
	 * @throws Exception 
	 */
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		/**
		 * Com o assume, posso definir que o meu teste só vai executar em certas condições, como nesse exemplo, só quando não for sabados
		 */
//		assumeThat(new Date(), not(Calendar.SATURDAY));
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertEquals(5.0, locacao.getValor(), 0.01);
		
		// Esses são mais para questão de leitura, conforme exemplos abaixo
		assertThat(locacao.getValor(), CoreMatchers.is(5.0));
		
		// A leitura ficaria verifique que o valor de locação é igual a 5
		assertThat(locacao.getValor(), is(equalTo(5.0)));
		// A leitura ficaria verifique que o valor de locação não é igual a 6.
		assertThat(locacao.getValor(), is(not(6.0)));
		
		
		// Utilizando o assertThat, daria para rescrever essa assertiva conforme abaixo
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		
		assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		
		// Reescrevendo esse também com assertThat
		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		
		assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		
	}
	
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
	
	/**
	 * É possível passar para o teste o parâmetro expected, conforme abaixo, dizendo que é experado que dispare um exception.
	 * Desta forma o teste passa caso dispare uma Exception. Isso pode ser um problema, pois Exception é muito genérico, e pode ser qualquer coisa.
	 * Se fosse uma exception bem específica até daria para utilizar essa abordagem.
	 * @throws Exception 
	 */
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoqueEvitarAssim() throws Exception {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		// Ação
		service.alugarFilme(usuario, filmes);
		
	}
	
	/**
	 * Quando a exceção for mais genérica, é melhor validar assim.
	 */
	@Test
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoqueSegundaPreferencia() {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		try {
			// Ação
			service.alugarFilme(usuario, filmes);
			
			// Necessário esse assertfail, pois o objetivo aqui é validar se a regra de filme sem estoque está funcional.
			// Então esse teste deve lançar exception para ser considerado válido.
			fail("Deveria ter lançado exception");
		} catch (Exception e) {
			assertThat(e.getMessage(), is("Filme sem estoque"));
		}
		
	}

	@Rule
	public ExpectedException exException = ExpectedException.none();
	
	 
	
	@Test
	/**
	 * Preferir esse abordagem ao validar exception, pois da pra validar se veio a exception esperada e ainda é possível validar a mensagem, 
	 * não sendo necessário colocar o fail caso não dê a expcetion como no anterior
	 * @throws Exception
	 */
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoquePreferirEsseTratamento() throws Exception {
	
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		// Deve ser declarado antes da execução da ação
		exException.expect(Exception.class);
		exException.expectMessage("Filme sem estoque");
		
		// Ação
		service.alugarFilme(usuario, filmes);

	}
	
	@Test
	public void naoDeveAlugarFilme_UsuarioVazio() throws FilmeSemEstoqueException {
	
		Usuario usuario = null;
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Neste caso, como está sem esperado uma LocadoraException, qualquer outra exception, como o FilmeSemEstoqueException, 
		 * apenas é lançada para o Junit tratar. 
		 * Só tratar no catch o que é esperado.
		 */
		
		try {
			// Ação
			service.alugarFilme(usuario, filmes);
			fail("Não está validando usuário");
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário de locação não informado"));
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
		
		// Deve ser declarado antes da execução da ação
		exException.expect(LocadoraException.class);
		exException.expectMessage("Filme de locação não informado");
		
		// Ação
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
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		
		/**
		 * Com o assume, posso definir que o meu teste só vai executar em certas condições, como nesse exemplo, só em sabados
		 */
//		assumeThat(new Date(), is(Calendar.SATURDAY));
		
		//Cenario 
		Usuario u = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(4.0).agora());
		
		Calendar sabado = Calendar.getInstance(); 
		sabado.set(2019, Calendar.JULY, 6);
		// acao
		Locacao locacao = service.alugarFilme(u, filmes, sabado.getTime());
		
		// verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		assertThat(ehSegunda, is(true));
		
		assertThat(locacao.getDataRetorno(), caiNaSegunda());
//		assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
				
	}
	
	@Test 
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//topzera
		Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		/**
		 * Neste caso, como é necessário verificação posterior, não da pra utilizar o tratamento abaixo, pois o teste irá parar ao lançar a exception
		 * 		 */
		
//		exException.expect(LocadoraException.class);
//		exException.expectMessage("Usuário Negativado");
		
		// acao
		try {
			service.alugarFilme(usuario, filmes);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário Negativado"));
		}
		
		//verificacao
		// Verifica se na ação, o método possuiNegataivacao foi chamado com o usuário em questão
		Mockito.verify(spcService).possuiNegativacao(usuario);
		
	}

	@Test
	public void deveEnvialEmailParaLocacoesAtrasadas() {
		
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuário em dia").agora();
		
		List<Locacao> locacoes = Arrays.asList(umLocacao().comUsuario(usuario).atrasada().agora(),
				umLocacao().comUsuario(usuario).atrasada().agora(), umLocacao().comUsuario(usuario2).agora()); 
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		service.notificarAtrasos();
		
		// Verificacao
		// Vai verificar se o método notificarAtraso foi chamado com o usuário em questão
//		Mockito.verify(emailService).notificarAtraso(usuario);
		
		/**
		 * Da pra determinar o número de vezes que certo método vai deve ser chamado com determinado parâmetro
		 * Também tem os métodos atLeast(x) (pelo menos x vezes), atLeastOnce() (pelo menos uma vez), atMost(x).
		 *  
		 */
		Mockito.verify(emailService, Mockito.times(2)).notificarAtraso(usuario);
		
		/**
		 * Ainda da pra verificar se um determinado método, foi chamado uma quantidade x de vezes, independente do parâmetro passado, desde que atenda a assinatura.
		 * Então deve executar duas vezes, idependente do usuário recebido 
		 */
//		Mockito.verify(emailService, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
		
		// Verifica que notificarAtraso com o usuario2 nunca foi chamado
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		
		// Garantir que depois das execuções acima, não haverá mais nenhuma invocação ao service EmailService
		Mockito.verifyNoMoreInteractions(emailService);
		
		// Garantir que não houve interações com o spc service nessa execução
		// Isso é só um exemplo, não seria necessário testar isso, pois spcService não é utilizado nesse método notificarAtraso
		Mockito.verifyZeroInteractions(spcService);
		
	}
	
	@Test
	public void deveTratarErroSPC() throws Exception {
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		/*
		 * Quando o método possuiNegativacao for chamado com o usuario em questão, simplemente ele retorna uma exception
		 * Isso serve para poder validar o tratamento de excessoes, pois o esperado é que quando esse método retornar uma exception,
		 * o alugarFilme deve tratar e lançar uma  LocadoraException("Problemas com SPC, tente novamente")
		 */

		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));

		// verificacao		
		exException.expect(LocadoraException.class);
		exException.expectMessage("Problemas com SPC, tente novamente");
		
		// acao
		service.alugarFilme(usuario, filmes);
		

	}
}

