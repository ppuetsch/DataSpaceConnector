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

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

val awsVersion: String by project
val storageBlobVersion: String by project;
val jupiterVersion: String by project

dependencies {
    api(project(":spi"))

    testFixturesApi("com.azure:azure-storage-blob:${storageBlobVersion}")

    testFixturesImplementation(project(":common:util"))

    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testFixturesRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}

publishing {
    publications {
        create<MavenPublication>("azure-test") {
            artifactId = "azure-test"
            from(components["java"])
        }
    }
}
