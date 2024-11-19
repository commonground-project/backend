package tw.commonground.backend.exception;

public final class ProblemTemplate {
    private ProblemTemplate() {
        // hide the constructor
    }

    public static final String UNAUTHORIZED = """
            {
                "type": "UNAUTHORIZED",
                "title": "Unauthorized",
                "status": 401,
                "detail": "Login required"
            }
            """;

    public static final String FORBIDDEN = """
            {
                "type": "FORBIDDEN",
                "title": "Forbidden",
                "status": 403,
                "detail": "Permission denied"
            }
            """;
}
