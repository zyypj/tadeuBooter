package com.github.zyypj.tadeuBooter.downloaders.dependencies;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class DependencyInfo {
    private final String name;
    private final String version;
    private final String downloadURL;
}