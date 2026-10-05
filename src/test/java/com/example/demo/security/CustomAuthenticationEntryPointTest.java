package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import com.example.demo.constant.ExceptionMessages;
import com.example.demo.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private PrintWriter writer;
	@Mock
	private AuthenticationException authenticationException;
	@InjectMocks
	private CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Test
	void commence_ShouldReturnUnauthorizedResponse() throws IOException {

		when(response.getWriter()).thenReturn(writer);
		authenticationEntryPoint.commence(request, response, authenticationException);
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType("application/json");
		verify(response).getWriter();
		verify(objectMapper).writeValue(eq(writer), org.mockito.ArgumentMatchers.any(ErrorResponse.class));
	}

	@Test
	void commence_ShouldCreateCorrectErrorResponse() throws IOException {
		when(response.getWriter()).thenReturn(writer);
		ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
		authenticationEntryPoint.commence(request, response, authenticationException);
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType("application/json");
		verify(objectMapper).writeValue(eq(writer), captor.capture());
		ErrorResponse errorResponse = captor.getValue();
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, errorResponse.getStatus());
		assertEquals(ExceptionMessages.UNAUTHORIZED, errorResponse.getMessage());
		assertEquals(java.time.LocalDateTime.class, errorResponse.getTimestamp().getClass());
	}
}