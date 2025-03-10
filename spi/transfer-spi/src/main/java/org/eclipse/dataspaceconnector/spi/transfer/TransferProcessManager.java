/*
 *  Copyright (c) 2020, 2020-2021 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.spi.transfer;

import org.eclipse.dataspaceconnector.spi.command.Command;
import org.eclipse.dataspaceconnector.spi.system.Feature;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;

/**
 * Manages data transfer processes. Currently synchronous and asynchronous data transfers are supported.
 * <br/>
 * The {@link DataRequest#isSync()} flag indicates whether a data request should be processed synchronously or asynchronously.
 */
@Feature("edc:core:transfer:transferprocessmanager")
public interface TransferProcessManager {

    /**
     * Initiates a data transfer process on the consumer.
     */
    TransferInitiateResult initiateConsumerRequest(DataRequest dataRequest);

    /**
     * Initiates a data transfer process on the provider.
     */
    TransferInitiateResult initiateProviderRequest(DataRequest dataRequest);

    /**
     * Adds a single Command to the queue which is processed sequentially.
     */
    void enqueueCommand(Command command);

}
