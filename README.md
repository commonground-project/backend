# Backend

## Development

### Linting

This project uses [MegaLinter](https://megalinter.io/) to lint the codebase. To run the linter, use the
following command:

```sh
make lint
```

This command will start a Docker container to run the linter against the codebase.

### Testing

To execute tests, use the command below:

```sh
make test
```

### Running the Application

#### Configuration

Google OAuth2 Configuration

1. Create an `application-dev.properties` file in the `src/main/resources` directory.
2. Copy other configurations from `application.properties`, then add the following configuration to the
   `application-dev.properties` file:

```properties
spring.security.oauth2.client.registration.google.clientId=your-google-client-id
spring.security.oauth2.client.registration.google.clientSecret=your-google-client-secret
```

To start the application with the `dev` profile, run the following command:

```sh
make dev
```

Alternatively, you can use the following command:

```sh
./gradlew bootRun --args='--spring.profiles.active=dev'
```

This will configure your Spring Boot application to use Google OAuth2 for login during development.