package cn.edu.hbwe.gogo.exception;

import lombok.Getter;

import java.util.Arrays;

public class LoginException extends RuntimeException {
    public LoginException() {
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException(String message) {
        super(message);
    }

    public static class CookieInitFailed extends LoginException {
        public CookieInitFailed() {
            super("初始化Cookie失败");
        }

        public CookieInitFailed(String message) {
            super(message);
        }
    }

    public static class CookieOutOfDate extends LoginException {
        public CookieOutOfDate(String msg) {
            super("Cookie已过期：" + msg);
        }

        public CookieOutOfDate() {
            super("Cookie已过期");
        }
    }

    @Getter
    public static class NeedCaptcha extends LoginException {

        private final byte[] image;

        public NeedCaptcha(byte[] image) {
            super("需要验证码");
            this.image = Arrays.copyOf(image, image.length);
        }
    }
}
