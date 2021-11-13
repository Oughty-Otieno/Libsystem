package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Borrowing.
 */
@Entity
@Table(name = "borrowing")
public class Borrowing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_borrowed")
    private LocalDate date_borrowed;

    @Column(name = "due_date")
    private LocalDate due_date;

    @Column(name = "return_date")
    private LocalDate return_date;

    @Column(name = "status")
    private Boolean status;

    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Book book;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Borrowing id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate_borrowed() {
        return this.date_borrowed;
    }

    public Borrowing date_borrowed(LocalDate date_borrowed) {
        this.setDate_borrowed(date_borrowed);
        return this;
    }

    public void setDate_borrowed(LocalDate date_borrowed) {
        this.date_borrowed = date_borrowed;
    }

    public LocalDate getDue_date() {
        return this.due_date;
    }

    public Borrowing due_date(LocalDate due_date) {
        this.setDue_date(due_date);
        return this;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }

    public LocalDate getReturn_date() {
        return this.return_date;
    }

    public Borrowing return_date(LocalDate return_date) {
        this.setReturn_date(return_date);
        return this;
    }

    public void setReturn_date(LocalDate return_date) {
        this.return_date = return_date;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Borrowing status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Borrowing book(Book book) {
        this.setBook(book);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Borrowing user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Borrowing)) {
            return false;
        }
        return id != null && id.equals(((Borrowing) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Borrowing{" +
            "id=" + getId() +
            ", date_borrowed='" + getDate_borrowed() + "'" +
            ", due_date='" + getDue_date() + "'" +
            ", return_date='" + getReturn_date() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
