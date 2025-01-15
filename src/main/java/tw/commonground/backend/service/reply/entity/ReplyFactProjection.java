package tw.commonground.backend.service.reply.entity;

import tw.commonground.backend.service.fact.entity.FactEntity;

import java.util.UUID;

public interface ReplyFactProjection {

    UUID getReplyId();

    FactEntity getFact();

}
