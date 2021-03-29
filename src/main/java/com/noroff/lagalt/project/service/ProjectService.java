package com.noroff.lagalt.project.service;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectTagRepository projectTagRepository;

    public ResponseEntity<Project> create (Project project){
        if (project == null || project.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project or project.name or project.owner is null.");
        }
        Optional<Project> oldProject = projectRepository.findByName(project.getName());
        if (oldProject.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project already exist with the given name.");
        }
        Project createdProject = projectRepository.save(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Project>> getAll (){
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Project> getById(Long id) {
        Optional<Project> fetchedProject = projectRepository.findById(id);
        if (fetchedProject.isPresent()){
            return ResponseEntity.ok(fetchedProject.get());
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project found with id: " + id);
        }
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        Pageable p = PageRequest.of(page, 5);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }


    public ResponseEntity<Project> editProject(Long id, Project project, Long userId){

        if ( id == null || userId == null ||project == null || project.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not edit project with " + id + ", because project, project.name or user is null");
        }

        HttpStatus status;

        if(!id.equals(project.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You're trying to edit the wrong project");
        }



        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No project found by that id"));

        if ((!project.getName().equals(existingProject.getName())) &&
                projectRepository.existsByName(project.getName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with the new name already exists in the database.");
        }


        List<User> owners = existingProject.getOwners();
        for (User owner : owners){
            System.out.println("Userid: " + userId );
            System.out.println("Ownerid: " + owner.getId());

            if (owner.getId() == userId){
                //Create the tags!
                if (project.getProjectTags() != null){
                    for (ProjectTag tag: project.getProjectTags()) {
                        if (!existingProject.getProjectTags().contains(tag)) {
                            tag.setProject(existingProject);
                            projectTagRepository.save(tag);
                        }
                    }
                }
                Project savedProject = projectRepository.save(project);
                status = HttpStatus.OK;
                return new ResponseEntity<>(savedProject, status);
            }
        }
        status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(null, status);
    }

    public HttpStatus deleteProject(long id){
        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found"));

        List<User> users = userRepository.findAll();
        for (User u: users) {
            u.getOwnedProjects().remove(fetchedProject);
        }
        projectRepository.delete(fetchedProject);
        HttpStatus status = HttpStatus.OK;
        return status;
    }


    // Trengs kanskje?
    public ResponseEntity<List<Project>> getAllFromCategory(String category) {
        List<Project> projects = projectRepository.findAll()
                .stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
}
