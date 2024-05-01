package com.hhrpc.hhrpc.core.api;

import java.util.List;

public interface Router<T> {

    List<T> rout(List<T> urls);

    Router DEFAULT = (urls) -> urls;
}
