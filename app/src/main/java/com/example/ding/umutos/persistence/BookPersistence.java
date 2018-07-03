package com.example.ding.umutos.persistence;

import com.example.ding.umutos.objects.Book;
import java.util.List;

public interface  BookPersistence {
  
    List<Book> getBookSequential();

    Book insertBook(Book currentBook);

    Book updateBook(Book currentBook);
    
    Book searchBook(int id);
    
    List<Book> getUserBookSequential(int userID);
    
    void deleteBook(int id);

    List<Book> getCategoryList(String category);

   
}