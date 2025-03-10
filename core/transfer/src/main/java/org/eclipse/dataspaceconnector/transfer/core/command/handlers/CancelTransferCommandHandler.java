/*
 *  Copyright (c) 2020-2022 Microsoft Corporation
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
package org.eclipse.dataspaceconnector.transfer.core.command.handlers;

import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.eclipse.dataspaceconnector.transfer.core.command.commands.CancelTransferCommand;

public class CancelTransferCommandHandler extends TransferProcessCommandHandler<CancelTransferCommand> {

    public CancelTransferCommandHandler(TransferProcessStore store) {
        super(store);
    }

    @Override
    public Class<CancelTransferCommand> getType() {
        return CancelTransferCommand.class;
    }

    @Override
    protected boolean modify(TransferProcess process) {
        process.transitionError("Cancelled");
        return true;
    }
}
