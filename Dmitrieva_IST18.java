package dmitrieva_ist18;
///Разработка интерфейса ввода данных  для фото-галереи
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
 
public class Dmitrieva_IST18 extends Application {
    private Desktop desktop = Desktop.getDesktop(); 
    private ImageView imageView;
    private Image I;
    private int i=0, c = 0;
    private String localUrl;
    private List<Image> images;
    private Iterator<Image> imageIterator;
    
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        
        Button m = new Button("Галерея");
        root.setAlignment(m, Pos.TOP_CENTER);
        root.setMargin(m, new Insets(10, 0, 0, 0));
        
        Button m2 = new Button("Слайд-шоу");
        
        Button m3 = new Button("Добавить фото");
        root.setAlignment(m3, Pos.BOTTOM_CENTER);
        root.setMargin(m3, new Insets(0, 0, 10, 0));
        
        root.getChildren().addAll(m, m2, m3);
        
        Scene scene = new Scene(root, 300, 150);
        primaryStage.setTitle("ФОТО-ГАЛЕРЕЯ");
        primaryStage.setScene(scene);
        primaryStage.show();
        //подключение к БД
        try{
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foto", "dmitrieva", "12345")){
                System.out.println("Connection true");
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM data");
                while(resultSet.next()){
                    c++;
                }
            }
        } catch (Exception ex){
            System.out.println("Connection false");
            System.out.println(ex);
        }
        
        Foto [] f = new Foto[c];
        
        //запись данных в массив
        try{
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foto", "dmitrieva", "12345")){
                System.out.println("Connection true");
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM data");
                while(resultSet.next()){
                    String foto = resultSet.getString(1);
                    String name = resultSet.getString(2);
                    String inf = resultSet.getString(3);
                    String date = resultSet.getString(4);
                    String kat = resultSet.getString(5);
                    f[i] = new Foto(foto, name, inf, date, kat); //заполнение масива объектов
                    i++;
                }
            }
        } catch (Exception ex){
            System.out.println("Connection false");
            System.out.println(ex);
        }
        
        m.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FlowPane container = new FlowPane();
                    for (i=0; i<c; i++){
                        I = new Image(f[i].displayfoto());
                        int scaledWidth = (int) (I.getWidth() * 0.2); //уменьшение изображения
                        int scaledHeight = (int) (I.getHeight() * 0.2);
                        I = new Image(f[i].displayfoto(), scaledWidth, scaledHeight, false, true);
                        ImageView imageView = new ImageView();
                        imageView.setImage(I);
                        container.getChildren().addAll(imageView);
                        Label l = new Label("     Название: " + f[i].inf.displayname() + "\n     Описание: " + f[i].inf.displayinf() + "\n     Год: " + f[i].inf.displaydate() + "\n     Раздел: " + f[i].inf.displaykat());
                        container.getChildren().addAll(l);
                    }  
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(container);
                scrollPane.setPannable(true);

                Scene secondScene = new Scene(scrollPane, 400, 450); //создание окна
                Stage newWindow = new Stage();
                newWindow.setTitle("Галерея");
                newWindow.setScene(secondScene);
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(primaryStage);
                newWindow.show();
            }  
        });
        
        String [] full = new String[c];
        for (i=0; i<c; i++){
            full[i] = f[i].displayfoto();
        }
        m2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                images = Arrays.stream(full)
                .map(Image::new)
                .collect(Collectors.toList());
                Collections.shuffle(images);
                imageIterator = images.iterator();
                ImageView imageView = new ImageView();

                Timeline timeline = new Timeline(
                    new KeyFrame(
                        Duration.ZERO,
                            e -> {
                                imageView.setImage(imageIterator.next()); 
                            }
                    ),
                    new KeyFrame(Duration.seconds(2)) //установка таймера
                );
                timeline.setCycleCount(images.size());
                timeline.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        Collections.shuffle(images);
                            imageIterator = images.iterator();
                            timeline.playFromStart();
                        }
                });
                timeline.play();

                StackPane layout = new StackPane(imageView);
                Scene secondScene = new Scene(layout, 1000, 700);
                Stage newWindow = new Stage();
                newWindow.setTitle("Слайд-шоу");
                newWindow.setScene(secondScene);
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(primaryStage);
                newWindow.show();
                newWindow.show();
            }
        });
        
        m3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                final FileChooser fileChooser = new FileChooser();
                configuringFileChooser(fileChooser);
                AnchorPane root = new AnchorPane();
                root.setPadding(new Insets(10));
                Scene secondScene = new Scene(root, 550, 350);

                Stage newWindow = new Stage();
                newWindow.setTitle("Выбор фото");
                newWindow.setScene(secondScene);
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(primaryStage);
                newWindow.show();

                Button button = new Button("Выбрать фото");
                root.setLeftAnchor(button, 10.0);
                root.setTopAnchor(button, 25.0);
                root.getChildren().addAll(button);
                
                button.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        try {
                            File file = fileChooser.showOpenDialog(primaryStage);
                            localUrl = file.toURI().toURL().toString();
                            I = new Image(localUrl);
                            int scaledWidth = (int) (I.getWidth() * 0.2);
                            int scaledHeight = (int) (I.getHeight() * 0.2);
                            I = new Image(localUrl, scaledWidth, scaledHeight, false, true);
                            imageView = new ImageView();
                            root.getChildren().removeAll(imageView);
                            imageView.setImage(I);
                            root.setLeftAnchor(imageView, 10.0);
                            root.setTopAnchor(imageView, 60.0);
                            root.getChildren().addAll(imageView);
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(Dmitrieva_IST18.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                
                Label l1 = new Label("Название: ");
                root.setLeftAnchor(l1, 250.0);
                root.setTopAnchor(l1, 5.0);

                TextField textField = new TextField();
                textField.setPrefColumnCount(20);
                root.setLeftAnchor(textField, 250.0); 
                root.setTopAnchor(textField, 25.0);

                Label l2 = new Label("Описание: ");
                root.setLeftAnchor(l2, 250.0); 
                root.setTopAnchor(l2, 50.0);

                TextArea textArea = new TextArea();
                textArea.setPrefColumnCount(20);
                textArea.setPrefRowCount(5);
                root.setLeftAnchor(textArea, 250.0);
                root.setTopAnchor(textArea, 70.0);

                Label l3 = new Label("Год: ");
                root.setLeftAnchor(l3, 250.0); 
                root.setTopAnchor(l3, 175.0);

                Slider slider = new Slider(2000.0, 2021.0, 2021.0);
                slider.setShowTickMarks(true);
                slider.setShowTickLabels(true);
                slider.setBlockIncrement(1.0);
                slider.setMajorTickUnit(5.0);
                slider.setMinorTickCount(4);
                slider.setSnapToTicks(true);
                root.setLeftAnchor(slider, 250.0);
                root.setTopAnchor(slider, 195.0);

                Label l4 = new Label("Раздел: ");
                root.setLeftAnchor(l4, 250.0); 
                root.setTopAnchor(l4, 235.0);

                ObservableList<String> langs = FXCollections.observableArrayList("Природа", "Спорт", "Люди", "Картины", "Стикеры", "Животные");
                ComboBox<String> langsComboBox = new ComboBox<String>(langs);
                root.setLeftAnchor(langsComboBox, 250.0);
                root.setTopAnchor(langsComboBox, 255.0);
                
                Button b = new Button("Сохранить");
                root.setLeftAnchor(b, 250.0);
                root.setTopAnchor(b, 290.0);
                
                root.getChildren().addAll(l1, textField, l2, textArea, l3, slider, l4, langsComboBox, b);
                //вставка данных в БД
                b.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        try{
                            Class.forName("com.mysql.jdbc.Driver");
                            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foto", "dmitrieva", "12345")){
                                System.out.println("Connection true");
                                Statement statement = conn.createStatement();
                                int rows = statement.executeUpdate("INSERT INTO data(foto, name, inf, year, type) VALUES ('"+localUrl+"','"+textField.getText()+"', '"+textArea.getText()+"','"+slider.getValue()+"','"+langsComboBox.getValue()+"' )");
                                System.out.printf("Added %d rows", rows);
                            }
                        } catch (Exception ex){
                            System.out.println("Connection false");
                            System.out.println(ex);
                        }
                        newWindow.close();
                    }
                });
            }
        });
    }
    //открытие проводника
    private void configuringFileChooser(FileChooser fileChooser) {
          fileChooser.setTitle("Выбор фото");
          fileChooser.setInitialDirectory(new File("C:/КП"));
          fileChooser.getExtensionFilters().addAll(
              new FileChooser.ExtensionFilter("All Files", "*.*"),
              new FileChooser.ExtensionFilter("JPG", "*.jpg"), 
              new FileChooser.ExtensionFilter("PNG", "*.png"));
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
