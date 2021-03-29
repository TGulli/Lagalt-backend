package com.noroff.lagalt.userhistory.model;


import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;

import javax.persistence.*;



enum ActionType {
    SEEN,
    CLICKED,
    APPLIED,
    COLLABORATED
}

//tableId, userId, ActionType (enum), projectId, timeStamp

@Entity
@Table(name = "UserHistory")
public class UserHistory {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private ActionType actionType;

    @Column
    private Long project_id;

    @Column
    private String timestamp;

    public UserHistory() {
    }

    public UserHistory(Long id, User user, ActionType actionType, Long project_id, String timestamp) {
        this.id = id;
        this.user = user;
        this.actionType = actionType;
        this.project_id = project_id;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
