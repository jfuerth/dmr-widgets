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
package org.jboss.uberfire.poc.client.local;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormCallback;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.uberfire.poc.client.local.ballroom.ConsoleBeanFactory;
import org.jboss.uberfire.poc.client.local.ballroom.ConsoleFramework;
import org.jboss.uberfire.poc.client.local.ballroom.DeploymentScanner;
import org.jboss.uberfire.poc.client.local.dmr.DMRCallback;
import org.jboss.uberfire.poc.client.local.dmr.DMRRequest;
import org.jboss.uberfire.poc.client.local.viewframework.Columns.EnabledColumn;
import org.jboss.uberfire.poc.client.local.viewframework.Columns.NameColumn;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.uberfire.poc.client.local.viewframework.ScannerBridge;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2014 Red Hat Inc.
 */
@Templated
@Page
public class DeploymentScannerPage extends Composite {

    /**
     * The master table that lists all existing Deployment Scanners. The selected item in this table can be viewed and edited in
     * the detail table.
     * <p>
     * In the original HAL console code, this table is managed by an EntityEditor, which is not (yet?) part of this POC.
     */
    @DataField
    private DefaultCellTable<DeploymentScanner> scannerMasterTable = new DefaultCellTable<DeploymentScanner>(4);
    private ListDataProvider<DeploymentScanner> scannerDataProvider;
    /**
     * The detail table that shows the properties of the currently-selected scanner.
     */
    @DataField
    private Widget scannerDetail;
    /**
     * The AutoBeans-driven component that owns the {@link #scannerDetail} widget.
     */
    private Form<DeploymentScanner> scannerForm = new Form<DeploymentScanner>(DeploymentScanner.class);
    /**
     * Global variables that the Ballroom widgets collection cares about.
     */
    @Inject
    private ConsoleFramework consoleFramework;
    /**
     * The model object that's currently selected in the master table and whose details are currently occupying the detail
     * table.
     */
    private DeploymentScanner scanner;
    private TextItem name = new TextItem("name", "Name");
    private CheckBoxItem autoDepExploded = new CheckBoxItem("autoDeployExploded", "Auto-deploy Exploded");
    private CheckBoxItem autoDepXML = new CheckBoxItem("autoDeployXML", "Auto-deploy XML");
    private CheckBoxItem autoDepZip = new CheckBoxItem("autoDeployZipped", "Auto-deploy Zipped");
    private NumberBoxItem deployTimeout = new NumberBoxItem("deploymentTimeout", "Deployment Timeout");
    private TextBoxItem path = new TextBoxItem("path", "Deployments");
    private TextBoxItem relativeTo = new TextBoxItem("relativeTo", "Relative-to");
    private CheckBoxItem scanEnabled = new CheckBoxItem("enabled", "Scan Enabled");
    private NumberBoxItem scanInterval = new NumberBoxItem("scanInterval", "Scan Interval");
    private ScannerBridge dmrBridge;

    public DeploymentScannerPage() {
        scannerForm.setToolsCallback(new FormCallback<DeploymentScanner>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                saveItemValuesToAutoBean();
                dmrBridge.onSaveDetails(scanner, changeset);
            }

            @Override
            public void onCancel(DeploymentScanner entity) {
                //Window.alert("Canceled edit on: " + entity);
            }
        });

        scannerForm.setFields(name, autoDepExploded, autoDepXML, autoDepZip, deployTimeout, path, relativeTo, scanEnabled, scanInterval);
        scannerDetail = scannerForm.asWidget();

        scannerMasterTable.addColumn(new NameColumn(), NameColumn.LABEL);
        scannerMasterTable.addColumn(new EnabledColumn(), EnabledColumn.LABEL);

        final SingleSelectionModel<DeploymentScanner> selectionModel = new SingleSelectionModel<DeploymentScanner>();
        scannerMasterTable.setSelectionModel(selectionModel);
        scannerDataProvider = new ListDataProvider<DeploymentScanner>();
        scannerDataProvider.addDataDisplay(scannerMasterTable);
    }

    private void saveItemValuesToAutoBean() {
        scanner.setAutoDeployExploded(autoDepExploded.getValue());
        scanner.setAutoDeployXML(autoDepXML.getValue());
        scanner.setAutoDeployZipped(autoDepZip.getValue());
        scanner.setDeploymentTimeout(deployTimeout.getValue().longValue());
        scanner.setPath(path.getValue());
        scanner.setRelativeTo(relativeTo.getValue());
        scanner.setEnabled(scanEnabled.getValue());
        scanner.setScanInterval(scanInterval.getValue().intValue());
    }

    @PostConstruct
    private void init() {
        ConsoleBeanFactory factory = consoleFramework.getBeanFactory();

        this.dmrBridge = new ScannerBridge(factory, scannerDataProvider, scannerForm);
        dmrBridge.loadEntities("default");
        //scanner = dmrBridge.findEntity("default");
        scanner = dmrBridge.newEntity();
        scannerForm.setEnabled(false);

        fillScannerData();
    }

    // This can go away if we implement interaction between the table and the detail edit.
    // For now, the editor will only work on the default scanner.
    private void fillScannerData() {
        DMRRequest.sendRequest(defaultScanner(), new DMRCallback() {
            @Override
            public void dmrResponse(ModelNode responseNode) {
                System.out.println("***** Got response *******");
                System.out.println(responseNode.toString());
                System.out.println("**************************");

                scanner.setName("default");

                ModelNode result = responseNode.get("result");
                scanner.setAutoDeployExploded(result.get("auto-deploy-exploded").asBoolean());
                scanner.setAutoDeployXML(result.get("auto-deploy-xml").asBoolean());
                scanner.setAutoDeployZipped(result.get("auto-deploy-zipped").asBoolean());
                scanner.setDeploymentTimeout(result.get("deployment-timeout").asLong());
                scanner.setPath(result.get("path").asString());
                scanner.setRelativeTo(result.get("relative-to").asString());
                scanner.setScanInterval(result.get("scan-interval").asInt());
                scanner.setEnabled(result.get("scan-enabled").asBoolean());
                scannerForm.edit(scanner);
            }
        });
    }

    private ModelNode defaultScanner() {
        ModelNode request = new ModelNode();
        request.get(ModelDescriptionConstants.OP_ADDR).set(scannerAddress("default"));
        request.get(ModelDescriptionConstants.OP).set("read-resource");
        request.get("attributes-only").set(true);
        return request;
    }

    private ModelNode scannerAddress(String scannerName) {
        ModelNode address = new ModelNode();
        address.add("subsystem", "deployment-scanner");
        address.add("scanner", scannerName);
        return address;
    }
}
