/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.uberfire.dmr.poc.client.local.dmr;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import org.jboss.dmr.client.ModelNode;

/**
 * Simplified Callback for DMR requests.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2014 Red Hat Inc.
 */
public abstract class DMRCallback implements RequestCallback {

    private boolean rethrowExceptionOnError = true;

    public DMRCallback() {}

    public DMRCallback(boolean rethrowExceptionOnError) {
        this.rethrowExceptionOnError = rethrowExceptionOnError;
    }

    abstract public void dmrResponse(ModelNode responseNode);

    @Override
    public void onResponseReceived(Request request, Response response) {
        ModelNode responseNode = ModelNode.fromBase64(response.getText());
        dmrResponse(responseNode);
    }

    @Override
    public void onError(Request request, Throwable exception) {
        if (exception != null) exception.printStackTrace();
        if (rethrowExceptionOnError) throw new RuntimeException(exception);
    }

}
