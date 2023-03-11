package com.boletafast.main.app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsS3Connection {

	//private static final Logger LOG = LoggerFactory.getLogger(AwsS3Connection.class);
	
	@Value("${aws.access_key_id}")
	private String accesskeyAws;
	
	@Value("${aws.secret_access_key}")
	private String secretkeyAws;
	
	@Value("${aws.s3.bucket}")
	private String nameBucket;
	
	@Value("${aws.s3.region}")
	private String awsRegion;
	
	@Bean
	public AmazonS3 getS3Cliente() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accesskeyAws, secretkeyAws);
		return AmazonS3ClientBuilder.standard()
				.withRegion(awsRegion)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}
}
