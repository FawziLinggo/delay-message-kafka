compile:
	mvn clean compile assembly:single
tes1:
	java -cp target/delay-datafeed-idx-1.0-SNAPSHOT-jar-with-dependencies.jar DelayCommits --config.consumer.path $(PWD)/src/main/resources/consumer.properties
tes2:
	java -cp target/delay-datafeed-idx-1.0-SNAPSHOT-jar-with-dependencies.jar DelayCommits1 --config.consumer.path $(PWD)/src/main/resources/consumer.properties

