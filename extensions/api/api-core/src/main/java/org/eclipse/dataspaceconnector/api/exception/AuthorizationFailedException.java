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

import org.eclipse.dataspaceconnector.spi.EdcException;

/**
 * Indicates that an authorization was not possible or failed, e.g. due to missing or unreadable auth headers
 */
public class AuthorizationFailedException extends EdcException {
    public AuthorizationFailedException() {
        super("Authorization not possible or failed");
    }
}
