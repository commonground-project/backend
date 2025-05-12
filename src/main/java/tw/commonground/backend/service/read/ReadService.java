package tw.commonground.backend.service.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import org.springframework.context.event.EventListener;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.read.dto.ReadMapper;
import tw.commonground.backend.service.read.dto.ReadResponse;
import tw.commonground.backend.service.read.entity.ReadEntity;
import tw.commonground.backend.service.read.entity.ReadKey;
import tw.commonground.backend.service.read.entity.ReadObjectType;
import tw.commonground.backend.service.read.entity.ReadRepository;
import tw.commonground.backend.service.reply.ReplyCreatedEvent;
import tw.commonground.backend.service.reply.dto.QuoteReply;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.Optional;
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

    public ReadEntity updateReadStatus(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseGet(() -> getReadEntity(userId, objectId, objectType));
        entity.setReadStatus(true);
        return readRepository.save(entity);
    }

    private ReadEntity getReadEntity(Long userId, UUID objectId, ReadObjectType objectType) {
        /*
        * if the read entity does not exist, create a new one
        * the read status is set to false for viewpoint and reply
        * since the new created viewpoint and reply are not read yet
        * the read status is set to true for issue
        * since the new created issue is read
        * */

        ReadEntity entity = new ReadEntity();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        entity.setUser(user);

        if (objectType == ReadObjectType.ISSUE) {
            IssueEntity issue = issueRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Issue not found"));
            entity.setIssue(issue);
            entity.setReadStatus(true);
        } else if (objectType == ReadObjectType.VIEWPOINT) {
            ViewpointEntity viewpoint = viewpointRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Viewpoint not found"));
            entity.setViewpoint(viewpoint);
            entity.setReadStatus(false);
        } else if(objectType == ReadObjectType.REPLY) {
            ReplyEntity reply = replyRepository.findById(objectId)
                    .orElseThrow(() -> new EntityNotFoundException("Reply not found"));
            entity.setReply(reply);
            entity.setReadStatus(false);
        } else {
            throw new IllegalArgumentException("Invalid object type for read status update");
        }
        ReadKey key = new ReadKey(userId, objectId, objectType);
        entity.setId(key);
        return entity;
    }

    public ReadResponse getReadStatus(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseGet(() -> getReadEntity(userId, objectId, objectType));
        return ReadMapper.toResponse(entity);
    }

    @EventListener
    @Transactional
    public void onReplyCreated(ReplyCreatedEvent event) {
        handleReplyCreatedEvent(event);
    }

    public void handleReplyCreatedEvent(ReplyCreatedEvent event) {
        ReplyEntity reply = event.getReplyEntity();
        ViewpointEntity viewpoint = reply.getViewpoint();
        IssueEntity issue = viewpoint.getIssue();
        Long senderId = event.getUser().getId();

//        List<UserEntity> relatedUsers = userRepository.findAllByIssueOrViewpoint(issue.getId(), viewpoint.getId());

//        List<UserEntity> folloUsers = followRe
//                userRepository.findAllByIssueOrViewpoint(issue.getId(), viewpoint.getId());

//        for (UserEntity user : relatedUsers) {
//            if (user.getId().equals(senderId)) continue;
//
//            createOrUpdateUnread(user.getId(), reply.getId(), ReadObjectType.REPLY, reply);
//            createOrUpdateUnread(user.getId(), viewpoint.getId(), ReadObjectType.VIEWPOINT, viewpoint);
//        }
//
//        // quoteReply 也可以視情況設為綠色
//        for (QuoteReply quote : event.getQuotes()) {
//            UUID quotedReplyId = quote.getReplyId();
//            ReplyEntity quotedReply = replyRepository.findById(quotedReplyId).orElse(null);
//            if (quotedReply != null) {
//                for (UserEntity user : relatedUsers) {
//                    if (!user.getId().equals(senderId)) {
//                        createOrUpdateUnread(user.getId(), quotedReply.getId(), ReadObjectType.REPLY, quotedReply);
//                    }
//                }
//            }
//        }
    }

    private void createOrUpdateUnread(Long userId, UUID objectId, ReadObjectType objectType, Object entityObj) {
        Optional<ReadEntity> existing = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType);
        if (existing.isPresent()) {
            ReadEntity read = existing.get();
            read.setReadStatus(false);
            readRepository.save(read);
            return;
        }

        ReadEntity read = new ReadEntity();
        read.setId(new ReadKey(userId, objectId, objectType));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        read.setUser(user);
        read.setReadStatus(false);

        switch (objectType) {
            case ISSUE -> read.setIssue((IssueEntity) entityObj);
            case VIEWPOINT -> read.setViewpoint((ViewpointEntity) entityObj);
            case REPLY -> read.setReply((ReplyEntity) entityObj);
        }

        readRepository.save(read);
    }
}
