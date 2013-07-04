package org.gaixie.jibu.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于对 Servlet 中接收的 CI 指令进行权限的验证。
 * <p>
 * 此注解可加在 Servlet 类中的方法上，要求该方法的前两个参数分别为：
 * HttpServletRequest 和 Injector。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CIVerify {
}
