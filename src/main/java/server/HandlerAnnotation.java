package server;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerAnnotation {
	public String requestType();
    public String path();
}
