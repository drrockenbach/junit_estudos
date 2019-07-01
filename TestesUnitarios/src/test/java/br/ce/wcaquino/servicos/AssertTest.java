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
		 * Terceiro par�metro � um valor de compara��o
		 * para arrendondamento.
		 * No caso abaixo, mesmo os valores sendo diferentes nas casas decimais,
		 * o teste vai passar.
		 */
		Assert.assertEquals(0.5123, 0.51335, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		// N�o tem metodo com autoboxing no Junit
		Assert.assertEquals("Erro, valores n�o s�o iguais",Integer.valueOf(i), i2);
		Assert.assertEquals(i2.intValue(), i);

		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		
		Assert.assertTrue("bola".startsWith("bo"));
	
		Usuario u1 = new Usuario("Usu�rio 1");
		Usuario u2 = new Usuario("Usu�rio 1");
		Usuario u3 = u2;
		Usuario u4 = null;
		
		// Vai comparar usando o equals do objeto
		Assert.assertEquals(u1, u2);
		
		// Compara se s�o a mesma inst�ncia
		Assert.assertSame(u3, u2);
		Assert.assertNotSame(u1, u2);
		
		Assert.assertTrue(u4 == null);
		Assert.assertNull(u4);
		Assert.assertNotNull(u3);
		
	}
	
}
