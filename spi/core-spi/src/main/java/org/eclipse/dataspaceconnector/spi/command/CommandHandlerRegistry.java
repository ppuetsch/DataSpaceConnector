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
package org.eclipse.dataspaceconnector.spi.command;

/**
 * Links together a Command and its handler class
 */
public interface CommandHandlerRegistry {
    <C extends Command> void register(CommandHandler<C> handlerClass);

    <C extends Command> CommandHandler<C> get(Class<C> commandClass);
}
