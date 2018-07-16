package com.example.ding.umutos.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SearchView;
import com.example.ding.umutos.R;
import com.example.ding.umutos.business.AccessAccounts;
import com.example.ding.umutos.business.AccessBooks;
import com.example.ding.umutos.objects.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ListView bookList;
    private int bookID;
    private String bookTitle,category;
    private AccessBooks accessBookList;
    private AccessAccounts accessAccounts;
    private List<Book> newBookList;
    private TextView infoBar;
    private int userType, userID;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userType = getIntent().getIntExtra("userType",-1);
        userID = getIntent().getIntExtra("userID",-1);


        if (userType==0){
            setContentView(R.layout.activity_seller_booklist);
            bookList=(ListView)findViewById(R.id.sellerBookList);

            accessBookList=new AccessBooks();
            accessAccounts=new AccessAccounts();
            newBookList=accessBookList.getUserBooks(userID);

            infoBar=(TextView)findViewById(R.id.sellListInfoBar);
            infoBar.setText("Hi "+accessAccounts.getAccountByID(userID).getUserName()+".");

            loadBookList(newBookList);
        }
        else {
            setContentView(R.layout.activity_customer_booklist);
            bookList=(ListView)findViewById(R.id.cusListView);

            accessBookList=new AccessBooks();
            newBookList=accessBookList.getBooks();
            loadBookList(newBookList);
            Book aBook=new Book(  );

            Spinner searchByCategory;
            searchByCategory=(Spinner) findViewById(R.id.searchByCategory);
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,aBook.getCategoryArr());
            searchByCategory.setAdapter(adapter);
            searchByCategory.setOnItemSelectedListener(new SpinnerSelectedListener());
            SearchView sv=(SearchView)findViewById(R.id.searchByKeyword);
            sv.setIconifiedByDefault(false);
            sv.setOnQueryTextListener(this);
            sv.setSubmitButtonEnabled(true);
            sv.setQueryHint("Search book title, author or category here");
        }
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            Book aBook=new Book(  );
            category=aBook.getCategoryArr()[arg2];
            newBookList=accessBookList.CategoryList(category);
            loadBookList(newBookList);
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        if(TextUtils.isEmpty(newText)) {
            bookList.clearTextFilter();
            newBookList=accessBookList.getBooks();
            loadBookList( newBookList );
        }
        else {
            bookList.setFilterText(newText);
            newBookList=accessBookList.searchBooksByKeyWord( newText );
            loadBookList( newBookList );
        }
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        newBookList=accessBookList.searchBooksByKeyWord( query );
        loadBookList( newBookList );
        return true;
    }

    public void loadBookList( List<Book> newBookList ){
        int size=newBookList.size();
        Book aBook=new Book();

        ArrayList<HashMap<String, Object>> books = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <size; i++) {
            HashMap<String, Object> book = new HashMap<String, Object>();
            book.put("id",""+newBookList.get(i).getBookID());
            book.put("img",aBook.getImageByBookID(newBookList.get(i).getBookID()) );
            book.put("title", newBookList.get(i).getName());
            book.put("price","$"+newBookList.get(i).getPrice());
            books.add(book);
        }
        SimpleAdapter sItems = new SimpleAdapter(this,
                books,
                R.layout.activity_book_row,
                new String[] {"img", "title", "price","id" },
                new int[] { R.id.cusBookListImg, R.id.cusBookListTitle, R.id.cusBookListPrice , R.id.cusBookListID});
        bookList.setAdapter(sItems);

        bookList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                HashMap<String,String> map=(HashMap<String,String>)bookList.getItemAtPosition(arg2);
                String id=map.get("id");
                bookID=Integer.parseInt(id);
                if (userType==0) {
                    bookTitle=map.get("title");
                    infoBar.setText("You selected book: "+bookTitle);
                }
                else{
                    Intent intent = new Intent(BookListActivity.this,SingleBookActivity.class);
                    intent.putExtra("bookID", bookID);
                    intent.putExtra("userID", userID);
                    BookListActivity.this.startActivity(intent);
                }
            }
        });
    }

    public void buttonAddNewBook(View view) {
        openEditBookActivity();
    }

    public void buttonEditPostedBook(View view) {
        if (bookID<1)
            showEditDialog();
        else
            openEditBookActivity(bookID);
    }

    public void buttonDeletePostedBook(View view) {
        if (bookID<1)
            showDeleteDialog();
        else
            showDeleteDialog(bookTitle);
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert:")
                .setMessage("\n"+"Please select a book to delete.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {}
                })
                .show();
    }

    private void showDeleteDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation:")
                .setMessage("\n"+"Sure to delete "+msg+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        accessBookList.deleteBook(bookID);
                        Intent intent = new Intent(BookListActivity.this, BookListActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("userType", userType);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {}
                })
                .show();
    }

    private void showEditDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert:")
                .setMessage("\n"+"Please select a book to edit.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {}
                })
                .show();
    }

    private void openEditBookActivity(){
        Intent intent = new Intent(BookListActivity.this,EditBookActivity.class);
        intent.putExtra("userID", userID);
        BookListActivity.this.startActivity(intent);
    }

    private void openEditBookActivity(int bookID){
        Intent intent = new Intent(BookListActivity.this,EditBookActivity.class);
        intent.putExtra("bookID", bookID);
        intent.putExtra("userID", userID);
        BookListActivity.this.startActivity(intent);
    }


    public void buttonOpenHistory(View view){
        Intent intent = new Intent(BookListActivity.this,HistoryActivity.class);
        intent.putExtra("userType", userType);
        intent.putExtra("userID", userID);
        BookListActivity.this.startActivity(intent);
    }

    public void buttonPriceHighToLow(View view){
        List<Book> aList=accessBookList.declineSort(newBookList);
        loadBookList(aList);
    }

    public void buttonPriceLowToHigh(View view){
        List<Book> aList=accessBookList.ascentSort(newBookList);
        loadBookList(aList);
    }

    public void btnCusBackToMain(View view) {
        Intent intent = new Intent(BookListActivity.this,HomeActivity.class);
        intent.putExtra("userID", userID);
        BookListActivity.this.startActivity(intent);
    }
}