package com.noroff.lagalt.project.service;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
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

    public ResponseEntity<Project> getById(long id) {

        Optional<Project> fetchedProject = projectRepository.findById(id);
        if (fetchedProject.isPresent()){
            return ResponseEntity.ok(fetchedProject.get());
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project found with id: " + id);
        }
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        Pageable p = PageRequest.of(page, 3);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }


    public ResponseEntity<Project> editProject(long id, Project project) {

        if (id < 0 ){ // Todo replace with null when using Long instead of long
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not edit project with " + id + ", because the given id is not allowed.");
        } else if (project == null || project.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not edit project with " + id + ", because project or project.name is null");
        }
        Optional<Project> existingProject = projectRepository.findById(id);

        if (existingProject.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Can not edit project with id: " + id);
        }
        Project databaseProject = existingProject.get();

        if ((!project.getName().equals(databaseProject.getName())) &&
                projectRepository.existsByName(project.getName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with the new name already exists in the database.");
        }

        if (!project.getName().equals("")) databaseProject.setName(project.getName());
        if (!project.getDescription().equals("")) databaseProject.setDescription(project.getDescription());
        if (!project.getImage().equals("")) databaseProject.setImage(project.getImage());
        databaseProject.setProgress(project.getProgress());


        //Create the tags!
        if (project.getProjectTags() != null){
            for (ProjectTag tag: project.getProjectTags()) {
                if (!databaseProject.getProjectTags().contains(tag)) {
                    tag.setProject(databaseProject);
                    projectTagRepository.save(tag);
                }
            }
        }

        Project savedProject = projectRepository.save(databaseProject);

        return ResponseEntity.ok(savedProject);
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
