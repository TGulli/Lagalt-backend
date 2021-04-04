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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    private final static int MAXEDESCRIPTIONLENGTH = 1000;
    private final static int MAXEGENERALLENGTH = 50;
    private final static int MAXIMAGELENGTH = 1000;
    private final static int LIMITADDTAGS = 1000;

    public ResponseEntity<Project> create(Project project) {
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (project.getName() == null || project.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn er ikke satt.");
        } else if (project.getCategory() == null || project.getCategory().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektkategori er ikke satt."); // todo Må den være satt?
        } else if (project.getProgress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektprogresjon er ikke satt."); // todo Må den være satt?
        } else if (project.getName().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn kan ikke være lengre enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (project.getDescription() != null && project.getDescription().length() > MAXEDESCRIPTIONLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXEDESCRIPTIONLENGTH + " tegn.");
        } else if (project.getImage() != null && project.getImage().length() > MAXIMAGELENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (project.getCategory() != null && project.getCategory().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (project.getProjectTags() != null && project.getProjectTags().size() > LIMITADDTAGS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXEGENERALLENGTH + " kvalifikasjoner.");
        } else if (project.getOwner() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det må eksistere minst en eier av prosjektet.");
        } else if (project.getCollaborators() != null && project.getCollaborators().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det kan ikke eksistere noen medlemmer av prosjektet " +
                    "ved opprettelse. De brukerne som ønsker å bli medlem av prosjektet, må sende inn en forespørsel om " +
                    "å bli medlem, og en eier av prosjektet må godta vedkommende.");
        } else if (project.getMessages() != null && project.getMessages().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å opprette et prosjekt med messages.");
        } else if (project.getChatMessages() != null && project.getChatMessages().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å opprette et prosjekt med chatMessages.");
        } else if (projectRepository.existsByName(project.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer allerede et prosjekt med navn: " + project.getName());
        }


        Project createdProject = projectRepository.save(project);

        if (project.getProjectTags() != null) {

            for (ProjectTag tag : project.getProjectTags()) {
                if (tag.getTag().length() > MAXEGENERALLENGTH) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
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
        return ResponseEntity.ok(projectRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen prosjekter med id: " + id)));
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        return ResponseEntity.ok(projectRepository.findAll(PageRequest.of(page, 5)));
    }

    public ResponseEntity<Page<Project>> searchProjects(int page, String searchString) {
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCase(searchString, PageRequest.of(page, 5)));
    }

    public ResponseEntity<Page<Project>> filterProjects(int page, String filterString) {
        return ResponseEntity.ok(projectRepository.findByCategoryIgnoreCase(filterString, PageRequest.of(page, 5)));
    }

    public ResponseEntity<Page<Project>> searchAndfilterProjects(int page, String filterString, String searchString) {
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(searchString, filterString, PageRequest.of(page, 5)));
    }


    public ResponseEntity<Project> editProject(Long id, Project project, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (project.getDescription() != null && project.getDescription().length() > MAXEDESCRIPTIONLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXEDESCRIPTIONLENGTH + " tegn.");
        } else if (project.getImage() != null && project.getImage().length() > MAXIMAGELENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (project.getCategory() != null && project.getCategory().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (project.getProjectTags() != null && project.getProjectTags().size() > LIMITADDTAGS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXEGENERALLENGTH + " kvalifikasjoner.");
        } else if (project.getCollaborators() != null && project.getCollaborators().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det kan ikke eksistere noen medlemmer av prosjektet " +
                    "ved å endre prosjektet. De brukerne som ønsker å bli medlem av prosjektet, må sende inn en forespørsel om " +
                    "å bli medlem, og en eier av prosjektet må godta vedkommende.");
        } else if (project.getMessages() != null && project.getMessages().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å endre på messages.");
        } else if (project.getChatMessages() != null && project.getChatMessages().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å endre på chatMessages.");
        } else if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer ikke et prosjekt med angitt id: " + id.toString());
        }


        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No project found by that id"));

        if (!existingProject.getOwner().getId().equals(requestUser.get().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not yours to edit");
        }


        if (!project.getDescription().equals("")) {
            existingProject.setDescription(project.getDescription());
        }

        if (project.getProgress() != existingProject.getProgress()) {
            existingProject.setProgress(project.getProgress());
        }

        if (!project.getCategory().equals("")) {
            existingProject.setCategory(project.getCategory());
        }

        if (!project.getImage().equals("")) {
            existingProject.setImage(project.getImage());
        }


        //Create the tags!
        if (project.getProjectTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (ProjectTag t : existingProject.getProjectTags()) {
                if (t.getTag().length() > MAXEGENERALLENGTH) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
                currentTags.add(t.getTag().toLowerCase(Locale.ROOT));
            }
            for (ProjectTag tag : project.getProjectTags()) {
                String projectTag = tag.getTag();
                if (!currentTags.contains(projectTag.toLowerCase(Locale.ROOT))) {
                    tag.setProject(existingProject);
                    projectTagRepository.save(tag);
                }
            }
        }

        return new ResponseEntity<>(projectRepository.save(existingProject), HttpStatus.OK);
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
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
}
