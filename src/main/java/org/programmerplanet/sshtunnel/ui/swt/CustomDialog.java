/*
 * Copyright 2009 Joseph Fifield
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.programmerplanet.sshtunnel.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public abstract class CustomDialog extends Dialog {

	private int result = SWT.CANCEL;
	private Shell shell;

	public CustomDialog(Shell parent) {
		super(parent, SWT.NONE);
	}

	public int open() {
		Shell parent = this.getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());
		shell.setLayout(new GridLayout());
		createContentComposite(shell);
		createButtonBarComposite(shell);
		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void createContentComposite(Composite parent) {
		Composite contentComposite = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		contentComposite.setLayoutData(gridData);

		initialize(contentComposite);
	}

	private void createButtonBarComposite(Composite parent) {
		Composite buttonBarComposite = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		buttonBarComposite.setLayoutData(gridData);

		RowLayout rowLayout = new RowLayout();
		rowLayout.pack = false;
		buttonBarComposite.setLayout(rowLayout);

		Button okButton = new Button(buttonBarComposite, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});

		this.shell.setDefaultButton(okButton);

		Button cancelButton = new Button(buttonBarComposite, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
	}

	protected void okPressed() {
		result = SWT.OK;
		shell.close();
	}

	protected void cancelPressed() {
		result = SWT.CANCEL;
		shell.close();
	}

	protected abstract void initialize(Composite parent);

}