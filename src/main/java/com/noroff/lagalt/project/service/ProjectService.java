package com.noroff.lagalt.project.service;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.userhistory.UserHistoryDTO;
import com.noroff.lagalt.userhistory.model.ActionType;
import com.noroff.lagalt.userhistory.model.UserHistory;
import com.noroff.lagalt.userhistory.repository.UserHistoryRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

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

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    private final static int MAXDESCRIPTIONLENGTH = 1000;
    private final static int MAXGENERALLENGTH = 50;
    private final static int MAXIMAGELENGTH = 1000;
    private final static int LIMITADDTAGS = 1000;

    public ResponseEntity<Project> create(Project project) {
        // Checks to check that project has all necessary data set.
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (project.getName() == null || project.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn er ikke satt.");
        } else if (project.getCategory() == null || project.getCategory().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektkategori er ikke satt."); // todo Må den være satt?
        } else if (project.getProgress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektprogresjon er ikke satt."); // todo Må den være satt?
        } else if (project.getName().length() > MAXGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektnavn kan ikke være lengre enn " + MAXGENERALLENGTH + " tegn.");
        } else if (project.getDescription() != null && project.getDescription().length() > MAXDESCRIPTIONLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXDESCRIPTIONLENGTH + " tegn.");
        } else if (project.getImage() != null && project.getImage().length() > MAXIMAGELENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (project.getCategory() != null && project.getCategory().length() > MAXGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXGENERALLENGTH + " tegn.");
        } else if (project.getProjectTags() != null && project.getProjectTags().size() > LIMITADDTAGS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXGENERALLENGTH + " kvalifikasjoner.");
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

        // Saves the given project
        Project createdProject = projectRepository.save(project);

        // Saves project tags to project tag repository
        if (project.getProjectTags() != null) {

            for (ProjectTag tag : project.getProjectTags()) {
                if (tag.getTag().length() > MAXGENERALLENGTH) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXGENERALLENGTH);
                }
                tag.setProject(createdProject);
                projectTagRepository.save(tag);
            }
        }

        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    // Returns all projects stored
    public ResponseEntity<List<Project>> getAll() {
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    // Gets a project based on the id
    public ResponseEntity<Project> getById(Long id, String authHeader) {

        Optional<Project> project = projectRepository.findById(id);

        if (project.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen prosjekter med id: " + id);
        }

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        User requestUser = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User mismatch"));


        LocalDate localDate = LocalDate.now();
        UserHistory uh = new UserHistory();
        uh.setUser(requestUser);
        uh.setActionType(ActionType.CLICKED);
        uh.setTimestamp(localDate.toString());
        uh.setProject_id(id);
        userHistoryRepository.save(uh);

        return ResponseEntity.ok(project.get());
    }


    // Returns projects for a given page
    public ResponseEntity<Page<Project>> showDisplayProjects(int page, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        User requestUser = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User mismatch"));

        List<UserHistoryDTO> dto = userHistoryRepository.getRecordCountForId(requestUser.getId());
        List<Project> projects = projectRepository.findAll();
        List<Project> sortedProjects = new ArrayList<>(projects);

        for (UserHistoryDTO userHistoryDTO : dto){
            for (Project p : projects){
                if (userHistoryDTO.getProjectId() == p.getId()){
                    sortedProjects.remove(p); // WHAAAAAAAAT?
                    sortedProjects.add(p);
                    break;
                }
            }
        }

        //Creates page request
        PageRequest pageRequest = PageRequest.of(page, 5);

        int total = sortedProjects.size();
        int start = toIntExact(pageRequest.getOffset());
        int end = Math.min((start + pageRequest.getPageSize()), total);

        List<Project> output = new ArrayList<>();

        if (start <= end) {
            output = sortedProjects.subList(start, end);
        }

        for (Project p : output){
            LocalDate localDate = LocalDate.now();
            UserHistory uh = new UserHistory();
            uh.setUser(requestUser);
            uh.setActionType(ActionType.SEEN);
            uh.setTimestamp(localDate.toString());
            uh.setProject_id(p.getId());
            userHistoryRepository.save(uh);
        }
        return ResponseEntity.ok(new PageImpl<>(output, pageRequest, total));
    }

    // Gets the project with name containing the search string
    public ResponseEntity<Page<Project>> searchProjects(int page, String searchString) {
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCase(searchString, PageRequest.of(page, 5)));
    }

    // Gets the project with the given category
    public ResponseEntity<Page<Project>> filterProjects(int page, String filterString) {
        return ResponseEntity.ok(projectRepository.findByCategoryIgnoreCase(filterString, PageRequest.of(page, 5)));
    }

    // Gets the project with name containing the search string, and the given category
    public ResponseEntity<Page<Project>> searchAndfilterProjects(int page, String filterString, String searchString) {
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(searchString, filterString, PageRequest.of(page, 5)));
    }

    // Edits a project, with the new data in a project object
    public ResponseEntity<Project> editProject(Long id, Project project, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        // Checks the new project data
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt objektet er ikke satt");
        } else if (project.getDescription() != null && project.getDescription().length() > MAXDESCRIPTIONLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjektbeskrivelse kan ikke være lengre enn " + MAXDESCRIPTIONLENGTH + " tegn.");
        } else if (project.getImage() != null && project.getImage().length() > MAXIMAGELENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image kan ikke ha lenge path enn bestående av " + MAXIMAGELENGTH + " tegn.");
        } else if (project.getCategory() != null && project.getCategory().length() > MAXGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kan ikke bestå av mer enn " + MAXGENERALLENGTH + " tegn.");
        } else if (project.getProjectTags() != null && project.getProjectTags().size() > LIMITADDTAGS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + MAXGENERALLENGTH + " kvalifikasjoner.");
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

        // Finds the project to edit
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No project found by that id"));

        // Replacing the data if new data si set
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


        //Adds the new tags
        if (project.getProjectTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (ProjectTag t : existingProject.getProjectTags()) {
                if (t.getTag().length() > MAXGENERALLENGTH) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXGENERALLENGTH);
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

    // Deletes a project, if the user is the owner
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

    // Stores a user history
    private UserHistory storeUserHistory(Long id, ActionType type) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by that ID"));
        LocalDate localDate = LocalDate.now();
        UserHistory uh = new UserHistory();
        uh.setUser(u);
        uh.setActionType(type);
        uh.setTimestamp(localDate.toString());
        return uh;
    }


    // Gets all project from a category
    public ResponseEntity<List<Project>> getAllFromCategory(String category) {
        List<Project> projects = projectRepository.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }
}
