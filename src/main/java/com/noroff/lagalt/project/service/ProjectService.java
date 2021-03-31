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

    private final static int MAXEDESCRIPTIONLENGTH = 1000;
    private final static int MAXEGENERALLENGTH = 50;
    private final static int MAXIMAGELENGTH = 1000;
    private final static int LIMITADDTAGS = 1000;

    public ResponseEntity<Project> create (Project project){
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (project.getName() == null || project.getName().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn er ikke satt.");
        } else if (project.getCategory() == null || project.getCategory().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektkategori er ikke satt."); // todo Må den være satt?
        } else if (project.getProgress() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektprogresjon er ikke satt."); // todo Må den være satt?
        } else if (project.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn kan ikke være lengre enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (project.getDescription() != null && project.getDescription().length() > MAXEDESCRIPTIONLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXEDESCRIPTIONLENGTH + " tegn.");
        } else if (project.getImage() != null && project.getImage().length() > MAXIMAGELENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (project.getCategory() != null && project.getCategory().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (project.getProjectTags() != null && project.getProjectTags().size() > LIMITADDTAGS){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXEGENERALLENGTH + " kvalifikasjoner.");
        } else if (project.getOwners() == null || project.getOwners().size() < 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det må eksistere minst en eier av prosjektet.");
        } else if (project.getCollaborators() != null && project.getCollaborators().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det kan ikke eksistere noen medlemmer av prosjektet " +
                    "ved opprettelse. De brukerne som ønsker å bli medlem av prosjektet, må sende inn en forespørsel om " +
                    "å bli medlem, og en eier av prosjektet må godta vedkommende.");
        } else if (project.getMessages() != null && project.getMessages().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å opprette et prosjekt med messages.");
        } else if (project.getChatMessages() != null && project.getChatMessages().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å opprette et prosjekt med chatMessages.");
        } else if (projectRepository.existsByName(project.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer allerede et prosjekt med navn: " + project.getName());
        }

        // todo sjekk at eier er verifisert med token


        Project createdProject = projectRepository.save(project);

        if (project.getProjectTags() != null) {

            List<String> uniqueTags = new ArrayList<>();
            for (ProjectTag tag : project.getProjectTags()) {
                if (tag.getTag().length() > MAXEGENERALLENGTH){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
                uniqueTags.add(tag.getTag().toLowerCase(Locale.ROOT));
                if (!uniqueTags.contains(tag.getTag().toLowerCase(Locale.ROOT))) {
                    tag.setProject(createdProject);
                    projectTagRepository.save(tag);
                }
            }
        }

        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Project>> getAll (){
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Project> getById(Long id) {
        return ResponseEntity.ok(projectRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen prosjekter med id: " + id)));
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        return ResponseEntity.ok(projectRepository.findAll(PageRequest.of(page, 5)));
    }


    public ResponseEntity<Project> editProject(Long id, Project newProject, Long userId){
        if (newProject == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (newProject.getName() != null && newProject.getName().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn kan ikke være en tom String.");
        } else if (newProject.getCategory() != null && newProject.getCategory().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektkategori kan ikke være en tom String."); // todo Må den være satt?
        } else if (newProject.getName() != null && newProject.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn kan ikke være lengre enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (newProject.getDescription() != null && newProject.getDescription().length() > MAXEDESCRIPTIONLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXEDESCRIPTIONLENGTH + " tegn.");
        } else if (newProject.getImage() != null && newProject.getImage().length() > MAXIMAGELENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (newProject.getCategory() != null && newProject.getCategory().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (newProject.getProjectTags() != null && newProject.getProjectTags().size() > LIMITADDTAGS){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXEGENERALLENGTH + " kvalifikasjoner.");
        } else if (newProject.getCollaborators() != null && newProject.getCollaborators().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det kan ikke eksistere noen medlemmer av prosjektet " +
                    "ved å endre prosjektet. De brukerne som ønsker å bli medlem av prosjektet, må sende inn en forespørsel om " +
                    "å bli medlem, og en eier av prosjektet må godta vedkommende.");
        } else if (newProject.getMessages() != null && newProject.getMessages().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å endre på messages.");
        } else if (newProject.getChatMessages() != null && newProject.getChatMessages().size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det er ikke mulig å endre på chatMessages.");
        } else if (newProject.getId() != null && !newProject.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke endre et prosjekt, hvor id'en i prosjektet er ulik det som er sendt med i request body.");
        } else if (!projectRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer ikke et prosjekt med angitt id: " + id.toString());
        }


        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke prosjektet som skulle endres med id: " + id.toString()));

        if (!existingProject.getName().equalsIgnoreCase(newProject.getName()) && projectRepository.existsByName(newProject.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer allerede et prosjekt med navn: " + newProject.getName());
        }

        // todo replace with token authentication?
        boolean isOwner = false;
        for (User owner : existingProject.getOwners()) {
            if (owner.getId().equals(userId)) {
                isOwner = true;
                break;
            }
        }
        if (!isOwner){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brukeren er ikke eier av prosjektet.");
        }

        existingProject.setName(newProject.getName());
        existingProject.setCategory(newProject.getCategory());

        if (newProject.getDescription() != null){
            existingProject.setDescription(newProject.getDescription());
        }
        if (newProject.getProgress() != null){
            existingProject.setProgress(newProject.getProgress());
        }
        if (newProject.getImage() != null){
            existingProject.setImage(newProject.getImage());
        }

        if (newProject.getProjectTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (ProjectTag t : existingProject.getProjectTags()) {
                if (t.getTag().length() > MAXEGENERALLENGTH){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
                currentTags.add(t.getTag().toLowerCase(Locale.ROOT));
            }
            for (ProjectTag tag : newProject.getProjectTags()) {
                String projectTag = tag.getTag();
                if (!currentTags.contains(projectTag.toLowerCase(Locale.ROOT))) {
                    tag.setProject(existingProject);
                    projectTagRepository.save(tag);
                }
            }
        }
        return new ResponseEntity<>(projectRepository.save(existingProject), HttpStatus.OK);
    }

    public HttpStatus deleteProject(long id){
        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found with id: " + id));

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
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
}
