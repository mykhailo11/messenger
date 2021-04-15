set PRJ=%cd%
cd bin
java -cp ".;%PRJ%/lib/mongo.jar;%PRJ%/lib/bson.jar" org.chats.proxy.Main