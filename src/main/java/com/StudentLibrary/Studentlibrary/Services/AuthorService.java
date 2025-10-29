package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Author;
import com.StudentLibrary.Studentlibrary.Repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    @Autowired
    AuthorRepository authorRepository;


    public void createAuthor(Author author){
        authorRepository.save(author);

    }
    public void updateAuthor(Author author){
        authorRepository.updateAuthorDetails(author);
    }
    public void deleteAuthor(int id ){
        authorRepository.deleteCustom(id);
    }

    public Author getAuthorById(int id) {
        return authorRepository.findById(id).orElse(null);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
}
