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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.programmerplanet.sshtunnel.model.Tunnel;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EditTunnelDialog extends CustomDialog {

	private Tunnel tunnel;
	private Text localAddressText;
	private Text localPortText;
	private Text remoteAddressText;
	private Text remotePortText;
	private Button localRadioButton;
	private Button remoteRadioButton;

	public EditTunnelDialog(Shell parent, Tunnel tunnel) {
		super(parent);
		this.setText("Tunnel");
		this.tunnel = tunnel;
	}

	protected void initialize(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		Label localAddressLabel = new Label(parent, SWT.RIGHT);
		localAddressLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		localAddressLabel.setText("Local Address:");

		localAddressText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData1.widthHint = 200;
		localAddressText.setLayoutData(gridData1);

		Label localPortLabel = new Label(parent, SWT.RIGHT);
		localPortLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		localPortLabel.setText("Local Port:");

		localPortText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData2.widthHint = 200;
		localPortText.setLayoutData(gridData2);

		Label remoteAddressLabel = new Label(parent, SWT.RIGHT);
		remoteAddressLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		remoteAddressLabel.setText("Remote Address:");

		remoteAddressText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData3.widthHint = 200;
		remoteAddressText.setLayoutData(gridData3);

		Label remotePortLabel = new Label(parent, SWT.RIGHT);
		remotePortLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		remotePortLabel.setText("Remote Port:");

		remotePortText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData1.widthHint = 200;
		remotePortText.setLayoutData(gridData4);

		Label directionLabel = new Label(parent, SWT.RIGHT);
		directionLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		directionLabel.setText("Direction:");

		Composite directionComposite = new Composite(parent, SWT.NONE);
		GridData gridData5 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData5.widthHint = 200;
		directionComposite.setLayoutData(gridData5);
		directionComposite.setLayout(new FillLayout());

		localRadioButton = new Button(directionComposite, SWT.RADIO);
		localRadioButton.setText("Local");

		remoteRadioButton = new Button(directionComposite, SWT.RADIO);
		remoteRadioButton.setText("Remote");

		setLocalAddress(tunnel.getLocalAddress());
		setLocalPort(tunnel.getLocalPort());
		setRemoteAddress(tunnel.getRemoteAddress());
		setRemotePort(tunnel.getRemotePort());
		setLocal(tunnel.getLocal());
	}

	protected void okPressed() {
		tunnel.setLocalAddress(getLocalAddress());
		tunnel.setLocalPort(getLocalPort());
		tunnel.setRemoteAddress(getRemoteAddress());
		tunnel.setRemotePort(getRemotePort());
		tunnel.setLocal(getLocal());
		super.okPressed();
	}

	private String getLocalAddress() {
		return localAddressText.getText();
	}

	private int getLocalPort() {
		return Integer.parseInt(localPortText.getText());
	}

	private String getRemoteAddress() {
		return remoteAddressText.getText();
	}

	private int getRemotePort() {
		return Integer.parseInt(remotePortText.getText());
	}

	private boolean getLocal() {
		return localRadioButton.getSelection();
	}

	private void setLocalAddress(String localAddress) {
		localAddressText.setText(localAddress != null ? localAddress : "");
	}

	private void setLocalPort(int localPort) {
		localPortText.setText(localPort > 0 ? Integer.toString(localPort) : "");
	}

	private void setRemoteAddress(String remoteAddress) {
		remoteAddressText.setText(remoteAddress != null ? remoteAddress : "");
	}

	private void setRemotePort(int remotePort) {
		remotePortText.setText(remotePort > 0 ? Integer.toString(remotePort) : "");
	}

	private void setLocal(boolean local) {
		if (local) {
			localRadioButton.setSelection(true);
		} else {
			remoteRadioButton.setSelection(true);
		}
	}

}
