package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {

		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", " "));
	}

	@Test
	@DisplayName("Cadastrar usuário")
	public void deveCriarUmUsuario() {

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Betty", "beth@email.com", "12345678", " "));

		// Requisicao HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("Não deve duplicar usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Amanda", "amanda@email.com", "12345678", " "));

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Amanda", "amanda@email.com", "12345678", " "));

		// Requisicao HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("Você deve atualizar usuário")
	public void deveAtualizarUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Kendal", "kendal@email.com", "12345678", " "));

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(usuarioCadastrado.get().getId(), "Kendal Katherine", "kendalk@email.com", "87654321", " "));

		// Requisicao HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("Você deve Listar todos usuário")
	public void deveListarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Vitor", "vitor@email.com", "12345678", " "));

		usuarioService.cadastrarUsuario(new Usuario(0L, "Samara", "samara@email.com", "12345678", " "));

		// Requisicao HTTP
		ResponseEntity<String> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("Você deve Listar por id usuário")
	public void deveListarPorIdUsuarios() {
		
		Optional<Usuario> usuarioBusca = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Mel", "mel@email.com", "12345678", " "));

		// Requisicao HTTP
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + usuarioBusca.get().getId(), HttpMethod.GET, null, String.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}

	@Test
	@DisplayName("Deve logar usuário")
	public void deveLogarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Tiger", "tiger@email.com", "12345678", " "));
		
		// Corpo da Requisição
		HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(
				new UsuarioLogin(0L, "", "tiger@email.com", "12345678", "", ""));

		// Requisicao HTTP
		ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate
				.exchange("/usuarios/logar", HttpMethod.POST,corpoRequisicao, UsuarioLogin.class);

		// Verifica o HTTP status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
}
