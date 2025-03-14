/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.api.datamanagement.contractdefinition;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.dataspaceconnector.common.testfixtures.TestUtils;
import org.eclipse.dataspaceconnector.extension.jersey.CorsFilterConfiguration;
import org.eclipse.dataspaceconnector.extension.jersey.JerseyRestService;
import org.eclipse.dataspaceconnector.extension.jetty.JettyConfiguration;
import org.eclipse.dataspaceconnector.extension.jetty.JettyService;
import org.eclipse.dataspaceconnector.extension.jetty.PortMapping;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ContractDefinitionsApiControllerIntegrationTest {

    private static int port;
    private OkHttpClient client;

    @BeforeAll
    static void prepareWebserver() {
        port = TestUtils.getFreePort(1024, 10000);
        var monitor = mock(Monitor.class);
        var config = new JettyConfiguration(null, null);
        config.portMapping(new PortMapping("data", port, "/api/v1/data"));
        var jetty = new JettyService(config, monitor);

        var ctrl = new ContractDefinitionApiController(monitor);
        var jerseyService = new JerseyRestService(jetty, new TypeManager(), mock(CorsFilterConfiguration.class), monitor);
        jetty.start();
        jerseyService.registerResource("data", ctrl);
        jerseyService.start();
    }

    @BeforeEach
    void setup() {
        client = new OkHttpClient();
    }

    @Test
    void getAllContractDefs() throws IOException {
        var response = get(basePath());
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    void getAllContractDefs_withPaging() throws IOException {
        try (var response = get(basePath() + "?offset=10&limit=15&sort=ASC")) {
            assertThat(response.code()).isEqualTo(200);
        }
    }

    @Test
    void getSingleContractDef() throws IOException {
        var id = "test-id";
        try (var response = get(basePath() + "/" + id)) {
            //assertThat(response.code()).isEqualTo(200);
        }

    }

    @Test
    void getSingleContractDef_notFound() throws IOException {
        try (var response = get(basePath() + "/not-exist")) {
            // assertThat(response.code()).isEqualTo(404);
        }
    }


    @NotNull
    private String basePath() {
        return "http://localhost:" + port + "/api/v1/data/contractdefinitions";
    }

    @NotNull
    private Response get(String url) throws IOException {
        return client.newCall(new Request.Builder().get().url(url).build()).execute();
    }
}
