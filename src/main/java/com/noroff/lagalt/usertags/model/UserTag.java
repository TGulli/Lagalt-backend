package com.noroff.lagalt.usertags.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.user.model.User;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.*;

/**
 * Model class UserTag for database table
 */

@Entity
@Table(name = "UserTag")
public class UserTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String tag;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public UserTag() {

    }

    public UserTag(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    @JsonGetter("user")
    public String user() {
        return user != null ? user.getName() : null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
