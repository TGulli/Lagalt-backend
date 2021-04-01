package com.noroff.lagalt.project.service;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.security.JwtTokenUtil;
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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public ResponseEntity<Project> create(Project project) {
        if (project == null || project.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project or project.name or project.owner is null.");
        }
        Optional<Project> oldProject = projectRepository.findByName(project.getName());
        if (oldProject.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project already exist with the given name.");
        }
        Project createdProject = projectRepository.save(project);
        //Create the tags!
        if (project.getProjectTags() != null) {
            for (ProjectTag tag : project.getProjectTags()) {
                tag.setProject(createdProject);
                projectTagRepository.save(tag);
            }
        }
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Project>> getAll() {
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Project> getById(Long id) {
        Optional<Project> fetchedProject = projectRepository.findById(id);
        if (fetchedProject.isPresent()) {
            return ResponseEntity.ok(fetchedProject.get());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project found with id: " + id);
        }
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        Pageable p = PageRequest.of(page, 5);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }


    public ResponseEntity<Project> editProject(Long id, Project project, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        if (id == null || project == null || requestUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not edit project with " + id + ", because project, project.name or user is null");
        }

        HttpStatus status;

        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No project found by that id"));

        if (!existingProject.getOwner().getId().equals(requestUser.get().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not yours to edit");
        }


        if (!project.getDescription().equals("")) {
            existingProject.setDescription(project.getDescription());
        }

        if (project.getProgress() != existingProject.getProgress()) {
            existingProject.setProgress(project.getProgress());
        }

        if (!project.getCategory().equals("")){
            existingProject.setCategory(project.getCategory());
        }

        if (!project.getImage().equals("")) {
            existingProject.setImage(project.getImage());
        }


        //Create the tags!
        if (project.getProjectTags() != null) {
            for (ProjectTag tag : project.getProjectTags()) {
                if (!existingProject.getProjectTags().contains(tag)) {
                    tag.setProject(existingProject);
                    projectTagRepository.save(tag);
                }
            }
        }

        Project savedProject = projectRepository.save(existingProject);
        status = HttpStatus.OK;
        return new ResponseEntity<>(savedProject, status);
    }

    public HttpStatus deleteProject(Long id, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        if (requestUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user");
        }

        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found"));

        if (!fetchedProject.getOwner().getId().equals(requestUser.get().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to delete project");
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
