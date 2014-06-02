package br.com.presba.livros_ti.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
    private Long bookID;
    private String title;
    private String subTitle;
    private String description;
    private String image;
    private String isbn;
    private String author;
    private String year;
    private String page;
    private String publisher;
    private String download;

    public Book(JSONObject obj) {

        try {
            this.bookID = Long.parseLong(obj.getString("ID"));
            this.title = obj.getString("Title");
            this.subTitle = obj.getString("SubTitle");
            this.description = obj.getString("Description");
            this.image = obj.getString("Image");

            if (obj.has("isbn")) {
                this.isbn = obj.getString("isbn");
            }

            if (obj.has("ISBN")) {
                this.isbn = obj.getString("ISBN");
            }

            if (obj.has("Author")) {
                this.author = obj.getString("Author");
            }

            if (obj.has("Year")) {
                this.year = obj.getString("Year");
            }

            if (obj.has("Page")) {
                this.page = obj.getString("Page");
            }

            if (obj.has("Publisher")) {
                this.publisher = obj.getString("Publisher");
            }

            if (obj.has("Download")) {
                this.download = obj.getString("Download");
            }
        } catch (JSONException ex) {

        }

    }

    public Book(Long bookID, String title, String subTitle, String description, String image, String isbn) {
        this.bookID = bookID;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.image = image;
        this.isbn = isbn;
    }

    public Long getBookID() {

        return bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
