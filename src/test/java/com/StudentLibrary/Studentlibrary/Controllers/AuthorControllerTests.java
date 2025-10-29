package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Author;
import com.StudentLibrary.Studentlibrary.Services.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createAuthor_success() throws Exception {
        Author payload = new Author();
        payload.setName("Jane Austen");
        payload.setEmail("jane@example.com");
        payload.setAge(41);
        payload.setCountry("UK");

        Mockito.doNothing().when(authorService).createAuthor(any(Author.class));

        mockMvc.perform(post("/createAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Author created"));

        Mockito.verify(authorService).createAuthor(any(Author.class));
    }

    @Test
    void updateAuthor_success() throws Exception {
        Author payload = new Author();
        payload.setId(1);
        payload.setName("Updated Name");
        payload.setEmail("updated@example.com");
        payload.setAge(50);
        payload.setCountry("US");

        Mockito.doNothing().when(authorService).updateAuthor(any(Author.class));

        mockMvc.perform(put("/updateAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Auhtor upadted!!"));

        Mockito.verify(authorService).updateAuthor(any(Author.class));
    }

    @Test
    void deleteAuthor_success() throws Exception {
        int id = 7;
        Mockito.doNothing().when(authorService).deleteAuthor(eq(id));

        mockMvc.perform(delete("/deleteAuthor").param("id", String.valueOf(id)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Author deleted!!"));

        Mockito.verify(authorService).deleteAuthor(eq(id));
    }
}