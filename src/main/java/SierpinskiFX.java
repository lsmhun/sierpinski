import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SierpinskiFX extends Application {

    private final static int DEFAULT_WIDTH = 780;
    private final static int DEFAULT_HEIGHT = 780;

    private ColorPicker colorPicker = new ColorPicker();
    private ColorPicker bgColorPicker = new ColorPicker(Color.BLACK);
    private Group root = new Group();

    private boolean isFixedColor = false;

    private GraphicsContext graphicsContext;

    private TextField widthTextArea = new TextField(String.valueOf(DEFAULT_WIDTH));

    // header
    private HBox hBox = new HBox();
    // content
    private VBox vBox = new VBox();

    private Canvas canvas;
    private Button buttonSave;
    private Button genButtonRandomColor = new Button("Random");
    private Button genButtonFixedColor = new Button("Fixed color");
    private Button cleanButton = new Button("Clean");


    @Override
    public void start(final Stage primaryStage) {

        canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_WIDTH);
        graphicsContext = canvas.getGraphicsContext2D();

        initDraw(graphicsContext);

        // Just numbers are allowed for picture width value
        widthTextArea.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    widthTextArea.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        buildSceneDesign(primaryStage);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT + 25);
        primaryStage.setTitle("Sierpinsky 2D generator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void onGenerateButtonAction(){
        int size = Integer.valueOf(widthTextArea.getText());
        canvas.setWidth(size);
        canvas.setHeight(size);
        initDraw(graphicsContext);
        disableAll();
        drawGasket(0, 0, size);
        enableAll();
        graphicsContext.stroke();
    }

    private void buildButtonList(final Stage primaryStage){
        genButtonRandomColor.setOnAction(event -> {
            isFixedColor = false;
            Logger.getLogger(SierpinskiFX.class.getName()).log(Level.INFO, "Clicked on RANDOM color generation");
            onGenerateButtonAction();
        });

        genButtonFixedColor.setOnAction(event -> {
            isFixedColor = true;
            Logger.getLogger(SierpinskiFX.class.getName()).log(Level.INFO, "Clicked on FIXED color generation");
            onGenerateButtonAction();
        });
        cleanButton.setOnAction(event -> initDraw(graphicsContext));

        buttonSave = SaveButtonFactory.create(primaryStage, canvas);

    }

    private void buildSceneDesign(final Stage primaryStage){
        buildButtonList(primaryStage);
        hBox.getChildren().addAll(colorPicker, bgColorPicker, widthTextArea, genButtonFixedColor, genButtonRandomColor, cleanButton, buttonSave);

        vBox.getChildren().addAll(hBox, canvas);
        root.getChildren().add(vBox);
    }

    private void disableAll(){
       hBox.getChildren().stream().forEach(n -> n.setDisable(true));
    }

    private void enableAll(){
        hBox.getChildren().stream().forEach(n -> n.setDisable(false));
    }

    private void initDraw(GraphicsContext gc){

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(bgColorPicker.getValue());

        gc.fillRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle
        gc.setFill(colorPicker.getValue());
    }

    private Color getRandomColor()
    {
        double randomRed = ThreadLocalRandom.current().nextDouble();
        double randomGreen = ThreadLocalRandom.current().nextDouble();
        double randomBlue = ThreadLocalRandom.current().nextDouble();
        Color rColor = Color.color(randomRed, randomGreen, randomBlue);
        Logger.getLogger(SierpinskiFX.class.getName()).log(Level.FINEST, "color=" + rColor);
        return rColor;
    }


    private void drawGasket(int x, int y, int side)
    {
        Logger.getLogger(SierpinskiFX.class.getName()).log(Level.FINEST, "x=" + x + " y=" + y + " " + isFixedColor );

        // draw single white square in middle
        int sub = side / 3; // length of sub-squares

        graphicsContext.setLineWidth(0);

        graphicsContext.setFill(isFixedColor? colorPicker.getValue() : getRandomColor());
        graphicsContext.fillRect(x + sub, y + sub, sub , sub);

        if(sub >= 3) {
            // upper line
            drawGasket(x,           y, sub);
            drawGasket(x + sub,     y, sub);
            drawGasket(x + 2 * sub, y, sub);
            // middle
            drawGasket(x,           y + sub, sub);
            drawGasket(x + 2 * sub, y + sub, sub);
            // bottom
            drawGasket(x,           y + 2 * sub, sub);
            drawGasket(x + sub,     y + 2 * sub, sub);
            drawGasket(x + 2 * sub, y + 2 * sub, sub);
        }
    }

}
