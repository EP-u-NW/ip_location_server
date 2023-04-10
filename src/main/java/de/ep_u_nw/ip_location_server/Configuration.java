package de.ep_u_nw.ip_location_server;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Configuration {

    private final static Logger LOGGER = Logger.getLogger(Configuration.class.getSimpleName());
    public final BooleanVariable grpcEnabled = new BooleanVariable("GRPC_ENABLED", true);
    public final StringVariable grpcHost = new StringVariable("GRPC_HOST", "0.0.0.0");
    public final IntVariable grpcPort = new IntVariable("GRPC_PORT", 8081);
    public final StringVariable grpcSslDir = new StringVariable("GRPC_SSL_DIR", null, true);
    public final BooleanVariable grpcUseSsl = new BooleanVariable("GRPC_USE_SSL", false);
    public final StringVariable grpcMeHeaderName = new StringVariable("GRPC_ME_HEADER_NAME", "X-Forwarded-For");
    public final BooleanVariable grpcMeUseHeader = new BooleanVariable("GRPC_ME_USE_HEADER", false);

    public final BooleanVariable httpEnabled = new BooleanVariable("HTTP_ENABLED", true);
    public final StringVariable httpHost = new StringVariable("HTTP_HOST", "0.0.0.0");
    public final IntVariable httpPort = new IntVariable("HTTP_PORT", 80);
    public final StringVariable httpSslDir = new StringVariable("HTTP_SSL_DIR", null, true);
    public final BooleanVariable httpUseSsl = new BooleanVariable("HTTP_USE_SSL", false);
    public final BooleanVariable httpDetailedExceptionResponses = new BooleanVariable(
            "HTTP_DETAILED_EXCEPTION_RESPONSES", true);
    public final StringVariable httpMeHeaderName = new StringVariable("HTTP_ME_HEADER_NAME", "X-Forwarded-For");
    public final BooleanVariable httpMeUseHeader = new BooleanVariable("HTTP_ME_USE_HEADER", false);

    public final BooleanVariable ipv4Enabled = new BooleanVariable("IPV4_ENABLED", true);
    public final StringVariable ipv4GithubUser = new StringVariable("IPV4_GITHUB_USER", "sapics");
    public final StringVariable ipv4GithubRepo = new StringVariable("IPV4_GITHUB_REPO", "ip-location-db");
    public final StringVariable ipv4GithubFile = new StringVariable("IPV4_GITHUB_FILE",
            "geolite2-city/geolite2-city-ipv4-num.csv.gz");
    public final BooleanVariable ipv4GithubFileUseGzip = new BooleanVariable("IPV4_GITHUB_FILE_USE_GZIP", true);
    public final LongVariable ipv4GithubCheckIntervalMS = new LongVariable("IPV4_GITHUB_CHECK_INTERVAL_MS",
            60 * 60 * 1000L);
    public final IntVariable ipv4GithubMaxFileSize = new IntVariable("IPV4_GITHUB_MAX_FILE_SIZE", 100 * 1024 * 1024);

    public final BooleanVariable ipv6Enabled = new BooleanVariable("IPV6_ENABLED", true);
    public final StringVariable ipv6GithubUser = new StringVariable("IPV6_GITHUB_USER", "sapics");
    public final StringVariable ipv6GithubRepo = new StringVariable("IPV6_GITHUB_REPO", "ip-location-db");
    public final StringVariable ipv6GithubFile = new StringVariable("IPV6_GITHUB_FILE",
            "geolite2-city/geolite2-city-ipv6-num.csv.gz");
    public final BooleanVariable ipv6GithubFileUseGzip = new BooleanVariable("IPV6_GITHUB_FILE_USE_GZIP", true);
    public final LongVariable ipv6GithubCheckIntervalMS = new LongVariable("IPV6_GITHUB_CHECK_INTERVAL_MS",
            60 * 60 * 1000L);
    public final IntVariable ipv6GithubMaxFileSize = new IntVariable("IPV6_GITHUB_MAX_FILE_SIZE", 100 * 1024 * 1024);

    public final BooleanVariable debug = new BooleanVariable("DEBUG", false);

    private Variable<?>[] getAll() {
        return new Variable<?>[] { grpcEnabled, grpcHost, grpcPort, grpcSslDir, grpcUseSsl, grpcMeHeaderName,
                grpcMeUseHeader, httpEnabled, httpHost, httpPort, httpSslDir, httpUseSsl,
                httpDetailedExceptionResponses, httpMeHeaderName, httpMeUseHeader, ipv4Enabled, ipv4GithubUser,
                ipv4GithubRepo, ipv4GithubFile, ipv4GithubFileUseGzip, ipv4GithubCheckIntervalMS, ipv4GithubMaxFileSize,
                ipv6Enabled, ipv6GithubUser, ipv6GithubRepo, ipv6GithubFile, ipv6GithubFileUseGzip,
                ipv6GithubCheckIntervalMS, ipv6GithubMaxFileSize, debug };
    }

    private Configuration() {
    }

    public static Configuration fromEnvironment() {
        Configuration config = new Configuration();
        for (Variable<?> var : config.getAll()) {
            String keyVal = System.getenv(var.key);
            if (keyVal == null) {
                LOGGER.warning(var.key + " not defined, using default \"" + var.defaultValue + "\"");
            } else if (var.parse(keyVal.trim())) {
                LOGGER.info(var.key + ": " + var.value);
            } else {
                LOGGER.warning(var.key + " invalid, using default \"" + var.defaultValue + "\"");
            }
        }
        return config;
    }

    public static Configuration defaultConfiguration() {
        return new Configuration();
    }

    public abstract static class Variable<T> {
        public final String key;
        public final T defaultValue;
        public final boolean nullAllowed;
        private T value;

        public Variable(String key, T defaultValue, boolean nullAllowed) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.nullAllowed = nullAllowed;
        }

        protected void setValue(T value) {
            this.value = value;
        }

        public T valueOrDefault() {
            if (value == null) {
                if (defaultValue == null) {
                    if (nullAllowed) {
                        return null;
                    } else {
                        throw new NullPointerException("Value for " + key + " not set!");
                    }
                } else {
                    return defaultValue;
                }
            }
            return value;
        }

        public abstract boolean parse(String string);

        @Override
        public String toString() {
            return "" + valueOrDefault();
        }
    }

    public class StringVariable extends Variable<String> {

        public StringVariable(String key, String defaultValue, boolean nullAllowed) {
            super(key, defaultValue, nullAllowed);
        }

        public StringVariable(String key, String defaultValue) {
            this(key, defaultValue, false);
        }

        @Override
        public boolean parse(String string) {
            setValue(string);
            return true;
        }

    }

    public class IntVariable extends Variable<Integer> {

        public IntVariable(String key, Integer defaultValue, boolean nullAllowed) {
            super(key, defaultValue, nullAllowed);
        }

        public IntVariable(String key, Integer defaultValue) {
            this(key, defaultValue, false);
        }

        @Override
        public boolean parse(String string) {
            try {
                setValue(Integer.parseInt(string));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public class LongVariable extends Variable<Long> {

        public LongVariable(String key, Long defaultValue, boolean nullAllowed) {
            super(key, defaultValue, nullAllowed);
        }

        public LongVariable(String key, Long defaultValue) {
            this(key, defaultValue, false);
        }

        @Override
        public boolean parse(String string) {
            try {
                setValue(Long.parseLong(string));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public class BooleanVariable extends Variable<Boolean> {

        public BooleanVariable(String key, Boolean defaultValue, boolean nullAllowed) {
            super(key, defaultValue, nullAllowed);
        }

        public BooleanVariable(String key, Boolean defaultValue) {
            this(key, defaultValue, false);
        }

        @Override
        public boolean parse(String string) {
            String test = string.trim().toLowerCase();
            if (test.equals("true") || test.equals("1")) {
                setValue(true);
                return true;
            } else if (test.equals("false") || test.equals("0")) {
                setValue(false);
                return true;
            } else {
                return false;
            }
        }

    }

    public class SetVariable extends Variable<Set<String>> {

        public SetVariable(String key, Set<String> defaultValue, boolean nullAllowed) {
            super(key, defaultValue, nullAllowed);
        }

        public SetVariable(String key, Set<String> defaultValue) {
            this(key, defaultValue, false);
        }

        @Override
        public boolean parse(String string) {
            Set<String> set = new HashSet<String>();
            for (String entry : string.split(",")) {
                set.add(entry.trim());
            }
            setValue(set);
            return true;
        }

    }
}
