/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.rsql;

import org.eclipse.hawkbit.exception.SpServerError;
import org.eclipse.hawkbit.exception.SpServerRtException;

/**
 * Exception used by the REST API in case of invalid field name in the rsql
 * search parameter.
 * 
 *
 *
 *
 */
public class RSQLParameterUnsupportedFieldException extends SpServerRtException {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new RSQLParameterUnsupportedFieldException with
     * {@link SpServerError#SP_REST_RSQL_PARAM_INVALID_FIELD} error.
     */
    public RSQLParameterUnsupportedFieldException() {
        super(SpServerError.SP_REST_RSQL_PARAM_INVALID_FIELD);
    }

    /**
     * Creates a new RSQLParameterUnsupportedFieldException with
     * {@link SpServerError#SP_REST_RSQL_PARAM_INVALID_FIELD} error.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            getCause() method). (A null value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public RSQLParameterUnsupportedFieldException(final Throwable cause) {
        super(SpServerError.SP_REST_RSQL_PARAM_INVALID_FIELD, cause);
    }

    /**
     * Creates a new RSQLParameterUnsupportedFieldException with
     * {@link SpServerError#SP_REST_RSQL_PARAM_INVALID_FIELD} error.
     * 
     * @param message
     *            the message of the exception
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            getCause() method). (A null value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public RSQLParameterUnsupportedFieldException(final String message, final Throwable cause) {
        super(message, SpServerError.SP_REST_RSQL_PARAM_INVALID_FIELD, cause);
    }
}