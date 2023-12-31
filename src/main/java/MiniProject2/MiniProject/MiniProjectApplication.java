package MiniProject2.MiniProject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniProjectApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(MiniProjectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // You can add your initialization logic here
        System.out.println("MiniProject Application Started!");

        // Example: Loading some initial data
       
    }
}
