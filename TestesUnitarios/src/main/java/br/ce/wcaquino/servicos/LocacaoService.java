package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException  {
		
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme de locação não informado");
		}
		
		validarEstoque(filmes);
		
		if (usuario == null) {
			throw new LocadoraException("Usuário de locação não informado");
		}
		
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filmes.stream().mapToDouble(filme -> filme.getPrecoLocacao()).reduce( (ac, preco) -> ac += preco).orElse(0));

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar metodo para salvar
		
		return locacao;
	}

	private void validarEstoque(List<Filme> filmes) throws FilmeSemEstoqueException {

		boolean semEstoque = filmes.stream().anyMatch( filme -> filme.getEstoque() == 0);
		
		if (semEstoque) {
			throw new FilmeSemEstoqueException("Filme sem estoque");
		}
		
	}

	
}