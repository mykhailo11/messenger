set PRJ=%cd%
cd src
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/server/*.java
javac -d "%PRJ%/bin" org/chats/messenger/*.java
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/proxy/*.java
javac -cp ".;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/client/sc/*.java
javac -cp ".;%PRJ%/lib/bson.jar" --module-path "%PRJ%/lib/javafx/lib" --add-modules javafx.controls,javafx.fxml -d "%PRJ%/bin" org/chats/client/gui/*.java
cd ..