package ru.nekki.test.dao;


import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "ENTITY")
public class Entity {
    private String content;
    private Date creationDate;
    private int id;

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
