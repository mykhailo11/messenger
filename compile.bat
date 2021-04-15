set PRJ=%cd%
cd src
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/server/*.java
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/messenger/*.java
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/proxy/*.java
javac -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" -d "%PRJ%/bin" org/chats/client/*.java
cd ..