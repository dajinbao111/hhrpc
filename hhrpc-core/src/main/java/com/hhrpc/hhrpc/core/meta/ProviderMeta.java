package com.hhrpc.hhrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProviderMeta {

    private String methodSign;
    private Method method;
    private Object serviceImpl;
}
