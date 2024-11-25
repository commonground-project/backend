package tw.commonground.backend.exception;

public final class ProblemTemplate {
    private ProblemTemplate() {
        // hide the constructor
    }

    public static final String UNAUTHORIZED = """
            {
                "type": "type:UNAUTHORIZED",
                "title": "Unauthorized",
                "status": 401,
                "detail": "Login required"
            }
            """;

    public static final String FORBIDDEN = """
            {
                "type": "type:FORBIDDEN",
                "title": "Forbidden",
                "status": 403,
                "detail": "Permission denied"
            }
            """;

    public static final String INVALID_ACCESS_TOKEN = """
            {
                "type": "type:INVALID_ACCESS_TOKEN",
                "title": "Invalid access token",
                "status": 403,
                "detail": "Access token is invalid or expired"
            }
            """;
}
