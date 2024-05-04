package com.hhrpc.hhrpc.core.meta;

import com.google.common.base.Strings;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta {

    private String app;
    private String namespace;
    private String name;
    private String env;

    public String toPath() {
        return Strings.lenientFormat("%s_%s_%s_%s", app, namespace, env, name);
    }
}
