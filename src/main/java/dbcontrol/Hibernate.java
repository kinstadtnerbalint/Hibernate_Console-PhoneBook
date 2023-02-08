package dbcontrol;

import dbcontrol.tables.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.util.Date;
import java.util.List;

public class Hibernate
{
    SessionFactory sessionFactory;

    public Hibernate()
    {
        Configuration config = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Author.class)
                .addAnnotatedClass(Store.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(config.getProperties())
                .build();
        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }

    public List<Author> searchAuthor(String name, Integer id)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Author> query = null;

        if (name != null)
        {
            query = session.createNamedQuery("searchByAuthorName", Author.class);
            query.setParameter("name", name);
        } else if (id != 0)
        {
            query = session.createNamedQuery("searchByAuthorId", Author.class)
                    .setParameter("id", id);
        }

        if (query != null)
        {
            return query.getResultList();
        }
        session.close();
        return null;
    }

    public void modifyAuthor(Author author, List<Book> books, String newName, Date newDob, boolean delete)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Author> query = null;

        if (author != null && books != null)             // Add book
        {
            session.createNamedQuery("updateBooks", Author.class)
                    .setParameter("id", author.getId())
                    .setParameter("books", books.toString())
                    .executeUpdate();
        } else if (author != null && newName != null)       // Change name
        {
            session.createNamedQuery("updateAuthorName", Author.class)
                    .setParameter("id", author.getId())
                    .setParameter("name", newName)
                    .executeUpdate();
        } else if (author != null && newDob != null)
        {
            session.createNamedQuery("updateDob", Author.class)
                    .setParameter("id", author.getId())
                    .setParameter("dob", newDob)
                    .executeUpdate();
        } else if (author != null && delete)
        {
            session.createNamedQuery("deleteAuthor", Author.class)
                    .setParameter("id", author.getId())
                    .executeUpdate();

        }
        session.getTransaction().commit();
        session.close();
    }


    public Author authorAdd(String name, Date dateOfBirth)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Author> query = null;
        session.close();
        return new Author(name, dateOfBirth);
    }


    public List<Book> searchBook(String isbn, String title, String authorName, Integer id)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Book> query = null;

        if (isbn != null)
        {
            query = session.createNamedQuery("searchByIsbn", Book.class);
            query.setParameter("isbn", isbn);
        } else if (title != null)
        {
            query = session.createNamedQuery("searchByTitle", Book.class);
            query.setParameter("title", title);
        } else if (authorName != null)
        {
            query = session.createNamedQuery("searchByAuthor", Book.class)
                    .setParameter("author", authorName)
                    .setParameter("id", id);
        } else if (id != null)
        {
            query = session.createNamedQuery("searchByBookId", Book.class)
                    .setParameter("id", id);
        }
        if (query != null)
        {
            return query.getResultList();
        }
        session.close();
        return null;
    }

    public Book addBook(Author author, String isbn, String title, Date publishDate)
    {
        return new Book(author, isbn, title, publishDate);
    }

    public void modifyBook(int id, Author author, String isbn, String title, Date publishDate)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        if (id < 0)
        {
            session.close();
            return;
        }

        if (author != null)
        {
            session.createNamedQuery("updateAuthor", Book.class)
                    .setParameter("newAuthor", author)
                    .setParameter("id", id)
                    .executeUpdate();
        } else if (isbn != null)
        {
            session.createNamedQuery("updateISBN", Book.class)
                    .setParameter("newIsbn", isbn)
                    .setParameter("id", id)
                    .executeUpdate();
        } else if (title != null)
        {
            session.createNamedQuery("updateTitle", Book.class)
                    .setParameter("newTitle", title)
                    .setParameter("id", id)
                    .executeUpdate();
        } else if (publishDate != null)
        {
            session.createNamedQuery("updateDOP", Book.class)
                    .setParameter("newDOP", publishDate)
                    .setParameter("id", id)
                    .executeUpdate();
        }
        session.getTransaction().commit();
        session.close();
    }

    public void changeBookMarket(Book book, boolean inMarket)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNamedQuery("updateMarket", Book.class)
                .setParameter("id", book.getId())
                .setParameter("inMarket", inMarket)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void commit(Object commit)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(commit);
        session.getTransaction().commit();
        session.close();
    }

}