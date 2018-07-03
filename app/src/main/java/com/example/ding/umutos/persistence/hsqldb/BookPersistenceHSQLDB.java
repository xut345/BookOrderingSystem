package com.example.ding.umutos.persistence.hsqldb;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.ding.umutos.objects.Book;
import com.example.ding.umutos.persistence.BookPersistence;

public class BookPersistenceHSQLDB implements BookPersistence {

    private final Connection c;


    public BookPersistenceHSQLDB(final String dbPath) {
        try {
            this.c = DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private Book fromResultSet(final ResultSet rs) throws SQLException {
        int bookID = rs.getInt("bookID");
        String bookName = rs.getString("bookName");
        String authorName = rs.getString("authorName");
        int bookPicture = rs.getInt("bookPicture");
        String bookDescription = rs.getString("bookDescription");
        String bookCategory = rs.getString("bookCategory");
        double price = rs.getDouble("price");
        int ownerID = rs.getInt("ownerID");

        Book book = new Book(bookName, authorName, bookPicture, bookDescription, bookCategory, price, ownerID);
        book.setBookID(bookID);
        return book;
    }

    @Override
    public List<Book> getBookSequential() {
        final List<Book> books = new ArrayList<>();

        try
        {
            final Statement st = c.createStatement();
            final ResultSet rs = st.executeQuery("SELECT * FROM books");
            while (rs.next())
            {
                final Book book = fromResultSet(rs);
                books.add(book);
            }
            rs.close();
            st.close();

            return books;
        }
        catch (final SQLException e)
        {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Book insertBook(Book currentBook) {
        try {
            final PreparedStatement st = c.prepareStatement("INSERT INTO books VALUES(?, ?, ?, ?, ?, ?, ?)");
            st.setString(1, currentBook.getName());
            st.setString(2, currentBook.getAuthor());
            st.setInt(3, currentBook.getPicture());
            st.setString(4, currentBook.getDescription());
            st.setString(5, currentBook.getCategory());
            st.setDouble(6, currentBook.getPrice());
            st.setInt(7, currentBook.getOwner());

            st.executeUpdate();

            return currentBook;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override

    public Book updateBook(Book currentBook) {
        try {
            final PreparedStatement st = c.prepareStatement("UPDATE books SET bookName = ?, authorName = ?, bookPicture = ?, bookDescription = ?, bookCategory = ?, price = ?, ownerID = ? WHERE bookID = ?");
            st.setString(1, currentBook.getName());
            st.setString(2, currentBook.getAuthor());
            st.setInt(3, currentBook.getPicture());
            st.setString(4, currentBook.getDescription());
            st.setString(5, currentBook.getCategory());
            st.setDouble(6, currentBook.getPrice());
            st.setInt(7,currentBook.getOwner());
            st.setInt(8, currentBook.getBookID());

            st.executeUpdate();

            return currentBook;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Book searchBook(int id){
        Book book = null;
        try {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM books WHERE bookID = ?");
            st.setInt(1, id);

            final ResultSet rs = st.executeQuery();

            if(rs.next()) {
                book = fromResultSet(rs);
            }

            rs.close();
            st.close();

            return book;

        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Book> getUserBookSequential(int userID) {
        final List<Book> books = new ArrayList<>();
        try {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM books WHERE ownerID = ?");
            st.setInt(1, userID);

            final ResultSet rs = st.executeQuery();
            while(rs.next()) {
                final Book book = fromResultSet(rs);
                books.add(book);
            }

            rs.close();
            st.close();

            return books;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void deleteBook(int id) {
        try {
            final PreparedStatement st = c.prepareStatement("DELETE FROM books WHERE bookID = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Book> getBookCategorySequential(String category) {
        final List<Book> books = new ArrayList<>();
        try {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM books WHERE bookCategory = ?");
            st.setString(1, category);

            final ResultSet rs = st.executeQuery();
            while(rs.next()) {
                final Book book = fromResultSet(rs);
                books.add(book);
            }

            rs.close();
            st.close();

            return books;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Book> searchKeyword(String keyword){
        final List<Book> books = new ArrayList<>();
        try {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM books WHERE bookName = ? OR authorName = ? OR bookCategory = ?");
            st.setString(1, keyword);
            st.setString(2, keyword);
            st.setString(3, keyword);

            final ResultSet rs = st.executeQuery();
            while(rs.next()) {
                final Book book = fromResultSet(rs);
                books.add(book);
            }

            rs.close();
            st.close();

            return books;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }
}
