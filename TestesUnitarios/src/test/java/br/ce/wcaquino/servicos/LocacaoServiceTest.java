package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.hamcrest.CoreMatchers;
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
	
	
	/**
	 * O ideal é que cada método de teste, seja utilizado para validar um único ponto, e que tenha apenas uma assertiva, visto que o Junit para ao encontrar uma assertiva que falhou,
	 * e caso exista mais de um teste que vai falhar, apenas o primeiro será mostrado.
	 * Da pra contornar isso utilizando Rule, conforme exemplo abaixo.
	 * @throws Exception 
	 */
	
	@Test
	public void testeLocacao() throws Exception {
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filme);
		
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
	public void testeLocacaoComRule() throws Exception {
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.0);
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		error.checkThat(locacao.getValor(), CoreMatchers.is(5.0));
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getValor(), is(not(6.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		
		
	}
	
	/**
	 * É possível passar para o teste o parâmetro expected, conforme abaixo, dizendo que é experado que dispare um exception.
	 * Desta forma o teste passa caso dispare uma Exception. Isso pode ser um problema, pois Exception é muito genérico, e pode ser qualquer coisa.
	 * Se fosse uma exception bem específica até daria para utilizar essa abordagem.
	 * @throws Exception 
	 */
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testLocacao_filmeSemEstoqueEvitarAssim() throws Exception {
	
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filme);
		
	}
	
	/**
	 * Quando a exceção for mais genérica, é melhor validar assim.
	 */
	@Test
	public void testLocacao_filmeSemEstoqueSegundaPreferencia() {
	
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		try {
			// Ação
			Locacao locacao = service.alugarFilme(usuario, filme);
			
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
	public void testLocacao_filmeSemEstoquePreferirEsseTratamento() throws Exception {
	
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		// Deve ser declarado antes da execução da ação
		exException.expect(Exception.class);
		exException.expectMessage("Filme sem estoque");
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filme);

	}
	
	@Test
	public void testLocacao_UsuarioVazio() throws FilmeSemEstoqueException {
	
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = null;
		Filme filme = new Filme("Filme 1", 1, 5.0);
		
		/*
		 * Neste caso, como está sem esperado uma LocadoraException, qualquer outra exception, como o FilmeSemEstoqueException, 
		 * apenas é lançada para o Junit tratar. 
		 * Só tratar no catch o que é esperado.
		 */
		
		try {
			// Ação
			Locacao locacao = service.alugarFilme(usuario, filme);
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
	public void testeLocacao_FilmeVazio_PreferirEsseTratamento() throws LocadoraException, FilmeSemEstoqueException {
		
		LocacaoService service = new LocacaoService();
		
		Usuario usuario = new Usuario("Usuario 1");
		
		// Deve ser declarado antes da execução da ação
		exException.expect(LocadoraException.class);
		exException.expectMessage("Filme de locação não informado");
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, null);
		
	}
}

