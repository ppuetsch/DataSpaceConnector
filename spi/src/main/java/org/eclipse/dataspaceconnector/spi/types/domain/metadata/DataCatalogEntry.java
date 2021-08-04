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

package org.eclipse.dataspaceconnector.spi.types.domain.metadata;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.dataspaceconnector.spi.types.domain.Polymorphic;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;

/**
 * Base extension point for data entries.
 */
@JsonTypeName("dataspaceconnector:datacatalogentry")
public interface DataCatalogEntry extends Polymorphic {
    DataAddress getAddress();
}
