package com.threestyle.productapi.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.threestyle.productapi.model.Produto;
import com.threestyle.productapi.service.ProdutoService;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

	@Autowired
	ProdutoService produtoService;

	@GetMapping()
	public ResponseEntity<List<Produto>> listAll() {
		/*
		 * return new
		 * ResponseEntity<Iterable<Produto>>(service.getProdutos(),HttpStatus.OK); este
		 * é um atalho para o de cima
		 */
		return ResponseEntity.ok(produtoService.getProdutos());
	}

	@GetMapping("/{idproduto}")
	public ResponseEntity<Produto> findById(@PathVariable("idproduto") Long id) {
		Optional<Produto> produto = produtoService.getProdutoById(id);

		// uso com operador ternário
		return produto.isPresent() ? ResponseEntity.ok(produto.get()) : ResponseEntity.notFound().build();
	}

	/*
	 * salva um objeto do tipo Produto, sendo que a anotação @RequestBody converte o
	 * Json para o objeto. ao insetir um produto, pegaremos a URI dela para fornecer
	 * como resposta
	 */
	@PostMapping
	public ResponseEntity<Produto> create(@RequestBody @Valid Produto produto) {

		Produto produtoSalvo = produtoService.create(produto);
		URI uri = getUri(produtoSalvo.getId());
		return ResponseEntity.created(uri).build();

	}
	
	private URI getUri(Long id) {
		
		return ServletUriComponentsBuilder
				// pega o caminho padrão para inserir um produto ("/api/v1/produtos")
				.fromCurrentRequest()
				// acrescenta o id na URI ("/api/v1/produtos/id");
				.path("/{id}")
				// pega o id do objeto salvo acima;
				.buildAndExpand(id)
				// converte tudo em URI.
				.toUri();
	}

	/*
	 * o método do put é mais complexo. Preste atenção. Com este mapeamento, é
	 * possível passar o id que será atualizado na própria URL
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Produto> update(@RequestBody Produto produto, @PathVariable("id") Long id) {
		return produtoService.update(produto, id) == null ? ResponseEntity.notFound().build()
				: ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable("id") Long id) {
		produtoService.delete(id);
		return ResponseEntity.ok().build();
	}

}