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
	
	String token_inicial="token_inicial";
	String token_confirmacion="";

	@CrossOrigin()
	@PostMapping("/login")
	public ResponseEntity<String> ejemplo(@RequestBody String mensaje) {

		System.out.println("Mensaje recibido desde el cliente: " + mensaje);

		JSONObject jsonObject = new JSONObject(mensaje);

		String page_request = jsonObject.getString("page_request");
		String user_token= jsonObject.getString("user_token_db");
		

		
		
		
		
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
				String id_login=id.toString();
				
				
				
				//el cliente envia el parametro user_token con valor inicio al inicio
				if (user_token.equals("inicio")) {
					
			
				if (password.equals(user_password_login)) {
					
					
					try {
			            MessageDigest digest = MessageDigest.getInstance("SHA-256");
			            byte[] encodedHash = digest.digest(id_login.getBytes(StandardCharsets.UTF_8));

			            // hash to hex
			            StringBuilder hexString = new StringBuilder();
			            for (byte b : encodedHash) {
			                String hex = Integer.toHexString(0xff & b);
			                if (hex.length() == 1) {
			                    hexString.append('0');
			                }
			                hexString.append(hex);
			            }

			            token_inicial=hexString.toString();
			        } catch (NoSuchAlgorithmException e) {
			            // Excepciones
			            e.printStackTrace();
			        }
					
					System.out.println("se ha creado el token: " + token_inicial);
					json.put("id", id);
					//este token se envia al cliente y el cliente lo guarda en la cache
					json.put("token", token_inicial);
					json.put("response", "correct password");
					response = json.toString();
					
					
				} else {
					json.put("response", "incorrect password");
					response = json.toString();
				}
				
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
	            
			}
		}
		
		
		if (page_request.equals("editar")) {
			String username_editar = jsonObject.getString("username_db");
			String user_password_editar = jsonObject.getString("user_password_db");
			String id_editar = jsonObject.getString("id_db");

			
			
			try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedHash = digest.digest(id_editar.getBytes(StandardCharsets.UTF_8));

	            // hash to hex
	            StringBuilder hexString = new StringBuilder();
	            for (byte b : encodedHash) {
	                String hex = Integer.toHexString(0xff & b);
	                if (hex.length() == 1) {
	                    hexString.append('0');
	                }
	                hexString.append(hex);
	            }

	            token_confirmacion=hexString.toString();
	        } catch (NoSuchAlgorithmException e) {
	            // Excepciones
	            e.printStackTrace();
	        }
			
			if (token_confirmacion.equals(user_token))
			{
				String sql_editar = "UPDATE Users SET username=?,user_password=? WHERE users_id=?";
				jdbcTemplate.update(sql_editar, username_editar,user_password_editar, id_editar);

				json.put("response", "cambio exitoso");
				response = json.toString();
				System.out.println("cambio exitoso");
				
			}
			
		}
		
		if (page_request.equals("mostrar")) {
		    String id_mostrar = jsonObject.getString("id_db");
		    
		    try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedHash = digest.digest(id_mostrar.getBytes(StandardCharsets.UTF_8));

	            // hash to hex
	            StringBuilder hexString = new StringBuilder();
	            for (byte b : encodedHash) {
	                String hex = Integer.toHexString(0xff & b);
	                if (hex.length() == 1) {
	                    hexString.append('0');
	                }
	                hexString.append(hex);
	            }

	            token_confirmacion=hexString.toString();
	        } catch (NoSuchAlgorithmException e) {
	            // Excepciones
	            e.printStackTrace();
	        }
			
		    System.out.println("el token de confirmacion es: "+token_confirmacion);
		    System.out.println("el token recibido del cliente es: "+user_token);
			if (token_confirmacion.equals(user_token))
			{
				final String QUERY="SELECT * FROM USERS;";
				List<Map<String,Object>> results=jdbcTemplate.queryForList(QUERY);
				
				for (int i=0;i<results.size();i++)
				{
					System.out.println(results.get(i).get("users_id"));
					if (id_mostrar.equals(results.get(i).get("users_id").toString()))
					{					
						json.put("username", results.get(i).get("username") );
						json.put("email", results.get(i).get("email") );
						json.put("user_password", results.get(i).get("user_password") );
						json.put("phone", results.get(i).get("phone") );
						json.put("address", results.get(i).get("adrress") );
						json.put("response", "muestra exitosa");
						response = json.toString();
					}
				}
				
			}
			
			
			
		}
		
		
		
		
		
		

		System.out.println(response);
		return ResponseEntity.ok(response);
	}
}
