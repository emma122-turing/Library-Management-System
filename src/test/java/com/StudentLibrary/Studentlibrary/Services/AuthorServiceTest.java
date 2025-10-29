package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Author;
import com.StudentLibrary.Studentlibrary.Repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void createAuthor_callsRepositorySave() {
        Author author = new Author();
        author.setName("John Doe");
        author.setEmail("john@example.com");

        when(authorRepository.save(any(Author.class))).thenReturn(author);

        authorService.createAuthor(author);

        verify(authorRepository).save(eq(author));
    }


    @Test
    void createAuthor_propagatesRepositoryException() {
        Author author = new Author();
        author.setName("Error");
        author.setEmail("error@example.com");

        doThrow(new RuntimeException("db error")).when(authorRepository).save(any(Author.class));

        assertThrows(RuntimeException.class, () -> authorService.createAuthor(author));
    }

    @Test
    void updateAuthor_callsRepositoryUpdateDetails() {
        Author author = new Author();
        author.setId(5);
        author.setName("Updated");
        author.setEmail("updated@example.com");

        when(authorRepository.updateAuthorDetails(any(Author.class))).thenReturn(1);

        authorService.updateAuthor(author);

        verify(authorRepository).updateAuthorDetails(eq(author));
    }

    @Test
    void updateAuthor_propagatesRepositoryException() {
        Author author = new Author();
        author.setId(10);
        author.setName("Break");
        author.setEmail("break@example.com");

        doThrow(new RuntimeException("update failure")).when(authorRepository).updateAuthorDetails(any(Author.class));

        assertThrows(RuntimeException.class, () -> authorService.updateAuthor(author));
    }

    @Test
    void deleteAuthor_callsRepositoryDeleteCustom() {
        int id = 42;
        when(authorRepository.deleteCustom(eq(id))).thenReturn(1);

        authorService.deleteAuthor(id);

        verify(authorRepository).deleteCustom(eq(id));
    }

    @Test
    void getAuthorById_returnsAuthor() {
        Author author = new Author();
        author.setId(1);
        author.setName("Jane Doe");

        when(authorRepository.findById(1)).thenReturn(Optional.of(author));

        Author result = authorService.getAuthorById(1);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(authorRepository).findById(1);
    }

    @Test
    void getAuthorById_returnsNullWhenNotFound() {
        when(authorRepository.findById(99)).thenReturn(Optional.empty());

        Author result = authorService.getAuthorById(99);

        assertNull(result);
        verify(authorRepository).findById(99);
    }

    @Test
    void getAllAuthors_returnsAll() {
        List<Author> authors = List.of(
                new Author("A", "a@example.com", 40, "UK"),
                new Author("B", "b@example.com", 30, "US")
        );
        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> result = authorService.getAllAuthors();

        assertEquals(2, result.size());
        verify(authorRepository).findAll();
    }
}