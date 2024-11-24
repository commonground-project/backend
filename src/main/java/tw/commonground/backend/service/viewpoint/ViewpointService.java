package tw.commonground.backend.service.viewpoint;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.viewpoint.dto.ViewpointMapper;
import tw.commonground.backend.service.viewpoint.dto.ViewpointResponse;
import tw.commonground.backend.service.viewpoint.dto.ViewpointUpdateRequest;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static sun.awt.image.MultiResolutionCachedImage.map;

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

    public Optional<ViewpointEntity> getViewpoint(UUID id) {
        return viewpointRepository.findViewpointEntityById(id);
    }

    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
        viewpointRepository.flush();
    }

    public ViewpointReaction reactToViewpoint(UUID viewpointId, ViewpointReaction reaction) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(viewpointId).orElseThrow(
                () -> new RuntimeException("Viewpoint not found"));
        viewpointEntity.setUserReaction(reaction);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity.getUserReaction();
    }

    public ViewpointEntity addFactToViewpoint(UUID viewpointId, UUID factId) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(viewpointId).orElseThrow(
                () -> new RuntimeException("Viewpoint not found"));
        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                () -> new RuntimeException("Fact not found"));
        viewpointEntity.getFacts().add(factEntity);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

    public ViewpointEntity updateViewpoint(UUID id, ViewpointUpdateRequest updateRequest) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new RuntimeException("Viewpoint not found"));

        viewpointEntity.setTitle(updateRequest.getTitle());
        viewpointEntity.setContent(updateRequest.getContent());
        // updateRequest contains a list of factIds, need to get the fact entities from the database
        List<UUID> factIds = updateRequest.getFacts();
        List<FactEntity> facts = factIds.stream()
                .map(factId -> factRepository.findById(factId)
                        .orElseThrow(() -> new RuntimeException("Fact not found")))
                .collect(Collectors.toList());
        viewpointEntity.setFacts(facts);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

    public void deleteFactFromViewpoint(UUID viewpointId, UUID factId) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(viewpointId).orElseThrow(
                () -> new RuntimeException("Viewpoint not found"));
        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                () -> new RuntimeException("Fact not found"));
        viewpointEntity.getFacts().remove(factEntity);
        viewpointRepository.save(viewpointEntity);
    }

}
