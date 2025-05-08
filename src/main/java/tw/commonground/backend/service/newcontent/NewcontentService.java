package tw.commonground.backend.service.newcontent;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.newcontent.dto.NewcontentResponse;
import tw.commonground.backend.service.newcontent.entity.NewcontentEntity;
import tw.commonground.backend.service.newcontent.entity.NewcontentObjectType;
import tw.commonground.backend.service.newcontent.entity.NewcontentRepository;
import tw.commonground.backend.service.newcontent.dto.NewcontentMapper;
import tw.commonground.backend.service.newcontent.entity.NewcontentKey;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@Service
public class NewcontentService {
    
    private final NewcontentRepository newcontentRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final ViewpointRepository viewpointRepository;
    
    public NewcontentService(NewcontentRepository newcontentRepository, UserRepository userRepository,
                             IssueRepository issueRepository, ViewpointRepository viewpointRepository) {
        this.newcontentRepository = newcontentRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.viewpointRepository = viewpointRepository;
    }


    public NewcontentEntity updateNewcontentStatus(Long userId, UUID objectId, boolean newcontentStatus, NewcontentObjectType objectType) {
       
        NewcontentEntity entity = newcontentRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseGet(() -> getNewcontentEntity(userId, objectId, objectType));

        entity.setNewcontentStatus(newcontentStatus);

        if(objectType == NewcontentObjectType.VIEWPOINT) {
            // need to also set the issue newcontent status
            ViewpointEntity viewpoint = viewpointRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Viewpoint not found"));
            IssueEntity issue = viewpoint.getIssue();
            updateNewcontentStatus(userId, issue.getId(), newcontentStatus, NewcontentObjectType.ISSUE);
        }
        return newcontentRepository.save(entity);
    }

    private NewcontentEntity getNewcontentEntity(Long userId, UUID objectId, NewcontentObjectType objectType) {
        NewcontentEntity entity = new NewcontentEntity();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        entity.setUser(user);

        if (objectType == NewcontentObjectType.ISSUE) {
            IssueEntity issue = issueRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Issue not found"));
            entity.setIssue(issue);
        } else if (objectType == NewcontentObjectType.VIEWPOINT) {
            ViewpointEntity viewpoint = viewpointRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Viewpoint not found"));
            entity.setViewpoint(viewpoint);
        } else {
            throw new IllegalArgumentException("Invalid object type for newcontent status update");
        }
        NewcontentKey key = new NewcontentKey(userId, objectId, objectType);
        entity.setId(key);
        return entity;
    }

    public NewcontentResponse getNewcontentStatus(Long userId, UUID objectId, NewcontentObjectType objectType) {
        NewcontentEntity entity = newcontentRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseThrow(() -> new EntityNotFoundException("Newcontent status not found"));
        return NewcontentMapper.toResponse(entity);
    }
}
