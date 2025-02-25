package Me.Labs.Lab1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Lab1 extends Application {


    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox pane = new VBox(10);
        Button btn = new Button("Encode");
        Button de = new Button("Decode");
        TextField tf = new TextField();
        TextField tf1 = new TextField();
        TextArea ta = new TextArea();
        ta.setEditable(false);
        pane.setPadding(new Insets(10));
//        pane.setBorder(new Border(77));
        pane.getChildren().addAll(new Label("Word"), tf, new Label("Shift Val"), tf1, ta, btn, de);
        btn.setOnAction(e -> {
            int shift = 0;
            try {
                shift = Integer.parseInt(tf1.getText());
            } catch (Exception ex) {
                ta.setText("Error");
            }
            String m = tf.getText();
            if (m.isEmpty())
                ta.setText("Enter word");
            else
                ta.setText(CesarCipher(shift, m));

        });

        de.setOnAction(e -> {
            int shift = Integer.parseInt(tf1.getText());
            String m = ta.getText();


            ta.setText(CesarCipher(-shift, m));
        });
        Scene scene = new Scene(pane);

        primaryStage.setTitle("MyJavaFX"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

//        Stage stage = new Stage(); // Create a new stage
//        stage.setTitle("Second Stage"); // Set the stage title
//        // Set a scene with a button in the stage
//        stage.setScene(new Scene(new Button("New Stage"), 100, 100));
//        stage.show();
    }

    public String CesarCipher(int shift, String m) {
        StringBuilder answer = new StringBuilder();
        for (char c : m.toCharArray()) {
//                ta.appendText(String.valueOf(c+4));
            if (Character.isLetter(c)) {
                char b = Character.isLowerCase(c) ? 'a' : 'A';
                answer.append(String.valueOf((char) ((c + shift - b) % 26 + b)));
            } else {
                answer.append(String.valueOf(c));
            }

        }
        return answer.toString();
    }
}


