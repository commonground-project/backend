package tw.commonground.backend.service.image;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ImageService {
    private final WebClient webClient;

    public ImageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Fetch image as byte array. If image is less than 1MB, can use byte array to reduce complexity
     *
     * @param imageUrl the image url
     * @return the mono
     */
    public Mono<byte[]> fetchImage(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    /**
     * Fetch image as stream. If image is more than 1MB, should use stream to avoid OOM
     *
     * @param imageUrl the image url
     * @return the mono
     */
    public Mono<DataBuffer> fetchImageAsStream(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(DataBuffer.class);
    }
}
