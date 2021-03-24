package com.noroff.lagalt.project.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

enum Progress{
    FOUNDING,
    IN_PROGRESS,
    STALLED,
    COMPLETED
}

@Entity
@Table(name = "Projects")
public class Project {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "progress")
    private Progress progress;

    @Column(name = "image")
    private String image;
    
    @ManyToMany()
    @JoinTable(
            name = "project_users",
            joinColumns = {@JoinColumn(name = "projects_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")}
    )
    private List<User> owners;

    @OneToMany(mappedBy = "project")
    List<ProjectCollaborators> collaborators;

    @JsonGetter("collaborators")
    public List<ReturnCollaborator> getCollaboratorId(){
        if(collaborators != null) {
            return collaborators.stream().map(temp -> new ReturnCollaborator(temp.getId())).collect(Collectors.toList());
        }
        return null;
    }

    @OneToMany(mappedBy = "project")
    List<Message> messages;

    @JsonGetter("messages")
    public List<ReturnMessage> getMessagesId(){
        if(messages != null) {
            return messages.stream().map(temp -> new ReturnMessage(temp.getId())).collect(Collectors.toList());
        }
        return null;
    }

    @OneToMany(mappedBy = "project")
    List<ChatMessage> chatMessages;
    
    public Project(){}

    public Project(Long id, String name, String description, Progress progress, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.image = image;
    }

    /*
    public List<String> getOwnerNames(){
        if(owners != null) {
            return owners.stream()
                    .map(User::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

     */

    @JsonGetter("owners")
    public List<ReturnUser> getOwnerNames(){
        if(owners != null) {
            return owners.stream().map(temp -> new ReturnUser(temp.getId(), temp.getName())).collect(Collectors.toList());
        }
        return null;
    }

    public List<ProjectCollaborators> getCollaborators() {
        return collaborators;
    }

    public List<User> getOwners() {
        return owners;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnUser {
        private Long id;
        private String name;

        public ReturnUser(Long id, String name) {
            this.id = id;
            this.name = name;
        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnMessage {
        private Long id;


        public ReturnMessage(Long id) {
            this.id = id;

        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnCollaborator {
        private Long id;


        public ReturnCollaborator(Long id) {
            this.id = id;

        }

    }

    public void setOwners(List<User> owners) {
        this.owners = owners;
    }

    public void setCollaborators(List<ProjectCollaborators> collaborators) {
        this.collaborators = collaborators;
    }
}
