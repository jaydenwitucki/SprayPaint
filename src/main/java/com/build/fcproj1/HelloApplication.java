package com.build.fcproj1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.PixelFormat;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {

    private Canvas canvas;
    private GraphicsContext gc;
    private ColorPicker colorPicker;
    private Slider densitySlider;

    private boolean isSpraying = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spray Paint App");

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        setupCanvas();

        colorPicker = new ColorPicker();
        densitySlider = new Slider(1, 10, 5);

        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");

        saveButton.setOnAction(e -> saveImage());
        loadButton.setOnAction(e -> loadImage());
        // Add loadButton.setOnAction(e -> loadImage()) if needed

        HBox toolbar = new HBox(10, colorPicker, densitySlider, saveButton, loadButton);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvas);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        setupMouseEvents();
    }

    private void setupCanvas() {
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void setupMouseEvents() {
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isSpraying = true;
                sprayPaint(event.getX(), event.getY());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                erasePaint(event.getX(), event.getY());
            }
        });

        canvas.setOnMouseReleased(event -> isSpraying = false);

        canvas.setOnMouseDragged(event -> {
            if (isSpraying) {
                sprayPaint(event.getX(), event.getY());
            } else {
                erasePaint(event.getX(), event.getY());
            }
        });
    }

    private void sprayPaint(double x, double y) {
        double radius = densitySlider.getValue();
        gc.setFill(colorPicker.getValue());

        for (int i = 0; i < 10; i++) {
            double offsetX = Math.random() * 2 * radius - radius;
            double offsetY = Math.random() * 2 * radius - radius;
            gc.fillOval(x + offsetX, y + offsetY, 2, 2);
        }
    }

    private void erasePaint(double x, double y) {
        double radius = densitySlider.getValue();
        gc.setFill(javafx.scene.paint.Color.WHITE);

        for (int i = 0; i < 10; i++) {
            double offsetX = Math.random() * 2 * radius - radius;
            double offsetY = Math.random() * 2 * radius - radius;
            gc.fillOval(x + offsetX, y + offsetY, 2, 2);
        }
    }

    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            PixelReader pixelReader = canvas.snapshot(null, writableImage).getPixelReader();

            int width = (int) writableImage.getWidth();
            int height = (int) writableImage.getHeight();

            int[] buffer = new int[width * height];

            // Get the pixel format
            PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
            pixelReader.getPixels(0, 0, width, height, (WritablePixelFormat<IntBuffer>) pixelFormat, buffer, 0, width);

            try {
                javax.imageio.ImageIO.write(createBufferedImage(buffer, width, height), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        }
    }
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            // Load the selected image onto the canvas
            Image loadedImage = new Image(file.toURI().toString());
            gc.drawImage(loadedImage, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    private java.awt.image.BufferedImage createBufferedImage(int[] pixels, int width, int height) {
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        return bufferedImage;
    }
}

