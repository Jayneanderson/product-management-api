package com.threestyle.productapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.threestyle.productapi.repository.UserRepository;

@Service(value = "userDetailsService")//objeto identificador
public class UserDetailsServiceImpl implements UserDetailsService { //apenas um método é implementado
	
	@Autowired
	private UserRepository userRepository;
	//é um método que serve para rivate UserRepository userRepository;encontrar um usuário, caso não encontre lança uma exceção
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		com.threestyle.productapi.model.User user = userRepository.findByLogin(username);
		
		//se o usuário não for encontrado no banco de dados, eu retorno uma exceção
		if(user == null) {
			throw new UsernameNotFoundException("user not found"); 
		}
	//caso o usuário exista, retornarei o usuário com a role USER
		return User.withUsername(username).password(user.getSenha()).roles("USER").build();
	}

}
