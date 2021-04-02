package com.noroff.lagalt.userhistory;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.project.service.ProjectService;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.userhistory.model.UserHistory;
import com.noroff.lagalt.userhistory.repository.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class UserHistoryController {

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @GetMapping("/history")
    public ResponseEntity<List<UserHistory>> getRecords(){
        List<UserHistory> records = userHistoryRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<UserHistoryDTO>> getRecordsInOrder(@PathVariable(value = "id") long id){
        List<UserHistoryDTO> records = userHistoryRepository.getRecordCountForId(id);
        return ResponseEntity.ok(records);
    }
}

