package com.example.demo.security;	

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.example.demo.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private PrintWriter writer;
	@InjectMocks
	private CustomAccessDeniedHandler accessDeniedHandler;

	@Test
	void handle_ShouldReturnForbiddenResponse() throws IOException {
		AccessDeniedException exception = new AccessDeniedException("Access denied");
		when(response.getWriter()).thenReturn(writer);
		accessDeniedHandler.handle(request, response, exception);
		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response).setContentType("application/json");
		verify(response).getWriter();
		verify(objectMapper).writeValue(eq(writer), any(ErrorResponse.class));
	}
}