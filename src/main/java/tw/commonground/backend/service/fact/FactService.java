package tw.commonground.backend.service.fact;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.reference.*;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class FactService {

    private final FactRepository factRepository;

    private final ReferenceRepository referenceRepository;

    public FactService(FactRepository factRepository, ReferenceRepository referenceRepository) {
        this.factRepository = factRepository;
        this.referenceRepository = referenceRepository;
    }

    public Page<FactEntity> getFacts(Pageable pageable) {
        return factRepository.findAll(pageable);
    }

    public List<FactEntity> getFacts(List<UUID> ids) {
        return factRepository.findAllById(ids);
    }

    public FactEntity getFact(UUID id) {

        return factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
    }

    public FactEntity createFact(FactRequest factRequest, FullUserEntity user) {
        FactEntity factEntity = FactEntity.builder()
                .title(factRequest.getTitle())
                .references(new HashSet<>())
                .build();

        factEntity.setAuthor(user);

        Set<ReferenceEntity> referenceEntities = parseReferenceEntity(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factRepository.save(factEntity);
    }

    // sameUrls represent the urls which already save in ReferenceEntity
    public FactEntity updateFact(UUID id, FactRequest factRequest) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        factEntity.setTitle(factRequest.getTitle());

        Set<ReferenceEntity> referenceEntities = parseReferenceEntity(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factRepository.save(factEntity);
    }

    public void deleteFact(UUID id) {
        factRepository.findById(id).ifPresent(factRepository::delete);
    }

    public Set<ReferenceEntity> getFactReferences(UUID id) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        return factEntity.getReferences();
    }

    public Set<ReferenceEntity> createFactReferences(UUID id,
                                                        List<ReferenceRequest> referenceRequests) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        if (factEntity.getReferences() == null) {
            factEntity.setReferences(new HashSet<>());
        }

        List<String> urls = referenceRequests.stream().map(ReferenceRequest::getUrl).toList();
        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.addAll(parseReferenceEntity(urls));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);

        return factEntity.getReferences();
    }

    public void deleteFactReferences(UUID id, UUID referenceId) {
        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );

        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.removeIf(referenceEntity -> referenceEntity.getId().equals(referenceId));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);
    }

    public void throwIfFactsNotExist(List<UUID> factIds) {
        List<UUID> existingFactIds = factRepository.findExistingIdsByIds(factIds);
        List<UUID> missingFacts = factIds.stream()
                .filter(factId -> !existingFactIds.contains(factId))
                .toList();
        if (!missingFacts.isEmpty()) {
            throw new EntityNotFoundException("Fact", "ids", missingFacts.toString());
        }
    }

    private Set<ReferenceEntity> parseReferenceEntity(List<String> urls) {
        urls = urlHandling(urls);

        Set<ReferenceEntity> referenceEntities = new HashSet<>();
        List<ReferenceEntity> newReferenceEntities = new ArrayList<>();

        for (String urlString : urls) {
            referenceRepository.findByUrl(urlString).ifPresentOrElse(referenceEntities::add,
                    () -> {
                        ReferenceEntity referenceEntity = new ReferenceEntity(urlString);
                        try {
                            Document document = Jsoup.connect(urlString).get();
                            referenceEntity.setTitle(document.title());

                            URL url = new URL(urlString);
                            Element iconTag = document.selectFirst("link[rel~=(?i)^(icon|shortcut icon)$]");

                            if (iconTag != null) {
                                referenceEntity.setFavicon(iconTag.attr("href"));
                            }

                            iconTag = document.selectFirst("meta[itemprop~=(?i)^(image)]");
                            if (iconTag != null) {
                                String host = url.getHost();
                                if (!host.startsWith("www.")) {
                                    host = "www." + host;
                                }

                                String iconUrl = url.getProtocol() + "://" + host + iconTag.attr("content");
                                referenceEntity.setFavicon(iconUrl);
                            }

                        } catch (Exception ignored) {
                            referenceEntity.setFavicon("");
                            referenceEntity.setTitle("");
                        }

                        newReferenceEntities.add(referenceEntity);
                    }
            );
        }

        referenceRepository.saveAll(newReferenceEntities);

        referenceEntities.addAll(newReferenceEntities);
        return referenceEntities;
    }

    private List<String> urlHandling(List<String> urls) {
        List<String> decodedUrls = new ArrayList<>();
        for (String url : urls) {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
            if (!decodedUrl.startsWith("https://")) {
                decodedUrl = "https://" + decodedUrl;
            }
            decodedUrls.add(decodedUrl);
        }
        return decodedUrls;
    }
}
