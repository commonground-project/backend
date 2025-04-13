package tw.commonground.backend.service.read;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.read.dto.ReadRequest;
import tw.commonground.backend.service.read.entity.ReadEntity;
import tw.commonground.backend.service.read.entity.ReadObjectType;
import tw.commonground.backend.service.read.entity.ReadRepository;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@Service
public class ReadService {
    private final ReadRepository readRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final ViewpointRepository viewpointRepository;

    public ReadService(ReadRepository readRepository, UserRepository userRepository, IssueRepository issueRepository, ViewpointRepository viewpointRepository) {
        this.readRepository = readRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.viewpointRepository = viewpointRepository;
    }

    public ReadEntity updateReadStatus(Long userId, UUID objectId, ReadRequest request) {
        ReadObjectType objectType = request.getObjectType();
        Boolean readStatus = request.getReadStatus();
        ReadEntity entity = readRepository.findByUserIdAndObjectId(userId, objectId)
                .orElseGet(() -> getReadEntity(userId, objectId, objectType));

        entity.setReadStatus(readStatus);
        return readRepository.save(entity);
    }

    private ReadEntity getReadEntity(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = new ReadEntity();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        entity.setUser(user);

        if (objectType == ReadObjectType.ISSUE) {
            IssueEntity issue = issueRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Issue not found"));
            entity.setIssue(issue);
        } else if (objectType == ReadObjectType.VIEWPOINT) {
            ViewpointEntity viewpoint = viewpointRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Viewpoint not found"));
            entity.setViewpoint(viewpoint);
        } else {
            throw new IllegalArgumentException("Invalid object type for read status update");
        }
        entity.setObjectType(objectType);
        return entity;
    }

}
