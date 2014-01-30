package org.jboss.uberfire.dmr.poc.client.local;

import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.uberfire.dmr.poc.client.local.tree.DMRTreeModel;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;

@Page(role = DefaultPage.class)
public class DMRTreePage extends Composite {

  private CellTree tree = new CellTree(new DMRTreeModel(), "");

  public DMRTreePage() {
    initWidget(tree);
  }
}
