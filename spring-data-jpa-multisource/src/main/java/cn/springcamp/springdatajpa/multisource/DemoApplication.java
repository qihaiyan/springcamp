package cn.springcamp.springdatajpa.multisource;

import cn.springcamp.springdatajpa.multisource.service.TestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoApplication implements CommandLineRunner{

	@Autowired
	private TestService testService;

	@Override
	public void run(String... args) {
		System.out.println(this.testService.getHelloMessage());
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
