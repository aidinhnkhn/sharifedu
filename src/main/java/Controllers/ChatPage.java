package Controllers;

import elements.chat.Chat;
import elements.chat.pm.Pm;
import elements.chat.pm.PmType;
import elements.people.Student;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import shared.util.ImageSender;
import site.edu.Main;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatPage implements Initializable {
    
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public ImageView chatImage;
    @FXML
    public TextArea chatText;

    @FXML
    public Button fileButton;

    @FXML
    public Button imageButton;

    @FXML
    public ImageView microphoneImage;

    @FXML
    public TextField pmId;

    @FXML
    public Button pmShowButton;

    @FXML
    public TextArea pmText;

    @FXML
    public Button recordButton;

    @FXML
    public Button sendButton;

    @FXML
    public Label usernameLabel;

    @FXML
    public Button homePage;

    @FXML
    public TableColumn<Chat, String> lastPmColumn;

    @FXML
    public TableColumn<Chat, String> nameColumn;

    @FXML
    public TableView<Chat> tableView;

    @FXML
    public ImageView userImage;

    @FXML
    public Label fileSelected;
    private Chat chat;
    private boolean running;
    private static Logger log = LogManager.getLogger(ChatPage.class);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        running = true;
        chat = null;
        new Thread( ()-> {

                while (running) {
                    setupTable();
                    if (chat!= null){
                        for (Chat setChat:Main.mainClient.getChats())
                            if (setChat.getId().equals(chat.getId())){
                                chat = setChat;
                                break;
                            }
                        setupChatText();
                    }
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        log.debug("chat page thread stopped!");
                    }
                }
        }).start();

        if (Main.mainClient.getUser().isTheme()) {
            anchorPane.setStyle("    -fx-background-color:\n" +
                    "            linear-gradient(#4568DC, #B06AB3),\n" +
                    "            repeating-image-pattern(\"Stars_128.png\"),\n" +
                    "            radial-gradient(center 50% 50%, radius 50%, #FFFFFF33, #00000033);\n");
        }
        else
            anchorPane.setStyle("-fx-background-color: CORNFLOWERBLUE");
    }

    private void setupTable(){
        Platform.runLater( ()-> {
            ObservableList<Chat> chats = FXCollections.observableArrayList();
            chats.addAll(Main.mainClient.getChats());
            tableView.setItems(chats);
            lastPmColumn.setCellValueFactory(new PropertyValueFactory<Chat, String>("lastPm"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<Chat, String>("name"));
        });
    }
    public void showPm(ActionEvent actionEvent) {
        if (chat == null) return;
        String id = pmId.getText();
        for (Pm pm : chat.getMessages()){
            if (pm.getId().equals(id)) {
                setupPmImage(pm);
                break;
            }
        }
    }

    private void setupPmImage(Pm pm) {
        byte[] bytes = ImageSender.decode(pm.getFile());
        Image image = new Image(new ByteArrayInputStream(bytes));
        chatImage.setImage(image);
    }

    public void send(ActionEvent actionEvent) {
        if (chat == null) return;
        Pm pm = new Pm(PmType.Text,Main.mainClient.getUser().getUsername());
        pm.setContent(pmText.getText());
        Main.mainClient.getServerController().sendPm(pm,chat.getId());
        pmText.clear();
    }

    public void record(ActionEvent actionEvent) {
        if (chat == null) return;
    }

    public void sendImage(ActionEvent actionEvent)  {
        if (chat == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif")); // limit fileChooser options to image files
        File selectedFile = fileChooser.showOpenDialog(usernameLabel.getScene().getWindow());

        if (selectedFile != null) {

            String imageFile = null;
            try {
                imageFile = selectedFile.toURI().toURL().toString();
                String encoded = ImageSender.encode(selectedFile.getPath());
                Pm pm = new Pm(PmType.Image,Main.mainClient.getUser().getUsername());
                pm.setContent(encoded);
                Main.mainClient.getServerController().sendPm(pm,chat.getId());

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            fileSelected.setText("file status: Image selected");
            fileSelected.setStyle("-fx-text-fill: green");
            log.info("user sent an Image!");
        } else {
            fileSelected.setText("file status: failed!");
            fileSelected.setStyle("-fx-text-fill: red");
        }
    }

    public void sendFile(ActionEvent actionEvent) {
        if (chat == null) return;
    }

    public void goHomePage(ActionEvent actionEvent) {
        running = false;
        if (Main.mainClient.getUser() instanceof Student)
            SceneLoader.getInstance().changeScene("StudentHomePage.fxml",actionEvent);
        else
            SceneLoader.getInstance().changeScene("ProfessorHomePage.fxml",actionEvent);
    }

    public void showChat(MouseEvent event) {
        if (event.getClickCount() <= 1) return;
        chat = tableView.getSelectionModel().getSelectedItem();
        chatText.clear();
        if (chat == null) return;
        setLabelAndImage();
        setupChatText();
    }

    private void setupChatText(){
        Platform.runLater( ()-> {
            chatText.clear();
            for (Pm pm : chat.getMessages())
                chatText.appendText(pm.getMessage() + '\n');
        });
    }
    private void setLabelAndImage() {
        if (Main.mainClient.getUser().getId().equals(chat.getStudentId1()))
            loadILabelAndImage(chat.getImage2(),chat.getStudentName2());
        else
            loadILabelAndImage(chat.getImage1(),chat.getStudentName1());
    }
    private void loadILabelAndImage(String img,String username){
        byte[] bytes = ImageSender.decode(img);
        Image image = new Image(new ByteArrayInputStream(bytes));
        userImage.setImage(image);
        usernameLabel.setText("Chat: "+username);
    }
}
