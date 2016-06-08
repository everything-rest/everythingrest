package rest.everything.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import rest.everything.core.controllers.AdminController
import rest.everything.core.controllers.ApiController
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 *
 */
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Class[] classes = new Class[4];
        classes[0] = Application.class;
        classes[1] = ApiController.class;
        classes[2] = AdminController.class;
        classes[3] = ConfigBean.class;
        SpringApplication.run(classes, args);
    }
}