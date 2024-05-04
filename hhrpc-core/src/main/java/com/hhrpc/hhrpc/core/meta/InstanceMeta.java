package com.hhrpc.hhrpc.core.meta;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.*;

import java.util.Map;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstanceMeta {

    private String schema;
    private String host;
    private Integer port;
    private String context;
    private boolean status;
    private Map<String, String> parameters = Maps.newHashMap();

    public String toPath() {
        return Strings.lenientFormat("%s_%s", host, port);
    }

    public String toUrl() {
        return Strings.lenientFormat("%s://%s:%s/", schema, host, port);
    }
}
