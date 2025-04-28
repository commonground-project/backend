package tw.commonground.backend.service.viewpoint.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionKey;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ViewpointRepositoryContainer {
    private final ViewpointFactRepository viewpointFactRepository;
    private final ViewpointRepository viewpointRepository;
    private final ViewpointReactionRepository viewpointReactionRepository;

    public ViewpointRepositoryContainer(
            ViewpointFactRepository viewpointFactRepository,
            ViewpointRepository viewpointRepository,
            ViewpointReactionRepository viewpointReactionRepository) {
        this.viewpointFactRepository = viewpointFactRepository;
        this.viewpointRepository = viewpointRepository;
        this.viewpointReactionRepository = viewpointReactionRepository;
    }

    public Page<ViewpointEntity> findAllViewpoints(Pageable pageable) {
        return viewpointRepository.findAll(pageable);
    }

    public Page<ViewpointEntity> findAllViewpointsByIssueId(UUID issueId, Pageable pageable) {
        return viewpointRepository.findAllByIssueId(issueId, pageable);
    }

    public void save(ViewpointEntity viewpointEntity) {
        viewpointRepository.save(viewpointEntity);
    }

    public void saveByViewpointIdAndFactId(UUID viewpointId, UUID factId) {
        viewpointFactRepository.saveByViewpointIdAndFactId(viewpointId, factId);
    }

    public Optional<ViewpointEntity> findViewpointById(UUID id) {
        return viewpointRepository.findById(id);
    }

    public void deleteViewpointById(UUID id) {
        viewpointRepository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return viewpointRepository.existsById(id);
    }

    public Optional<ViewpointReactionEntity> findViewpointReactionById(ViewpointReactionKey id) {
        return viewpointReactionRepository.findById(id);
    }

    public void insertReaction(ViewpointReactionKey id, String reaction) {
        viewpointReactionRepository.insertReaction(id, reaction);
    }

    public void updateReaction(ViewpointReactionKey id, String reaction) {
        viewpointReactionRepository.updateReaction(id, reaction);
    }

    public void updateReactionCount(UUID viewpointId, Reaction reaction, int delta) {
        viewpointRepository.updateReactionCount(viewpointId, reaction, delta);
    }

    public Optional<Reaction> findReactionById(ViewpointReactionKey id) {
        return viewpointReactionRepository.findReactionById(id);
    }

    public List<ViewpointReactionEntity> findReactionsByUserIdAndViewpointIds(Long userId, List<UUID> viewpointIds) {
        return viewpointReactionRepository.findReactionsByUserIdAndViewpointIds(userId, viewpointIds);
    }

    public List<FactEntity> findFactsByViewpointId(UUID viewpointId) {
        return viewpointFactRepository.findFactsByViewpointId(viewpointId);
    }

    public List<ViewpointFactProjection> findFactsByViewpointIds(List<UUID> viewpointIds) {
        return viewpointFactRepository.findFactsByViewpointIds(viewpointIds);
    }
}
