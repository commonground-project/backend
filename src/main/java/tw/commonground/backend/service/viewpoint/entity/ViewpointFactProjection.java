package tw.commonground.backend.service.viewpoint.entity;

import tw.commonground.backend.service.fact.entity.FactEntity;

import java.util.UUID;

public interface ViewpointFactProjection {
    UUID getViewpointId();

    FactEntity getFact();
}
