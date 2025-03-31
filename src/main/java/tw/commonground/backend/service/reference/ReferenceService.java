package tw.commonground.backend.service.reference;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tw.commonground.backend.service.reference.dto.FallbackResponse;
import tw.commonground.backend.service.reference.dto.WebsiteInfoResponse;
import tw.commonground.backend.shared.tracing.Traced;
import org.springframework.http.HttpHeaders;


import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Traced
@Service
public class ReferenceService {

    private final ReferenceRepository referenceRepository;

    private static final String FALLBACK_CRAWLER_API = "http://127.0.0.1:8000/title/";

    public ReferenceService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    public ReferenceEntity createReferenceFromUrl(String url) {
        Set<ReferenceEntity> referenceEntities = createReferencesFromUrls(List.of(url));
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
            log.error("Jsoup failed to fetch website, trying fallback API. Type: {}, Message: {}",
                    e.getClass().getSimpleName(), e.getMessage());

            // Use fallback API to fetch title
            String title = fetchTitleFromFallback(urlString);
            if (title != null) {
                websiteInfoResponse.setTitle(title);
            } else {
                throw new WebsiteFetchException();
            }

            websiteInfoResponse.setIcon("");
            return websiteInfoResponse;
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

    public String fetchTitleFromFallback(String urlString) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);


            String apiUrl = FALLBACK_CRAWLER_API + URLEncoder.encode(urlString, StandardCharsets.UTF_8);
            ResponseEntity<FallbackResponse> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, FallbackResponse.class
            );
            if (response.getBody() != null) {
                return  response.getBody().getTitle();
            }
        } catch (Exception e) {
            log.error("Fallback API failed, type: {}, message: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
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
