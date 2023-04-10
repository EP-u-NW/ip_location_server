package de.ep_u_nw.ip_location_server.http;

public abstract class HttpException extends Exception {
    private static final long serialVersionUID = 4573001909072057961L;
    public final int responseCode;

    public HttpException(int responseCode) {
        super();
        this.responseCode = responseCode;
    }

    public HttpException(int responseCode, String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.responseCode = responseCode;
    }

    public HttpException(int responseCode, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public HttpException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public HttpException(int responseCode, Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
    }

    public static class MethodNotAllowedException extends HttpException {
        private static final long serialVersionUID = 6532989994512628564L;
        private static final int httpResponseCode = 405;

        public MethodNotAllowedException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public MethodNotAllowedException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public MethodNotAllowedException(String message) {
            super(httpResponseCode, message);
        }

        public MethodNotAllowedException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public MethodNotAllowedException() {
            super(httpResponseCode);
        }
    }

    public static class UnauthorizedException extends HttpException {
        private static final long serialVersionUID = 6532989154512628564L;
        private static final int httpResponseCode = 401;

        public UnauthorizedException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public UnauthorizedException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public UnauthorizedException(String message) {
            super(httpResponseCode, message);
        }

        public UnauthorizedException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public UnauthorizedException() {
            super(httpResponseCode);
        }
    }

    public static class NotFoundException extends HttpException {
        private static final long serialVersionUID = 4857125201554167913L;
        private static final int httpResponseCode = 404;

        public NotFoundException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public NotFoundException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public NotFoundException(String message) {
            super(httpResponseCode, message);
        }

        public NotFoundException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public NotFoundException() {
            super(httpResponseCode);
        }
    }

    public static class ForbiddenException extends HttpException {
        private static final long serialVersionUID = 4857125201554122913L;
        private static final int httpResponseCode = 403;

        public ForbiddenException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public ForbiddenException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public ForbiddenException(String message) {
            super(httpResponseCode, message);
        }

        public ForbiddenException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public ForbiddenException() {
            super(httpResponseCode);
        }
    }

    public static class InternalServerErrorException extends HttpException {
        private static final long serialVersionUID = -3722508587238013379L;
        private static final int httpResponseCode = 500;

        public InternalServerErrorException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public InternalServerErrorException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public InternalServerErrorException(String message) {
            super(httpResponseCode, message);
        }

        public InternalServerErrorException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public InternalServerErrorException() {
            super(httpResponseCode);
        }
    }

    public static class BadRequestException extends HttpException {
        private static final long serialVersionUID = -657045782318461345L;
        private static final int httpResponseCode = 400;

        public BadRequestException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public BadRequestException(String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public BadRequestException(String message) {
            super(httpResponseCode, message);
        }

        public BadRequestException(Throwable cause) {
            super(httpResponseCode, cause);
        }

        public BadRequestException() {
            super(httpResponseCode);
        }
    }

    public static class GenericHttpException extends HttpException {
        private static final long serialVersionUID = -657045782318001345L;

        public GenericHttpException(int httpResponseCode, String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(httpResponseCode, message, cause, enableSuppression, writableStackTrace);
        }

        public GenericHttpException(int httpResponseCode, String message, Throwable cause) {
            super(httpResponseCode, message, cause);
        }

        public GenericHttpException(int httpResponseCode, String message) {
            super(httpResponseCode, message);
        }

        public GenericHttpException(int httpResponseCode, Throwable cause) {
            super(httpResponseCode, cause);
        }

        public GenericHttpException(int httpResponseCode) {
            super(httpResponseCode);
        }
    }
}
