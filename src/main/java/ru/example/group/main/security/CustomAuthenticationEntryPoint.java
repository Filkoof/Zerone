package ru.example.group.main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class CustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.SC_OK);
        response.setCharacterEncoding("utf-8");
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uri", request.getRequestURI());
        map.put("error_description", "Ошибка авторизации");
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(map);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
