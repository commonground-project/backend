package tw.commonground.backend.service.reference;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.reference.dto.WebsiteInfoResponse;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ReferenceService {

    private final ReferenceRepository referenceRepository;

    public ReferenceService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    public ReferenceEntity createReferenceFromUrl(String urls) {
        Set<ReferenceEntity> referenceEntities = createReferencesFromUrls(List.of(urls));
        return referenceEntities.stream().findFirst().orElseThrow();
    }

    public Set<ReferenceEntity> createReferencesFromUrls(List<String> urls) {
        urls = urlHandling(urls);

        Set<ReferenceEntity> referenceEntities = new HashSet<>();
        List<ReferenceEntity> newReferenceEntities = new ArrayList<>();

        for (String urlString : urls) {
            referenceRepository.findByUrl(urlString).ifPresentOrElse(referenceEntities::add,
                    () -> {
                        WebsiteInfoResponse websiteInfoResponse = getWebsiteInfo(urlString);
                        ReferenceEntity referenceEntity = ReferenceEntity.builder()
                                .title(websiteInfoResponse.getTitle())
                                .url(urlString)
                                .favicon(websiteInfoResponse.getIcon())
                                .build();
                        newReferenceEntities.add(referenceEntity);
                    }
            );
        }

        referenceRepository.saveAll(newReferenceEntities);

        referenceEntities.addAll(newReferenceEntities);
        return referenceEntities;
    }

    public WebsiteInfoResponse getWebsiteInfo(String urlString) {
        WebsiteInfoResponse websiteInfoResponse = new WebsiteInfoResponse();

        Document document;
        try {
            document = getDocument(urlString);
        } catch (Exception e) {
            log.error("Error fetching website, type: {}, message: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new WebsiteFetchException();
        }

        try {
            websiteInfoResponse.setTitle(document.title());

            URL url = new URI(urlString).toURL();

            // Get favicon from link tag
            String faviconUrl = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";
            Element iconTag = document.selectFirst("link[rel~=(?i)^(icon|shortcut icon)$]");

            if (iconTag != null) {
                String iconUrl = iconTag.attr("href");
                if (iconUrl.isEmpty()) {
                    faviconUrl = url.getProtocol() + "://" + url.getHost() + iconTag.attr("content");
                } else {
                    faviconUrl = appendHostAndProtocol(iconUrl, url);
                }
            } else {
                iconTag = document.selectFirst("meta[itemprop~=(?i)^(image)]");
                if (iconTag != null) {
                    String iconUrl = iconTag.attr("content");
                    if (!iconUrl.isEmpty()) {
                        faviconUrl = appendHostAndProtocol(iconUrl, url);
                    }
                }
            }

            websiteInfoResponse.setIcon(faviconUrl);
        } catch (URISyntaxException | MalformedURLException ignored) {
            websiteInfoResponse.setIcon("");
            websiteInfoResponse.setTitle("");
        }

        return websiteInfoResponse;
    }

    protected Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    protected List<String> urlHandling(List<String> urls) {
        List<String> decodedUrls = new ArrayList<>();
        for (String url : urls) {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
            if (!decodedUrl.startsWith("https://") && !decodedUrl.startsWith("http://")) {
                decodedUrl = "https://" + decodedUrl;
            }
            decodedUrls.add(decodedUrl);
        }
        return decodedUrls;
    }

    private String appendHostAndProtocol(String path, URL url) {
        if (!path.startsWith("https://") && !path.startsWith("http://")) {
            return url.getProtocol() + "://" + url.getHost() + path;
        }

        return path;
    }
}
