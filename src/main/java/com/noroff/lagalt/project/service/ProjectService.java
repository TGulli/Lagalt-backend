package com.noroff.lagalt.project.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.usertags.model.UserTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTagRepository projectTagRepository;

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


    public ResponseEntity<Project> editProject(long id, Project project) throws NoItemFoundException{

        Project databaseProject = projectRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No project by id: " + id));

        if (!project.getName().equals("")) databaseProject.setName(project.getName());
        if (!project.getDescription().equals("")) databaseProject.setDescription(project.getDescription());

        //Create the tags!
        if (project.getProjectTags() != null){
            for (ProjectTag tag: project.getProjectTags()) {

                if (!databaseProject.getProjectTags().contains(tag)) {
                    tag.setProject(project);
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
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(projects, status);
    }
}
