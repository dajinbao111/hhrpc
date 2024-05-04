package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {
    List<InstanceMeta> data;
}
