package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext springContext;
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(JavaFxApplication.class);
        ResourceLoader resourceLoader = springContext;
        Resource resource = resourceLoader.getResource("classpath:/templates/ChatApp.fxml");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("20045481 - Luong Van Dat");
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}