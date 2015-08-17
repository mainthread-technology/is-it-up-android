package technology.mainthread.apps.isitup.data.vo.enumeration;

public enum StatusCode {
    UP, DOWN, INVALID_DOMAIN;

    public static StatusCode fromCode(int code) {
        code--;
        for (StatusCode statusCode : StatusCode.values()) {
            if (code == statusCode.ordinal()) {
                return statusCode;
            }
        }
        return null;
    }
}
