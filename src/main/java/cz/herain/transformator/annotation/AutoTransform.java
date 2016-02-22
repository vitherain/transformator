package cz.herain.transformator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoTransform {

    String entityAttributeName() default "";
}
