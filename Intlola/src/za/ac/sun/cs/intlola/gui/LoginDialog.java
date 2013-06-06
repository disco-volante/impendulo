package za.ac.sun.cs.intlola.gui;

import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import za.ac.sun.cs.intlola.processing.IntlolaError;

public class LoginDialog extends Dialog {

	private String address;

	private String password;

	private int port;

	private final String title;

	private String username;

	private Text usernameField, passwordField, addressField, portField;

	public LoginDialog(final Shell parentShell, final String title,
			final String username, final String address, final int port) {
		super(parentShell);
		setFields(username, "", address, port);
		this.title = title;

	}

	@Override
	public void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	public void connError(final IntlolaError err) {
		if (err.equals(IntlolaError.CONNECTION)) {
			showError("Connection error", "Could not connect to server on: "
					+ address + ":" + port);
		} else if (err.equals(IntlolaError.LOGIN)) {
			showError("Login error", "Invalid username or password.");
		}
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
		connError(err);
		return super.open();
	}

	public void setFields(final String username, final String password,
			final String address, final int port) {
		this.username = username;
		this.password = password;
		this.address = address;
		this.port = port;
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
			if (Integer.parseInt(portField.getText().trim()) < 0) {
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
		}
		return null;
	}
}
