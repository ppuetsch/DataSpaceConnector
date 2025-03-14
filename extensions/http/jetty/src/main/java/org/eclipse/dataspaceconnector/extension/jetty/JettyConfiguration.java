/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.extension.jetty;

import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.system.Config;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JettyConfiguration {

    public static final String WEB_HTTP_PREFIX = "web.http";
    public static final String DEFAULT_PATH = "/api";
    public static final String DEFAULT_CONTEXT_NAME = "default";
    public static final int DEFAULT_PORT = 8181;
    @EdcSetting
    private static final String HTTP_PORT = "web.http.port";
    private final String keystorePassword;
    private final String keymanagerPassword;
    private final Set<PortMapping> portMappings;

    public JettyConfiguration(String keystorePassword, String keymanagerPassword) {
        this.keystorePassword = keystorePassword;
        this.keymanagerPassword = keymanagerPassword;
        portMappings = new HashSet<>();
    }

    public static JettyConfiguration createFromConfig(String keystorePassword, String keymanagerPassword, Config config) {
        var jettyConfig = new JettyConfiguration(keystorePassword, keymanagerPassword);

        var subConfig = config.getConfig(WEB_HTTP_PREFIX);

        Map<String, Map<String, String>> tempMappings = new HashMap<>();
        subConfig.getRelativeEntries().entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(expandKey(e), e.getValue()))
                .forEach(e -> split(tempMappings, e));

        var portMappings = tempMappings.entrySet().stream()
                .map(e -> new PortMapping(e.getKey(), Integer.parseInt(e.getValue().getOrDefault("port", "" + DEFAULT_PORT)), e.getValue().getOrDefault("path", DEFAULT_PATH)))
                .collect(Collectors.toSet());

        jettyConfig.portMappings.addAll(portMappings);


        if (jettyConfig.getPortMappings().isEmpty()) {
            jettyConfig.portMapping(new PortMapping());
        }

        return jettyConfig;
    }

    /**
     * converts a map entry, that looks like "something.port" -> 1234, into a map entry, that looks like
     * "something" -> ("port" -> "1234") and adds it to an existing map
     */
    private static void split(Map<String, Map<String, String>> rawMappings, Map.Entry<String, String> entry) {

        var key = entry.getKey();
        var value = entry.getValue();

        // only <alias>.[port|path] is accepted
        if (key.split("\\.").length != 2) {
            return;
        }

        var lastDotIndex = key.lastIndexOf(".");
        var keyNamePart = key.substring(0, lastDotIndex);
        var keyComponentPart = key.substring(lastDotIndex + 1);

        var map = rawMappings.computeIfAbsent(keyNamePart, s -> new HashMap<>());
        if (map.containsKey(keyComponentPart)) {
            throw new IllegalArgumentException(String.format("A port mapping for web.http.%s already exists, currently mapped to %s", key, map));
        }
        map.put(keyComponentPart, value);

    }


    //prepends the default context name ("default") to a key if necessary
    private static String expandKey(Map.Entry<String, ?> entry) {
        return entry.getKey().contains(".") ? entry.getKey() : DEFAULT_CONTEXT_NAME + "." + entry.getKey();
    }

    public Set<PortMapping> getPortMappings() {
        return portMappings;
    }

    public void portMapping(PortMapping mapping) {
        portMappings.add(mapping);
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeymanagerPassword() {
        return keymanagerPassword;
    }

}
