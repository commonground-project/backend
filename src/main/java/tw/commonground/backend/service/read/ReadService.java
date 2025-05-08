package tw.commonground.backend.service.read;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.read.dto.ReadMapper;
import tw.commonground.backend.service.read.dto.ReadRequest;
import tw.commonground.backend.service.read.dto.ReadResponse;
import tw.commonground.backend.service.read.entity.ReadEntity;
import tw.commonground.backend.service.read.entity.ReadKey;
import tw.commonground.backend.service.read.entity.ReadObjectType;
import tw.commonground.backend.service.read.entity.ReadRepository;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
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
    private final ReplyRepository replyRepository;

    public ReadService(ReadRepository readRepository, UserRepository userRepository,
                       IssueRepository issueRepository, ViewpointRepository viewpointRepository, ReplyRepository replyRepository) {
        this.readRepository = readRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.viewpointRepository = viewpointRepository;
        this.replyRepository = replyRepository;
    }

    public ReadEntity updateReadStatus(Long userId, UUID objectId, ReadRequest request, ReadObjectType objectType) {
        Boolean readStatus = request.getReadStatus();
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
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
        } else if(objectType == ReadObjectType.REPLY) {
            ReplyEntity reply = replyRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Reply not found"));
            entity.setReply(reply);
        } else {
            throw new IllegalArgumentException("Invalid object type for read status update");
        }
        ReadKey key = new ReadKey(userId, objectId, objectType);
        entity.setId(key);
        return entity;
    }

    public ReadResponse getReadStatus(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseThrow(() -> new EntityNotFoundException("Read status not found"));
        return ReadMapper.toResponse(entity);
    }
}
