package org.jboss.uberfire.poc.client.local;

import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import org.jboss.dmr.client.ModelNode;
import org.jboss.uberfire.poc.client.local.dmr.DMRCallback;
import org.jboss.uberfire.poc.client.local.dmr.DMRRequest;

/**
 * This is the companion Java class of the complaint form as specified by {@link Templated}. It refers to a data field called
 * "app-template" in ComplaintForm.html as its root and gains access to all data fields in the template to add dynamic behavior
 * (e.g. event handlers, data bindings, page transitions).
 *
 * The {@link Page} annotation declares this form as a bookmarkable page that can be transitioned to by other pages of this
 * application. Further the specified role (DefaultPage.class) make this page appear by default when the application is started.
 */
@Page(role = DefaultPage.class)
@Templated("DMRForm.html#dmr-template")
public class DMRForm extends Composite {

    @Inject
    @DataField
    private TextArea text;
    @Inject
    @DataField
    private Button submit;

    /**
     * This method is registered as an event handler for click events on the submit button of the complaint form.
     *
     * @param e the click event.
     */
    @EventHandler("submit")
    private void onSubmit(ClickEvent e) {
        System.out.println("Submit my DMR request!!!");
        sendNameRequest();
        System.out.println("Submitted my DMR Request!!!");
    }

    private void sendNameRequest() {
        DMRRequest.sendRequest(nameRequest(), new DMRCallback() {
            public void dmrResponse(ModelNode responseNode) {
                text.setText(responseNode.toString());
            }
        });
        /*RequestBuilder rb = makeRequest(nameRequest());

         try {
         rb.send();
         } catch (Exception e) {
         e.printStackTrace();
         text.setText(e.getMessage());
         }*/
    }

 /*   private RequestBuilder makeRequest(ModelNode requestData) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "http://127.0.0.1:9990/management");
        //RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + "index.jsp");
        rb.setHeader("Content-type", "application/dmr-encoded");
        rb.setHeader("Accept", "application/dmr-encoded");
        rb.setIncludeCredentials(true);
        rb.setRequestData(requestData.toBase64String());
        rb.setCallback(new RequestCallback() {
            public void onResponseReceived(Request request, Response response) {
                ModelNode responseNode = ModelNode.fromBase64(response.getText());
                text.setText(responseNode.toString());
            }

            public void onError(Request request, Throwable e) {
                e.printStackTrace();
                text.setText(e.getMessage());
            }
        });

        return rb;
    } */

    private ModelNode nameRequest() {
        ModelNode node = new ModelNode();
        node.get("address").setEmptyList();
        node.get("operation").set("read-attribute");
        //node.get("operation").set("read-resource");
        node.get("name").set("name");
        return node;
    }
}