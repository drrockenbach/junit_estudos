package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes, Date dataAtual) throws LocadoraException, FilmeSemEstoqueException {

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
		locacao.setDataLocacao(dataAtual);
		locacao.setValor(calcularValorLocacao(filmes));

		// Entrega no dia seguinte
		Date dataEntrega = dataAtual;
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, +1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {
		return alugarFilme(usuario, filmes, new Date());
	}

	private double calcularValorLocacao(List<Filme> filmes) {
		double valor = IntStream.range(0, filmes.size())
				.mapToDouble(idx -> precoComDesconto(filmes.get(idx).getPrecoLocacao(), idx))
				.reduce((ac, preco) -> ac += preco).orElse(0);

		return valor;

	}

	private double precoComDesconto(double preco, int idx) {
		double desconto = 0.0;

		switch (idx) {
		case 2:
			desconto = 0.25;
			break;
		case 3:
			desconto = 0.5;
			break;
		case 4:
			desconto = 0.75;
			break;
		case 5:
			desconto = 1.0;
			break;
		}

		return preco * (1 - desconto);
	}

	private void validarEstoque(List<Filme> filmes) throws FilmeSemEstoqueException {

		boolean semEstoque = filmes.stream().anyMatch(filme -> filme.getEstoque() == 0);

		if (semEstoque) {
			throw new FilmeSemEstoqueException("Filme sem estoque");
		}

	}
	
	public void setLocacaoDAO(LocacaoDAO dao) {
		this.dao = dao;
	}
	
}