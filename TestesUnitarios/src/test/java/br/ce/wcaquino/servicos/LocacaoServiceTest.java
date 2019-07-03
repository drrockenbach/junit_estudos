package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.matchers.MatchersProprios.caiNaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
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

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	static LocacaoService service;
	
	/**
	 * Before � executado antes de cada m�todo de teste
	 */
	@Before
	public void setup() {
		service = new LocacaoService();
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
	public void deveAlugarFilme() throws Exception {
		
		/**
		 * Com o assume, posso definir que o meu teste s� vai executar em certas condi��es, como nesse exemplo, s� quando n�o for sabados
		 */
//		assumeThat(new Date(), not(Calendar.SATURDAY));
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
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
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
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
	
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		// A��o
		service.alugarFilme(usuario, filmes);
		
	}
	
	/**
	 * Quando a exce��o for mais gen�rica, � melhor validar assim.
	 */
	@Test
	public void naoDeveAlugarFilmeSemEstoque_filmeSemEstoqueSegundaPreferencia() {
	
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
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
	
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		// Deve ser declarado antes da execu��o da a��o
		exException.expect(Exception.class);
		exException.expectMessage("Filme sem estoque");
		
		// A��o
		service.alugarFilme(usuario, filmes);

	}
	
	@Test
	public void naoDeveAlugarFilme_UsuarioVazio() throws FilmeSemEstoqueException {
	
		Usuario usuario = null;
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
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
		
		Usuario usuario = new Usuario("Usuario 1");
		
		// Deve ser declarado antes da execu��o da a��o
		exException.expect(LocadoraException.class);
		exException.expectMessage("Filme de loca��o n�o informado");
		
		// A��o
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void devePagar75porcentoNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = new Usuario("Usuario");
		
		List<Filme> filmes = Arrays.asList(new Filme("A", 1, 4.0), new Filme("B", 1, 4.0), new Filme("C", 1, 4.0));
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3
		
		assertThat(locacao.getValor(), is(11.0));
	}
	
	@Test
	public void devePagar50porcentoNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = new Usuario("Usuario");
		
		List<Filme> filmes = Arrays.asList(new Filme("A", 1, 4.0), new Filme("B", 1, 4.0), new Filme("C", 1, 4.0), new Filme("D", 1, 4.0));
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3+2
		
		assertThat(locacao.getValor(), is(13.0));
	}
	
	@Test
	public void devePagar25porcentoNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = new Usuario("Usuario");
		
		List<Filme> filmes = Arrays.asList(new Filme("A", 1, 4.0), new Filme("B", 1, 4.0), new Filme("C", 1, 4.0), new Filme("D", 1, 4.0), new Filme("E", 1, 4.0));
		
		// acao
		
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3+2+1
		
		assertThat(locacao.getValor(), is(14.0));
	}
	
	@Test
	public void naoDevePagar0porcentoNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = new Usuario("Usuario");
		
		List<Filme> filmes = Arrays.asList(new Filme("A", 1, 4.0), new Filme("B", 1, 4.0), new Filme("C", 1, 4.0), new Filme("D", 1, 4.0), new Filme("E", 1, 4.0), new Filme("F", 1, 4.0));
		
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
		 * Com o assume, posso definir que o meu teste s� vai executar em certas condi��es, como nesse exemplo, s� em sabados
		 */
//		assumeThat(new Date(), is(Calendar.SATURDAY));
		
		//Cenario 
		Usuario u = new Usuario("Teste");
		List<Filme> filmes = Arrays.asList(new Filme("A", 1, 4.0));
		
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
}

