# le4db-canoe_system
## システムの説明
本システムは3回生後期の実験(データベース)において作成したものである.
システム作成にはJavaServletとpostgreSQLを用いている.
## システムの要件
システム実行のためにはJavaとPostgreSQLのインストールが必要となる.
また、レポートに記載の通りテーブルを作成することでシステムは正常に動作する.

## 実行方法
```javac -d WEB-INF/classes -cp servlet-api-3.1.jar:WEB-INF/lib/*.jar WEB-INF/src/*.java```

で再コンパイルを行い
```java -cp .:servlet-api-3.1.jar:jetty-all-9.4.9.v20180320-uber.jar StartJetty```
によってシステムを起動する. 
ブラウザを立ち上げhttp://localhost:8080/ にアクセスすることで利用することができる.
