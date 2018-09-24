package com.github.lyokofirelyte.ElysianLite.Command.Internals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface ELCommand {
	String[] commands();
	String perm() default "el.command";
	String help() default "/command";
	String desc() default "An EL command";
	int max() default 99999;
	int min() default 0;
}
