package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		Assert.assertEquals(1, 1);
		
		/**
		 * Terceiro parâmetro é um valor de comparação
		 * para arrendondamento.
		 * No caso abaixo, mesmo os valores sendo diferentes nas casas decimais,
		 * o teste vai passar.
		 */
		Assert.assertEquals(0.5123, 0.51335, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		// Não tem metodo com autoboxing no Junit
		Assert.assertEquals("Erro, valores não são iguais",Integer.valueOf(i), i2);
		Assert.assertEquals(i2.intValue(), i);

		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		
		Assert.assertTrue("bola".startsWith("bo"));
	
		Usuario u1 = new Usuario("Usuário 1");
		Usuario u2 = new Usuario("Usuário 1");
		Usuario u3 = u2;
		Usuario u4 = null;
		
		// Vai comparar usando o equals do objeto
		Assert.assertEquals(u1, u2);
		
		// Compara se são a mesma instância
		Assert.assertSame(u3, u2);
		Assert.assertNotSame(u1, u2);
		
		Assert.assertTrue(u4 == null);
		Assert.assertNull(u4);
		Assert.assertNotNull(u3);
		
	}
	
}
