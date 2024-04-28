package com.hhrpc.hhrpc.core.annotation;

import java.lang.annotation.*;

@Documented // 允许文档化
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited  // 允许继承
public @interface HhRpcProvider {
}
