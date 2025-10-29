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

    public List<Author> searchAuthors(String name, String country) {
        if (name != null && country != null) {
            return authorRepository.findByNameContainingIgnoreCaseAndCountryIgnoreCase(name, country);
        } else if (name != null) {
            return authorRepository.findByNameContainingIgnoreCase(name);
        } else if (country != null) {
            return authorRepository.findByCountryIgnoreCase(country);
        }
        return authorRepository.findAll();
    }
}
