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

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.command.CommandHandler;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.eclipse.dataspaceconnector.transfer.core.command.commands.TransferProcessCommand;

import static java.lang.String.format;

/**
 * Handler for a {@link TransferProcessCommand}. Deals with obtaining the {@link TransferProcess} from the {@link TransferProcessStore}[
 *
 * @param <T> The concrete type of {@link TransferProcessCommand}
 */
public abstract class TransferProcessCommandHandler<T extends TransferProcessCommand> implements CommandHandler<T> {
    protected final TransferProcessStore store;

    public TransferProcessCommandHandler(TransferProcessStore store) {
        this.store = store;
    }

    @Override
    public void handle(TransferProcessCommand command) {
        var transferProcessId = command.getTransferProcessId();
        var transferProcess = store.find(transferProcessId);
        if (transferProcess == null) {
            throw new EdcException(format("Could not find TransferProcess with ID [%s]", transferProcessId));
        } else {
            if (modify(transferProcess)) {
                store.update(transferProcess);
            }
        }
    }

    /**
     * All operations (read/write/update) on the {@link TransferProcess} should be done inside this method.
     * It will not get called if there was an error obtaining the transfer process from the store.
     * If the {@link TransferProcess} was indeed modified, implementors should return {@code true}, otherwise {@code false}
     *
     * @param process The {@link TransferProcess}
     * @return true if the process was actually modified, false otherwise.
     */
    protected abstract boolean modify(TransferProcess process);
}
