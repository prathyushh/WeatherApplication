package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.security.CustomAccessDeniedHandler;
import com.example.demo.security.CustomAuthenticationEntryPoint;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.service.JwtService;

import tools.jackson.databind.ObjectMapper;

@SpringJUnitConfig(SecurityConfigTest.TestConfig.class)
@WebAppConfiguration
class SecurityConfigTest {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private SecurityFilterChain securityFilterChain;
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@Test
	void passwordEncoder_shouldBeBCrypt() {
		assertNotNull(passwordEncoder);
		assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
	}

	@Test
	void authenticationManager_shouldBeCreated() {
		assertNotNull(authenticationManager);
	}

	@Test
	void securityFilterChain_shouldBeCreated() {
		assertNotNull(securityFilterChain);
	}

	@Test
	void springSecurityFilterChain_shouldBeCreated() {
		assertNotNull(springSecurityFilterChain);
	}

	@Test
	void authRegister_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/auth/register")).andExpect(status().isOk());
	}

	@Test
	void authLogin_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
	}

	@Test
	void authToken_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/auth/token")).andExpect(status().isOk());
	}

	@Test
	void authRefresh_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/auth/refresh")).andExpect(status().isOk());
	}

	@Test
	void swaggerUi_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
	}

	@Test
	void swaggerUiHtml_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui.html")).andExpect(status().isOk());
	}

	@Test
	void apiDocs_shouldBePermittedWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/v3/api-docs/test")).andExpect(status().isOk());
	}

	/*
	 * @Test void getCities_shouldAllowUser() throws Exception {
	 * mockMvc.perform(get("/api/cities").with(user("user").roles("USER"))).
	 * andExpect(status().isOk()); }
	 * 
	 * @Test void getCities_shouldAllowAdmin() throws Exception {
	 * mockMvc.perform(get("/api/cities").with(user("admin").roles("ADMIN"))).
	 * andExpect(status().isOk()); }
	 */
	@ParameterizedTest
	@CsvSource({ "USER, 200", "ADMIN, 200" })
	void getCities_shouldCheckRoleAccess(String role, int expectedStatus) throws Exception {
		mockMvc.perform(get("/api/cities").with(user("test").roles(role))).andExpect(status().is(expectedStatus));
	}

	@Test
	void getCities_shouldRejectUnauthenticatedUser() throws Exception {
		mockMvc.perform(get("/api/cities")).andExpect(status().isUnauthorized());
	}

//	@Test
//	void getWeather_shouldAllowUser() throws Exception {
//		mockMvc.perform(get("/api/weather").with(user("user").roles("USER"))).andExpect(status().isOk());
//	}
//
//	@Test
//	void getWeather_shouldAllowAdmin() throws Exception {
//		mockMvc.perform(get("/api/weather").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
//	}
	@ParameterizedTest
	@CsvSource({ "USER, 200", "ADMIN, 200" })
	void getWeather_shouldCheckRoleAccess(String role, int expectedStatus) throws Exception {
		mockMvc.perform(get("/api/weather").with(user("test").roles(role))).andExpect(status().is(expectedStatus));
	}

	@Test
	void getWeather_shouldRejectUnauthenticatedUser() throws Exception {
		mockMvc.perform(get("/api/weather")).andExpect(status().isUnauthorized());
	}

//	@Test
//	void postCities_shouldAllowAdmin() throws Exception {
//		mockMvc.perform(post("/api/cities").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
//	}
//
//	@Test
//	void postCities_shouldRejectUser() throws Exception {
//		mockMvc.perform(post("/api/cities").with(user("user").roles("USER"))).andExpect(status().isForbidden());
//	}
	@ParameterizedTest
	@CsvSource({ "USER, 403", "ADMIN, 200" })
	void postCities_shouldCheckRoleAccess(String role, int expectedStatus) throws Exception {
		mockMvc.perform(post("/api/cities").with(user("test").roles(role))).andExpect(status().is(expectedStatus));
	}

	@Test
	void postCities_shouldRejectUnauthenticatedUser() throws Exception {
		mockMvc.perform(post("/api/cities")).andExpect(status().isUnauthorized());
	}

//	@Test
//	void deleteCity_shouldAllowAdmin() throws Exception {
//		mockMvc.perform(delete("/api/cities/1").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
//	}
//
//	@Test
//	void deleteCity_shouldRejectUser() throws Exception {
//		mockMvc.perform(delete("/api/cities/1").with(user("user").roles("USER"))).andExpect(status().isForbidden());
//	}
	@ParameterizedTest
	@CsvSource({ "USER, 403", "ADMIN, 200" })
	void deleteCity_shouldCheckRoleAccess(String role, int expectedStatus) throws Exception {
		mockMvc.perform(delete("/api/cities/1").with(user("test").roles(role))).andExpect(status().is(expectedStatus));
	}

	@Test
	void deleteCity_shouldRejectUnauthenticatedUser() throws Exception {
		mockMvc.perform(delete("/api/cities/1")).andExpect(status().isUnauthorized());
	}

	@Test
	void unknownEndpoint_shouldRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/unknown")).andExpect(status().isUnauthorized());
	}

	@Test
	void unknownEndpoint_shouldAllowAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/api/unknown").with(user("user").roles("USER"))).andExpect(status().isNotFound());
	}

	@Test
	void postCities_shouldNotFailBecauseOfCsrf() throws Exception {
		mockMvc.perform(post("/api/cities").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
	}

	@TestConfiguration
	@EnableWebSecurity
	@Import({ SecurityConfig.class, TestController.class })
	static class TestConfig {

		@Bean
		JwtService jwtService() {
			return org.mockito.Mockito.mock(JwtService.class);
		}

		@Bean
		UserDetailsService userDetailsService() {
			return username -> User.withUsername(username).password("{noop}password").roles("USER").build();
		}

		@Bean
		JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
			return new JwtAuthenticationFilter(jwtService, userDetailsService);
		}

		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}

		@Bean
		CustomAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
			return new CustomAuthenticationEntryPoint(objectMapper);
		}

		@Bean
		CustomAccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
			return new CustomAccessDeniedHandler(objectMapper);
		}
	}

	@RestController
	static class TestController {

		@GetMapping("/auth/register")
		String register() {
			return "register";
		}

		@GetMapping("/auth/login")
		String login() {
			return "login";
		}

		@GetMapping("/auth/token")
		String token() {
			return "token";
		}

		@GetMapping("/auth/refresh")
		String refresh() {
			return "refresh";
		}

		@GetMapping("/swagger-ui/index.html")
		String swaggerUi() {
			return "swagger-ui";
		}

		@GetMapping("/swagger-ui.html")
		String swaggerUiHtml() {
			return "swagger-ui";
		}

		@GetMapping("/v3/api-docs/test")
		String apiDocs() {
			return "api-docs";
		}

		@GetMapping("/api/cities")
		String getCities() {
			return "cities";
		}

		@PostMapping("/api/cities")
		String postCities() {
			return "city created";
		}

		@DeleteMapping("/api/cities/{id}")
		String deleteCity() {
			return "city deleted";
		}

		@GetMapping("/api/weather")
		String getWeather() {
			return "weather";
		}
	}
}