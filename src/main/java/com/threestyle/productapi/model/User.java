package com.threestyle.productapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome", length = 45)
	@NotBlank(message = "O nome não pode ser nulo")
	private String nome;
	
	@Column(name = "login", length = 45, unique = true)
	@NotBlank(message = "O login não pode ser vazio")
	private String login;
	
	@Column(name = "senha", length = 45, nullable = false)
	@Size(min = 5)
	@NotBlank(message = "a senha precisa ter 5 caracteres")
	private String senha;
	
	@Column(name = "email", length = 100, unique = true)
	@NotBlank(message = "O email é inválido")
	private String email;
}
