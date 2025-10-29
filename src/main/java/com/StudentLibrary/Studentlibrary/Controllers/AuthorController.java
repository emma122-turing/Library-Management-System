package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Author;
import com.StudentLibrary.Studentlibrary.Services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @PostMapping("/createAuthor")
    public ResponseEntity createAuthor(@RequestBody Author author){
        authorService.createAuthor(author);
        return new ResponseEntity("Author created", HttpStatus.CREATED);
    }
    @PutMapping("/updateAuthor")
    public ResponseEntity updateAuthor(@RequestBody Author author){
        authorService.updateAuthor(author);
        return new ResponseEntity("Auhtor upadted!!",HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/deleteAuthor")
    public ResponseEntity deleteAuthor(@RequestParam("id") int id){
        authorService.deleteAuthor(id);
        return new ResponseEntity("Author deleted!!",HttpStatus.ACCEPTED);

    }

    @GetMapping("/getAuthorById")
    public ResponseEntity<Author> getAuthorById(@RequestParam("id") int id) {
        Author author = authorService.getAuthorById(id);
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @GetMapping("/getAllAuthors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/searchAuthors")
    public ResponseEntity<List<Author>> searchAuthors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {

        List<Author> authors = authorService.searchAuthors(name, country);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

}
