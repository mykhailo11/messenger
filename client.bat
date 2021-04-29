set PRJ=%cd%
cd bin
java -cp ".;%PRJ%/lib/bson.jar" --module-path "%PRJ%/lib/javafx/lib" --add-modules javafx.controls,javafx.fxml org.chats.client.gui.Main