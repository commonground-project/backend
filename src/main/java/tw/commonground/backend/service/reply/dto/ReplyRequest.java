package tw.commonground.backend.service.reply.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ReplyRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private List<QuoteReplyRequest> quotes;

    private List<UUID> facts;

}