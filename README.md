
# How to Run
- Can be run using a standard Spring Configuration in intellij. 
- Alternatively you can use `./gradlew bootRun`

# How to test
1. Ensure you have an entry file of some sort and then you can use a curl command:

`curl -F "file=@EntryFile.txt" http://localhost:8080/v1/entries/parse -H "X-Forwarded-For: 24.48.0.1"`

2. if you want to access the database to check that everything has been correctly written to it, you can visit:

`localhost:8080/h2-console`

3. you can login using the credentials in `application.yaml`

4. Additionally, you may need to set the JDBC URL to `jdbc:h2:mem:reqlogdb`



