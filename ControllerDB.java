package com.wooffinder.controllers;

import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RestController
public class ControllerDB {

	@CrossOrigin()
	@PostMapping("/login")
	public ResponseEntity<String> ejemplo(@RequestBody String mensaje) {

		System.out.println("Mensaje recibido desde el cliente: " + mensaje);

		JSONObject jsonObject = new JSONObject(mensaje);

		String page_request = jsonObject.getString("page_request");

		String response = "0";

		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setUrl("jdbc:mysql://localhost:3305/hackathon_backend_db");
		dataSource.setUsername("root");
		dataSource.setPassword("root123");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		System.out.println(page_request);

		
		JSONObject json = new JSONObject();
		
		if (page_request.equals("login")) {
			String username_login = jsonObject.getString("username_db");
			String user_password_login = jsonObject.getString("user_password_db");

			String sql_username_login = "SELECT COUNT(*) FROM Users WHERE username = ?";
			int count_username_login = jdbcTemplate.queryForObject(sql_username_login, Integer.class, username_login);

			System.out.println(page_request);
			if (count_username_login > 0) {
				System.out.println("El email " + username_login + " ya existe en la tabla usuarios");

				String password = jdbcTemplate.queryForObject("SELECT user_password FROM Users WHERE username = ?",
						String.class, username_login);
				Integer id = jdbcTemplate.queryForObject("SELECT users_id FROM Users WHERE username = ?",
						Integer.class, username_login);
				if (password.equals(user_password_login)) {
					json.put("id", id);
					json.put("response", "correct password");
					response = json.toString();
				} else {
					json.put("response", "incorrect password");
					response = json.toString();
				}

			} else {
				json.put("response", "Esta cuenta no existe");
				response = json.toString();
			}
		}

		if (page_request.equals("register")) {
			String username_register = jsonObject.getString("username_db");
			String user_password_register = jsonObject.getString("user_password_db");

			String sql_username_register = "SELECT COUNT(*) FROM Users WHERE username = ?";
			int count_username_register = jdbcTemplate.queryForObject(sql_username_register, Integer.class, username_register);

			if (count_username_register > 0) {
				System.out.println("El usuario " + username_register + " ya existe en la tabla usuarios");
				json.put("response", "este usuario ya esta registrado");
				response = json.toString();
			} else {
				System.out.println("El usuario " + username_register + " no existe en la tabla usuarios");
				String sql_registro = "INSERT INTO Users (username, user_password) VALUES (?,?)";
				jdbcTemplate.update(sql_registro, username_register, user_password_register);
				json.put("response", "registro exitoso");
				response = json.toString();
				
				String token="";
				
				try {
		            MessageDigest digest = MessageDigest.getInstance("SHA-256");
		            byte[] encodedHash = digest.digest(username_register.getBytes(StandardCharsets.UTF_8));

		            // Convertir el hash en una representación hexadecimal
		            StringBuilder hexString = new StringBuilder();
		            for (byte b : encodedHash) {
		                String hex = Integer.toHexString(0xff & b);
		                if (hex.length() == 1) {
		                    hexString.append('0');
		                }
		                hexString.append(hex);
		            }

		            token=hexString.toString();
		        } catch (NoSuchAlgorithmException e) {
		            // Manejo de excepciones
		            e.printStackTrace();
		        }
				
				System.out.println("se ha creado el token: " + token);

				
	            
	            
	            
			}
		}
		
		
		
		

		System.out.println(response);
		return ResponseEntity.ok(response);
	}
}
