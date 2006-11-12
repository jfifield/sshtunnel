package org.programmerplanet.sshtunnel.ui;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EditTunnelPanel extends JPanel {

	private JTextField localAddressText;
	private JTextField localPortText;
	private JTextField remoteAddressText;
	private JTextField remotePortText;
	private JRadioButton localRadioButton;
	private JRadioButton remoteRadioButton;
	
	public EditTunnelPanel() {
		initialize();
	}

	private void initialize() {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);

		JLabel localAddressLabel = new JLabel("Local Address:", JLabel.TRAILING);
		this.add(localAddressLabel);
		localAddressText = new JTextField(20);
		localAddressLabel.setLabelFor(localAddressText);
		this.add(localAddressText);

		JLabel localPortLabel = new JLabel("Local Port:", JLabel.TRAILING);
		this.add(localPortLabel);
		localPortText = new JTextField(20);
		localPortLabel.setLabelFor(localPortText);
		this.add(localPortText);

		JLabel remoteAddressLabel = new JLabel("Remote Address:", JLabel.TRAILING);
		this.add(remoteAddressLabel);
		remoteAddressText = new JTextField(20);
		remoteAddressLabel.setLabelFor(remoteAddressText);
		this.add(remoteAddressText);

		JLabel remotePortLabel = new JLabel("Remote Port:", JLabel.TRAILING);
		this.add(remotePortLabel);
		remotePortText = new JTextField(20);
		remotePortLabel.setLabelFor(remotePortText);
		this.add(remotePortText);

		JPanel directionPanel = new JPanel(new GridLayout());
		ButtonGroup directionButtonGroup = new ButtonGroup();
		
		localRadioButton = new JRadioButton("Local", true);
		directionPanel.add(localRadioButton);
		directionButtonGroup.add(localRadioButton);

		remoteRadioButton = new JRadioButton("Remote", false);
		directionPanel.add(remoteRadioButton);
		directionButtonGroup.add(remoteRadioButton);
		
		JLabel directionLabel = new JLabel("Direction:", JLabel.TRAILING);
		this.add(directionLabel);
		this.add(directionPanel);
		
		int rows = 5;
		int cols = 2;
		int initX = 6;
		int initY = 6;
		int xPad = 6;
		int yPad = 6;
		SpringUtilities.makeCompactGrid(this, rows, cols, initX, initY, xPad, yPad);
	}

	public String getLocalAddress() {
		return localAddressText.getText();
	}

	public void setLocalAddress(String localAddress) {
		localAddressText.setText(localAddress);
	}

	public int getLocalPort() {
		return Integer.parseInt(localPortText.getText());
	}

	public void setLocalPort(int localPort) {
		localPortText.setText(Integer.toString(localPort));
	}

	public String getRemoteAddress() {
		return remoteAddressText.getText();
	}

	public void setRemoteAddress(String remoteAddress) {
		remoteAddressText.setText(remoteAddress);
	}

	public int getRemotePort() {
		return Integer.parseInt(remotePortText.getText());
	}

	public void setRemotePort(int remotePort) {
		remotePortText.setText(Integer.toString(remotePort));
	}
	
	public boolean getLocal() {
		return localRadioButton.isSelected();
	}
	
	public void setLocal(boolean local) {
		if (local) {
			localRadioButton.setSelected(true);
		}
		else {
			remoteRadioButton.setSelected(true);
		}
	}

}
