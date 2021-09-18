# mercedes-benz-back-end-service

SpringBoot application that runs on port 8082 and listens to <b>Kafka topic "merc-benz-topic"</b> (with 1 replica and 1 partition) decrypts messages (using <b>AES symmetric decryption</b>) and transforms from <b>protobuf format</b> (the schema for which is included in the resources folder for reference) and saves the customer data as an individual file (XML/CSV as specified in request).

# running the app

1. After cloning the repo, run "mvn install" from a terminal and this should generate the runnable jar.
2. Run "java -jar target/BackEndService-v0.1.jar"
3. Any requests sent to the front-end service will be decrypted and saved to the folder <b>/c/tmp/merc-benz/customers/csv/</b> for CSV files and <b>/c/tmp/merc-benz/customers/xml/</b> for xml files.
4. The files shall be named as the username of the customer.
