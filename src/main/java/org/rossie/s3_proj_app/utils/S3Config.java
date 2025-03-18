package org.rossie.s3_proj_app.utils;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
    private static String region = "eu-west-1";

//    @Bean
//    public static S3Client s3Client() {
//        return S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(ProfileCredentialsProvider.create("myprofile"))
//                .build();
//    }
//
//    @Bean
//    public S3Presigner s3Presigner() {
//        return S3Presigner.builder()
//                .region(Region.of(region))
//                .credentialsProvider(ProfileCredentialsProvider.create())
//                .build();
//    }

    @Bean
    public static S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
