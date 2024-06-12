package org.example.todoapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@SpringBootApplication
public class TodoappApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoappApplication.class, args);
	}

//	@Value("${DB_USERNAME}")
//	private String dbUsername;
//	@Value("${DB_PASSWORD}")
//	private String dbPassword;
//	@Bean
//	public CommandLineRunner run(){
//		return args -> {
//			System.out.println("DB_USERNAME:::"+dbUsername);
//			System.out.println("DB_PASSWORD:::"+dbPassword);
//		};
//	}

	@Bean
	public CommandLineRunner run(){

		return args -> {
			String region = "ap-northeast-2";

			SsmClient ssmClient = SsmClient.builder()
					.region(Region.of(region))
					.build();


			System.out.println("todo_DB_USERNAME:::"+getParameterValue(ssmClient,"/todo/config/DB_USERNAME"));
			System.out.println("todo_DB_PASSWORD:::"+getParameterValue(ssmClient,"/todo/config/DB_PASSWORD"));
		};
	}

	private String getParameterValue(SsmClient ssmClient, String parameterName){
		GetParameterRequest parameterRequest = GetParameterRequest.builder()
				.name(parameterName)
				.withDecryption(true)
				.build();

		GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
		return parameterResponse.parameter().value();
	}
}
