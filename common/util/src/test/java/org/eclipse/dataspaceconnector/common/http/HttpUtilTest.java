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

package org.eclipse.dataspaceconnector.common.http;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpUtilTest {

    private OkHttpClient okHttpClient;

    @BeforeEach
    void setUp() {
        okHttpClient = new OkHttpClient();
    }

    @Test
    void addBasicAuth_verifyNewInstance() {
        var newClient = HttpUtil.addBasicAuth(okHttpClient, "somuser", "somepwd");
        assertThat(newClient).isNotEqualTo(okHttpClient);
        assertThat(newClient.authenticator()).isNotNull();
    }

    @Test
    void addBasicAuth_verifyAuthorizationHeader() throws IOException {
        var newClient = HttpUtil.addBasicAuth(okHttpClient, "somuser", "somepwd");

        Response response = new Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .message("yey, it works")
                .request(new Request.Builder().url("http://localhost/api/test").build())
                .build();
        var rq = newClient.authenticator().authenticate(null, response);

        assertThat(rq.headers()).isNotNull().anyMatch(p -> p.getFirst().equals("Authorization") && p.getSecond().startsWith("Basic "));

    }
}
