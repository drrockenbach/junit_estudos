package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;

/**
 * Exemplo de DDT - Data driven test
 * @author diomar.rockenbach
 *
 */

// Para testes parametrizados
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	/* Como não foi definido o valor desse parameter, considera zero.
	 * Isso quer dizer que vai pegar o primeiro item da matriz retornada pelo getParametros, que no caso é o array de filmes, 
	 * sendo esse os dados de entrada dinâmicos dos métodos de teste
	 */
	
	@Parameter
	public List<Filme> filmes;
	
	// Segundo item da matriz retornada pelo getParametros, sendo o valor utilizado para fazer a validação do cenário
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;

	private LocacaoService service;
	
	@Before
	public void setup() {
		service = new LocacaoService();
//		LocacaoDAO dao = new LocacaoDAOFake();
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
	}
	
	private static Filme filme1 = umFilme().comValor(4.0).agora();
	private static Filme filme2 = umFilme().comValor(4.0).agora();
	private static Filme filme3 = umFilme().comValor(4.0).agora();
	private static Filme filme4 = umFilme().comValor(4.0).agora();
	private static Filme filme5 = umFilme().comValor(4.0).agora();
	private static Filme filme6 = umFilme().comValor(4.0).agora();
	
	/**
	 * O @Parameters diz para o Junit utilizar esse método como fonte de dados para os testes
	 * @return
	 */
//	@Parameters(name="Teste de cálculo de locacao {index} = {0} - {1}")
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros() {
		// Preparação para o DDT
		// Cada linha da matriz representa um cenário de teste
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2), 8.0, "Sem desconto"}, // linha 1
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 25%"}, // linha 1
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes: 50%"}, // linha 2
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 filmes: 75%"}, // linha 3
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 filmes: 100%"}, // linha 4
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws LocadoraException, FilmeSemEstoqueException {
		// Cenario
		
		Usuario u = new Usuario("Usuario");
		
		// acao
		Locacao locacao = service.alugarFilme(u, filmes);
		
		// verificacao
		//4+4+3
		
		assertThat(locacao.getValor(), is(valorLocacao));
	}

	
	
}
