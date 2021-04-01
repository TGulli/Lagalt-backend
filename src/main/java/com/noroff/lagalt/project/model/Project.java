package com.noroff.lagalt.project.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.usertags.model.UserTag;

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
@Table(name = "Projects", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
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

    @Column(name = "category")
    private String category;


    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProjectTag> projectTags;

    //GITHUB REPO/EXTRA DETAILS

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<ProjectCollaborators> collaborators;

    @JsonGetter("collaborators")
    public List<ReturnCollaborator> getCollaboratorId(){
        if(collaborators != null) {
            return collaborators.stream().map(temp -> new ReturnCollaborator(temp.getId(), temp.getUser().getId(), temp.getProject().getId(), temp.getStatus(), temp.getMotivation(), temp.getUser().getName(), temp.getProject().getName())).collect(Collectors.toList());

        }
        return null;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Message> messages;

    @JsonGetter("messages")
    public List<ReturnMessage> getMessagesId(){
        if(messages != null) {
            return messages.stream().map(temp -> new ReturnMessage(temp.getId())).collect(Collectors.toList());
        }
        return null;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<ChatMessage> chatMessages;

    public Project(){}

    public Project(Long id, String name, String description, Progress progress, String image, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.image = image;
        this.category = category;
    }

    @JsonGetter("owner")
    public ReturnUser getOwnerName(){
        if(owner != null) {
            return  new ReturnUser(owner.getId(), owner.getUsername());
        }
        return null;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setProjectTags(List<ProjectTag> projectTags) {
        this.projectTags = projectTags;
    }

    public List<ProjectTag> getProjectTags() {
        return projectTags;
    }

    public List<ProjectCollaborators> getCollaborators() {
        return collaborators;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
    class ReturnCollaborator {;
        private Long id;
        private Long user;
        private Long project;
        private Status status;
        private String motivation;
        private String userName;
        private String projectName;




        public ReturnCollaborator(Long id, Long user, Long project, Status status, String motivation, String userName, String projectName) {
            this.id = id;
            this.user = user;
            this.project = project;
            this.status = status;
            this.motivation = motivation;
            this.projectName = projectName;
            this.userName = userName;




        }

    }


    public void setCollaborators(List<ProjectCollaborators> collaborators) {
        this.collaborators = collaborators;
    }
}
