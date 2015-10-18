package technology.mainthread.apps.isitup.data.vo;

import com.google.gson.annotations.SerializedName;

import technology.mainthread.apps.isitup.data.vo.enumeration.StatusCode;

public class IsItUpInfo {

    private int id;
    private String domain;
    private int port;
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName("response_ip")
    private String responseIp;
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_time")
    private double responseTime;
    private long lastChecked;

    public int getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }

    public int getStatusCodeInteger() {
        return statusCode;
    }

    public StatusCode getStatusCode() {
        return StatusCode.fromCode(statusCode);
    }

    public String getResponseIp() {
        return responseIp;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    private IsItUpInfo(Builder builder) {
        id = builder.id;
        domain = builder.domain;
        port = builder.port;
        statusCode = builder.statusCode;
        responseIp = builder.responseIp;
        responseCode = builder.responseCode;
        responseTime = builder.responseTime;
        lastChecked = builder.lastChecked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int id;
        private String domain;
        private int port;
        private int statusCode;
        private String responseIp;
        private int responseCode;
        private double responseTime;
        private long lastChecked;

        private Builder() {
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder responseIp(String responseIp) {
            this.responseIp = responseIp;
            return this;
        }

        public Builder responseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder responseTime(double responseTime) {
            this.responseTime = responseTime;
            return this;
        }

        public Builder lastChecked(long lastChecked) {
            this.lastChecked = lastChecked;
            return this;
        }

        public IsItUpInfo build() {
            return new IsItUpInfo(this);
        }
    }
}
