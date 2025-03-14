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

package org.eclipse.dataspaceconnector.api.exception;

import org.eclipse.dataspaceconnector.api.exception.mappers.EdcApiExceptionMapper;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;


public class ExceptionMapperExtension implements ServiceExtension {
    @Inject
    private WebService webservice;

    @Override
    public void initialize(ServiceExtensionContext context) {
        // registered for the default context.
        webservice.registerController(new EdcApiExceptionMapper());
    }
}
