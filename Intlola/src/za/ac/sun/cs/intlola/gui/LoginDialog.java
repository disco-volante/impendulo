//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package za.ac.sun.cs.intlola.gui;

import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.util.IntlolaError;

/**
 * LoginDialog is used to get login details from the user for Impendulo.
 * 
 * @author godfried
 * 
 */
public class LoginDialog extends Dialog {
	private String address, password, username;

	private int port;

	private boolean login;

	private final String title;

	private Text usernameField, passwordField, addressField, portField;

	public LoginDialog(final Shell parentShell, final String title,
			final String username, final String address, final int port) {
		super(parentShell);
		this.title = title;
		this.username = username;
		this.address = address;
		this.port = port;
		this.password = "";
	}

	@Override
	public void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	@Override
	public Control createButtonBar(final Composite parent) {
		final Control ret = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Login");
		return ret;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		final Button btnLogin = new Button(comp, SWT.RADIO);
		btnLogin.setText("Login");
		btnLogin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button btnRegister = new Button(comp, SWT.RADIO);
		btnRegister.setText("Register");
		btnRegister.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("Username:");

		usernameField = new Text(comp, SWT.SINGLE);
		usernameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		usernameField.setText(username);

		final Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password:");

		passwordField = new Text(comp, SWT.SINGLE | SWT.PASSWORD);
		passwordField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		passwordField.setText(password);
		passwordField.setFocus();

		final Label addessLabel = new Label(comp, SWT.RIGHT);
		addessLabel.setText("Server address:");

		addressField = new Text(comp, SWT.SINGLE);
		addressField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addressField.setText(address);

		final Label portLabel = new Label(comp, SWT.RIGHT);
		portLabel.setText("Server port:");

		portField = new Text(comp, SWT.SINGLE);
		portField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portField.setText(String.valueOf(port));

		SelectionListener choiceListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				login = btnLogin.getSelection();
				if (login) {
					btnRegister.setSelection(false);
				} else {
					btnLogin.setSelection(false);
				}
			}
		};

		btnLogin.addSelectionListener(choiceListener);
		btnRegister.addSelectionListener(choiceListener);
		btnLogin.setSelection(true);
		login = true;
		return comp;
	}

	public String getAddress() {
		return address;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return username;
	}

	public String getRequest() {
		return login ? Const.LOGIN : Const.REGISTER;
	}

	@Override
	public void okPressed() {
		String msg;
		if ((msg = validString("Password", passwordField.getText().trim())) != null) {
			showError("Invalid password", msg);
		} else if ((msg = validString("Username", usernameField.getText()
				.trim())) != null) {
			showError("Invalid username", msg);
		} else if ((msg = validAddress(addressField.getText().trim())) != null) {
			showError("Invalid address", msg);
		} else if ((msg = validPort(portField.getText().trim())) != null) {
			showError("Invalid port", msg);
		} else {
			username = usernameField.getText().trim();
			password = passwordField.getText().trim();
			port = Integer.parseInt(portField.getText().trim());
			address = addressField.getText().trim();
			super.okPressed();
		}
	}

	public int open(final IntlolaError err) {
		if (!err.equals(IntlolaError.DEFAULT)) {
			showError(err.toString(), err.getDescription());
		}
		return super.open();
	}

	private void showError(final String title, final String msg) {
		MessageDialog.openError(getShell(), title, msg);
	}

	private String validAddress(final String ip) {
		final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		final Pattern ipPattern = Pattern.compile(IP_PATTERN);
		final UrlValidator validator = new UrlValidator(
				UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_ALL_SCHEMES
						+ UrlValidator.ALLOW_LOCAL_URLS);
		if (!validator.isValid("http://" + ip)
				&& !ipPattern.matcher(ip).matches()) {
			return "Invalid address " + ip + ".";
		}
		return null;
	}

	private String validPort(final String port) {
		try {
			int p = Integer.parseInt(portField.getText().trim());
			if (p < 0 || p > 65535) {
				return port + " is not a valid port number.";
			}
		} catch (final NumberFormatException ne) {
			return port + " is not an integer.";
		}
		return null;
	}

	private String validString(final String type, final String arg) {
		if (arg.length() == 0) {
			return type + " too short;";
		} else if (arg.length() > 100) {
			return type + " too long;";
		}
		return null;
	}

	public static void main(String[] args) {
		LoginDialog dlg = new LoginDialog(new Shell(), "a", "b", "localhost",
				3000);
		dlg.open();
	}
}
