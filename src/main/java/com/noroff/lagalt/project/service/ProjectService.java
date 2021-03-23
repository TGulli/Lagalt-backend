package com.noroff.lagalt.project.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Project> create (Project project){
        Project createdProject = projectRepository.save(project);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(createdProject, status);
    }

    public ResponseEntity<List<Project>> getAll (){
        List<Project> projects = projectRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(projects, status);
    }

    public ResponseEntity<Project> getById(long id) throws NoItemFoundException {
        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No project by id: " + id));
        return ResponseEntity.ok(fetchedProject);
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        Pageable p = PageRequest.of(page, 3);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }


    public ResponseEntity<Project> editProject(long id, Project project, Long userId) throws NoItemFoundException{
        HttpStatus status;

        if(id != project.getId()){
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(null, status);
        }

        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No Project by id: " + id));
        List<User> owners = existingProject.getOwners();
        for (User owner : owners){
            System.out.println("Userid: " + userId );
            System.out.println("Ownerid: " + owner.getId());
            if (owner.getId() == userId){
                Project savedProject = projectRepository.save(project);
                status = HttpStatus.OK;
                return new ResponseEntity<>(savedProject, status);
            }
        }
        status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(null, status);
    }

    public HttpStatus deleteProject(long id) throws  NoItemFoundException{
        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No project by id: " + id));

        List<User> users = userRepository.findAll();
        for (User u: users) {
            u.getOwnedProjects().remove(fetchedProject);
        }
        projectRepository.delete(fetchedProject);
        HttpStatus status = HttpStatus.OK;
        return status;
    }

}
