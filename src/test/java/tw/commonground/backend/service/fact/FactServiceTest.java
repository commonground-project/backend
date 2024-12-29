package tw.commonground.backend.service.fact;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.reference.ReferenceRepository;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("MethodName")
@ExtendWith(MockitoExtension.class)
public class FactServiceTest {

    @InjectMocks
    private FactService factService;

    @Mock
    private FactRepository factRepository;

    @Mock
    private ReferenceRepository referenceRepository;

    @Captor
    private ArgumentCaptor<List<ReferenceEntity>> captor;

    @Test
    void testParseReferenceEntity_withExistedUrl() {

        List<String> input = List.of("https://www.google.com");

        ReferenceEntity referenceEntity = new ReferenceEntity();
        referenceEntity.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        referenceEntity.setTitle("Google");
        referenceEntity.setUrl("https://www.google.com");
        referenceEntity.setFavicon("https://www.google.com/favicon.ico");

        //Mock referenceRepository.findByUrl
        Mockito.when(referenceRepository.findByUrl("https://www.google.com"))
                .thenReturn(Optional.of(referenceEntity));

        Set<ReferenceEntity> result = factService.parseReferenceEntity(input);

        Set<ReferenceEntity> expected = Set.of(referenceEntity);

        // verify result
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testParseReferenceEntity_withNotExistedUrl() throws IOException {
        List<String> input = List.of("https://www.github.com");

        ReferenceEntity referenceEntity = new ReferenceEntity();
        referenceEntity.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        referenceEntity.setTitle("Github");
        referenceEntity.setUrl("https://www.github.com");
        referenceEntity.setFavicon("https://www.github.com/favicon.ico");

        Mockito.when(referenceRepository.findByUrl("https://www.github.com"))
                .thenReturn(Optional.empty());

        // Simulate the behavior of Connection and Document
        Document mockDocument = new Document("https://www.github.com");
        Element head = mockDocument.appendElement("head");
        mockDocument.title("Github");
        head.appendElement("link")
                .attr("rel", "icon")
                .attr("href", "https://www.github.com/favicon.ico");

        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.get()).thenReturn(mockDocument);

        when(Jsoup.connect("https://www.github.com")).thenReturn(mockConnection);

        Set<ReferenceEntity> result = factService.parseReferenceEntity(input);
        System.out.println(result);

        assertThat(result).hasSize(1);

        // verify referenceRepository.saveAll() is called
        verify(referenceRepository).saveAll(captor.capture());
        List<ReferenceEntity> savedEntities = captor.getValue();
        assertEquals(1, savedEntities.size());
        assertEquals("https://www.github.com", savedEntities.getFirst().getUrl());
        assertEquals("Github", savedEntities.getFirst().getTitle());
        assertEquals("https://www.github.com/favicon.ico", savedEntities.getFirst().getFavicon());
    }

    @Test
    void testGetUrlDetails_withDocumentWithoutIcon() throws Exception {
        Document mockDocument = new Document("https://www.github.com");
        mockDocument.title("Github");

        FactService spyFactService = Mockito.spy(new FactService(factRepository, referenceRepository));
        when(spyFactService.getDocument("https://www.github.com")).thenReturn(mockDocument);

        ReferenceEntity referenceEntity = spyFactService.getUrlDetails("https://www.github.com");
        assertThat(referenceEntity.getUrl()).isEqualTo("https://www.github.com");
        assertThat(referenceEntity.getTitle()).isEqualTo("Github");
        assertThat(referenceEntity.getFavicon()).isEqualTo("");
    }

    @Test
    void testGetUrlDetails_withIconInLink() throws Exception {
        Document mockDocument = new Document("https://www.github.com");
        Element head = mockDocument.appendElement("head");
        mockDocument.title("Github");
        head.appendElement("link")
                .attr("rel", "icon")
                .attr("href", "https://www.github.com/favicon.ico");

        FactService spyFactService = Mockito.spy(new FactService(factRepository, referenceRepository));
        when(spyFactService.getDocument("https://www.github.com")).thenReturn(mockDocument);

        ReferenceEntity referenceEntity = spyFactService.getUrlDetails("https://www.github.com");
        assertThat(referenceEntity.getUrl()).isEqualTo("https://www.github.com");
        assertThat(referenceEntity.getTitle()).isEqualTo("Github");
        assertThat(referenceEntity.getFavicon()).isEqualTo("https://www.github.com/favicon.ico");
    }

    @Test
    void testGetUrlDetails_withIconInMeta() throws Exception {
        Document mockDocument = new Document("https://www.github.com");
        Element head = mockDocument.appendElement("head");
        mockDocument.title("Github");
        head.appendElement("meta")
                .attr("itemprop", "image")
                .attr("content", "/favicon.ico");

        FactService spyFactService = Mockito.spy(new FactService(factRepository, referenceRepository));
        when(spyFactService.getDocument("https://www.github.com")).thenReturn(mockDocument);

        ReferenceEntity referenceEntity = spyFactService.getUrlDetails("https://www.github.com");
        assertThat(referenceEntity.getUrl()).isEqualTo("https://www.github.com");
        assertThat(referenceEntity.getTitle()).isEqualTo("Github");
        assertThat(referenceEntity.getFavicon()).isEqualTo("https://www.github.com/favicon.ico");
    }

    @Test
    void testUrlHandling() {
        List<String> input = List.of("https://www.google.com");

        List<String> result = factService.urlHandling(input);

        List<String> expected = List.of("https://www.google.com");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testUrlHandling_withUrlWithoutProtocol() {
        List<String> input = List.of("www.google.com");

        List<String> result = factService.urlHandling(input);

        List<String> expected = List.of("https://www.google.com");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testUrlHandling_withEncodedUrl() {
        List<String> input = List.of("https%3A%2F%2Fwww.google.com");

        List<String> result = factService.urlHandling(input);

        List<String> expected = List.of("https://www.google.com");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testUrlHandling_withEncodedUrlWithoutProtocol() {
        List<String> input = List.of("https%3A%2F%2Fwww.google.com");

        List<String> result = factService.urlHandling(input);

        List<String> expected = List.of("https://www.google.com");

        assertThat(result).isEqualTo(expected);
    }
}
