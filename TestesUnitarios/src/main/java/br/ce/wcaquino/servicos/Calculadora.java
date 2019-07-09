package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exception.DivisaoPorZeroException;

public class Calculadora {

	public int somar(int a, int b) {
		System.out.println("Executou somar");
		return a + b;
	}

	public int subtrair(int a, int b) {
		return a-b;
	}

	public double dividir(int a, int b) throws DivisaoPorZeroException {
		if (b == 0) {
			throw new DivisaoPorZeroException();
		}
		return a/b;
	}
	
	public void imprime() {
		System.out.println("Calculadora: imprime");
		
	}
}
