package com.noroff.lagalt.chat.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.project.model.Project;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Entity
@Table(name = "chat_message")
public class ChatMessage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private String sender;

    @Column
    @JsonFormat(pattern="dd.MM.yyyy HH:mm") private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

    @Column
    private ChatMessageType type;

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonGetter("project")
    public ChatMessage.ReturnProject project(){
        if(project != null) {
            return  new ChatMessage.ReturnProject(project.getId());
        }
        return null;
    }



    public ChatMessage(Long id, String content, String sender, String timestamp, ChatMessageType type) {
        this.id = id;
        this.content = content;
        this.sender = sender ;
        this.type = type;

    }

    public ChatMessage(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setType(ChatMessageType type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnProject {
        private Long id;


        public ReturnProject(Long id) {
            this.id = id;

        }

    }
}
