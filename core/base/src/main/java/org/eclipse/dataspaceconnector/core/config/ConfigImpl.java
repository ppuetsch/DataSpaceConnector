/*
 *  Copyright (c) 2021 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */
package org.eclipse.dataspaceconnector.core.config;

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.system.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class ConfigImpl implements Config {

    static final Collector<Map.Entry<String, String>, ?, Map<String, String>> TO_MAP =
            toMap(Map.Entry::getKey, Map.Entry::getValue);

    private final Map<String, String> entries;
    private final String rootPath;

    ConfigImpl(Map<String, String> entries) {
        this("", entries);
    }

    protected ConfigImpl(String rootPath, Map<String, String> entries) {
        Objects.requireNonNull(rootPath, "rootPath");
        this.entries = entries;
        this.rootPath = rootPath;
    }

    @Override
    public String getString(String key) {
        return getNotNullValue(key, this::getString);
    }

    @Override
    public String getString(String key, String defaultValue) {
        var value = entries.get(absolutePathOf(key));
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public Integer getInteger(String key) {
        return getNotNullValue(key, this::getInteger);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return getNumber(key, defaultValue, "integer", Integer::parseInt);
    }

    @Override
    public Long getLong(String key) {
        return getNotNullValue(key, this::getLong);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return getNumber(key, defaultValue, "long", Long::parseLong);
    }

    @Override
    public Config getConfig(String path) {
        String absolutePath = absolutePathOf(path);
        var filteredEntries = entries.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(absolutePath))
                .collect(TO_MAP);

        return new ConfigImpl(absolutePath, filteredEntries);
    }

    @Override
    public Config merge(Config other) {
        var all = new HashMap<String, String>();
        all.putAll(entries);
        all.putAll(other.getEntries());

        return new ConfigImpl("", Collections.unmodifiableMap(all));
    }

    @Override
    public Stream<Config> partition() {
        return getRelativeEntries().keySet().stream().map(it -> it.split("\\.")[0]).distinct().map(group -> getConfig(group));
    }

    @Override
    public Map<String, String> getEntries() {
        return entries;
    }

    @Override
    public Map<String, String> getRelativeEntries() {
        return getEntries().entrySet().stream()
                .map(entry -> Map.entry(removePrefix(entry.getKey(), rootPath), entry.getValue()))
                .collect(TO_MAP);
    }

    @Override
    public Map<String, String> getRelativeEntries(String basePath) {
        return getRelativeEntries().entrySet().stream().filter(entry -> entry.getKey().startsWith(basePath)).collect(TO_MAP);
    }

    @Override
    public String currentNode() {
        var parts = rootPath.split("\\.");
        return parts[parts.length - 1];
    }

    @Override
    public boolean isLeaf() {
        return entries.size() == 1 && entries.keySet().stream().allMatch(rootPath::equals);
    }

    @Override
    public boolean hasKey(String key) {
        return getEntries().containsKey(key);
    }

    private String removePrefix(String path, String rootPath) {
        if (!rootPath.isEmpty() && path.startsWith(rootPath)) {
            return path.substring(rootPath.length() + 1);
        } else {
            return path;
        }
    }

    @Nullable
    private <T> T getNumber(String key, T defaultValue, String typeDescription, Function<String, T> parse) {
        var value = getString(key, Objects.toString(defaultValue, null));
        if (value == null) {
            return null;
        } else {
            try {
                return parse.apply(value);
            } catch (Exception e) {
                throw new EdcException(format("Setting %s with value %s cannot be parsed to %s", absolutePathOf(key), value, typeDescription));
            }
        }
    }

    @NotNull
    private <T> T getNotNullValue(String key, BiFunction<String, T, T> function) {
        var value = function.apply(key, null);
        if (value == null) {
            throw new EdcException(format("No setting found for key %s", absolutePathOf(key)));
        } else {
            return value;
        }
    }

    @NotNull
    private String absolutePathOf(String key) {
        String rootPath = Optional.of(this.rootPath).filter(it -> !it.isEmpty()).map(it -> it + ".").orElse("");
        return rootPath + key;
    }

}
