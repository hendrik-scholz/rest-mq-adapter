# rest-mq-adapter

## Configuration

The following environment variables are available to configure the REST MQ adapter.

| Variable | Description                                                                     | Example |
|----------|---------------------------------------------------------------------------------|---------|
| QUEUE_MANAGER | queue manager name                                                         | QM1 |
| CHANNEL | queue manager channel                                                            | DEV.ADMIN.SVRCONN |
| CONNECTION_NAME | name / IP address and port of the queue manager                          | queue-manager(1414) |
| RECEIVE_TIMEOUT | timeout for waiting for responses                                        | 5000 |
| MOCK_SOURCE_QUEUE | queue from which requests are read                                     | DEV.QUEUE.2 |
| MOCK_TARGET_QUEUE | queue to which responses are put                                       | DEV.QUEUE.3 |
| MOCK_RESPONSE_CCSID | CCSID of the mock response                                           | 1208 |
| MOCK_RESPONSE_ENCODING | encoding of the mock response                                     | 273 |
| MOCK_RESPONSE_FILE | file to read the mock response from                                   | /opt/response.txt |
| MOCK_RESPONSE_MESSAGE_TYPE | message type of the mock response - either "text" or "binary" | text |
| LOG_LEVEL | log level of the application                                                   | INFO |

## Start

1. Run ```./gradlew jibDockerBuild``` from the project root to build the REST MQ adapter Docker image.
2. Run ```docker-compose up -d``` to start a queue manager and the REST MQ adapter. 

## OpenAPI

After starting REST MQ adapter, the Swagger UI and the OpenAPI specification are available under the following endpoints. 

http://localhost:8080/swagger-ui/index.html

http://localhost:8080/v3/api-docs

http://localhost:8080/v3/api-docs.yaml