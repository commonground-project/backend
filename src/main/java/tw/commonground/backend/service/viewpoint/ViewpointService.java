package tw.commonground.backend.service.viewpoint;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.viewpoint.dto.ViewpointMapper;
import tw.commonground.backend.service.viewpoint.dto.ViewpointUpdateRequest;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReaction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ViewpointService {
    private final ViewpointRepository viewpointRepository;
    private final FactRepository factRepository;

    private final ViewpointMapper viewpointMapper = new ViewpointMapper();

    public ViewpointService(ViewpointRepository viewpointRepository, FactRepository factRepository) {
        this.viewpointRepository = viewpointRepository;
        this.factRepository = factRepository;
    }

    // TODO: issue viewpoint api

    // service checks if the viewpoint exists in the database
    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
    }

    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
        viewpointRepository.flush();
    }

    public ViewpointReaction reactToViewpoint(UUID id, ViewpointReaction reaction) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
        viewpointEntity.setUserReaction(reaction);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity.getUserReaction();
    }

    public ViewpointEntity addFactToViewpoint(UUID id, UUID factId) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", factId.toString()));
        viewpointEntity.getFacts().add(factEntity);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

    public ViewpointEntity updateViewpoint(UUID id, ViewpointUpdateRequest updateRequest) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));

        viewpointEntity.setTitle(updateRequest.getTitle());
        viewpointEntity.setContent(updateRequest.getContent());
        // updateRequest contains a list of factIds, need to get the fact entities from the database
        List<UUID> factIds = updateRequest.getFacts();
        List<FactEntity> facts = factIds.stream()
                .map(factId -> factRepository.findById(factId)
                        .orElseThrow(() -> new EntityNotFoundException("Fact", "id", factId.toString())))
                .collect(Collectors.toList());
        viewpointEntity.setFacts(facts);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

    public void deleteFactFromViewpoint(UUID id, UUID factId) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", factId.toString()));
        viewpointEntity.getFacts().remove(factEntity);
        viewpointRepository.save(viewpointEntity);
    }

}
