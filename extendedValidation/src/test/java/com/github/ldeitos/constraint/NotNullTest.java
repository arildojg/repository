package com.github.ldeitos.constraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;

import com.github.ldeitos.qualifier.ExtendedValidator;
import com.github.ldeitos.validation.impl.interpolator.ExtendedValidationBaseTest;
import com.github.ldeitos.validation.impl.interpolator.TestMessageSource;

@AdditionalClasses({TestMessageSource.class})
public class NotNullTest extends ExtendedValidationBaseTest {
	
	private static final String MENSAGEM_ESPERADA = "NotNull Teste";

	@Inject
	@ExtendedValidator
	private Validator validador;
		
	@Test
	public void testNullValue(){
		Teste var = new Teste("");
		Set<ConstraintViolation<Teste>> violacoes = validador.validate(var);
		assertTrue(violacoes.isEmpty());
	}
	
	@Test
	public void testNotNullValue(){
		Teste var = new Teste();
		Set<ConstraintViolation<Teste>> violacoes = validador.validate(var);
		assertFalse(violacoes.isEmpty());
		assertEquals(1, violacoes.size());
		assertEquals(MENSAGEM_ESPERADA, violacoes.iterator().next().getMessage());
	}
	
	static class Teste {
		@NotNull(messageParameters = {"par=Teste"})
		private String campo;
		
		Teste(){
		}
		
		Teste(String val){
			campo = val;
		}
	}
}
