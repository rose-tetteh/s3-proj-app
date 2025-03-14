package org.rossie.s3_proj_app.model;

import lombok.*;

@Data
@Builder
public class ImageDto {
    private String key;
    private String url;
    private String title;

}