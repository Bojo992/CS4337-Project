package com.cs4337.mediastorage.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BucketConfig {
    String awsAccessKey;
    String awsSecretKey;
    String endpoint;

    public BucketConfig(Environment env) {
        awsAccessKey = env.getProperty("AWS_ACCESS_KEY", "defaultKey");
        awsSecretKey = env.getProperty("AWS_SECRET_KEY", "defaultSecret");
        endpoint = env.getProperty("S3_URL", "http://localhost:4566");
    }

    @Bean
    public AmazonS3 getAmazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, "eu-west-1"))
                .enablePathStyleAccess()
                .build();
    }
}
