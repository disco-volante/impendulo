package za.ac.sun.cs.intlola.gui;

import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.core.databinding.validation.ValidationStatus;

import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;

public class LoginDialog extends Dialog {

	private Text				usernameField, passwordField, projectField,
			addressField, portField;

	private SelectionListener	modeListener;

	private String				title, username, password, project, address;
	private int					port;

	private IntlolaMode			mode;

	public LoginDialog(final Shell parentShell, final String title,
			final String username, final String project,
			final IntlolaMode mode, String address, int port) {
		super(parentShell);
		setFields(username, "", project, mode, address, port);
		this.title = title;

	}

	public void setFields(final String username, final String password,
			final String project, final IntlolaMode mode, String address,
			int port) {
		this.username = username;
		this.password = password;
		this.project = project;
		this.mode = mode;
		this.address = address;
		this.port = port;
	}

	@Override
	public void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	@Override
	public Control createButtonBar(Composite parent) {
		Control ret = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Login");
		return ret;
	}

	@Override
	public void okPressed() {
		String msg;
		if ((msg = validString("Password", passwordField.getText().trim())) != null) {
			showError("Invalid password", msg);
		} else if ((msg = validString("Username", usernameField.getText()
				.trim())) != null) {
			showError("Invalid username", msg);
		} else if ((msg = validString("Project", projectField.getText().trim())) != null) {
			showError("Invalid project", msg);
		} else if ((msg = validAddress(addressField.getText().trim())) != null) {
			showError("Invalid address", msg);
		} else if ((msg = validPort(portField.getText().trim())) != null) {
			showError("Invalid port", msg);
		} else {
			username = usernameField.getText().trim();
			project = projectField.getText().trim();
			password = passwordField.getText().trim();
			port = Integer.parseInt(portField.getText().trim());
			address = addressField.getText().trim();
			super.okPressed();
		}
	}

	private void showError(String title, String msg) {
		ErrorDialog.openError(getShell(), title, "",
				ValidationStatus.error(msg));
	}

	private String validString(String type, String arg) {
		if (arg.length() == 0) {
			return type + " too short;";
		}
		return null;
	}

	private String validAddress(String ip) {
		String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern ipPattern = Pattern.compile(IP_PATTERN);
		UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES
				+ UrlValidator.ALLOW_ALL_SCHEMES
				+ UrlValidator.ALLOW_LOCAL_URLS);
		if (!validator.isValid("http://" + ip)
				&& !ipPattern.matcher(ip).matches()) {
			return "Invalid address " + ip + ".";
		}
		return null;
	}

	private String validPort(String port) {
		try {
			if (Integer.parseInt(portField.getText().trim()) < 0) {
				return port + " is not a valid port number.";
			}
		} catch (NumberFormatException ne) {
			return port + " is not an integer.";
		}
		return null;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return username;
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

		final Label projectLabel = new Label(comp, SWT.RIGHT);
		projectLabel.setText("Project:");

		projectField = new Text(comp, SWT.SINGLE);
		projectField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectField.setText(project);

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

		final Label modeLabel = new Label(comp, SWT.RIGHT);
		modeLabel.setText("Select the mode to run Intlola in:");
		new Label(comp, SWT.RIGHT);

		new Label(comp, SWT.RIGHT);
		final Button btnFR = new Button(comp, SWT.RADIO);
		btnFR.setText(IntlolaMode.FILE_REMOTE.getDescription());
		btnFR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(comp, SWT.RIGHT);
		final Button btnAR = new Button(comp, SWT.RADIO);
		btnAR.setText(IntlolaMode.ARCHIVE_REMOTE.getDescription());
		btnAR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(comp, SWT.RIGHT);
		final Button btnAL = new Button(comp, SWT.RADIO);
		btnAL.setText(IntlolaMode.ARCHIVE_LOCAL.getDescription());
		btnAL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		modeListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnFR.getSelection()) {
					btnAR.setSelection(false);
					btnAL.setSelection(false);
					mode = IntlolaMode.FILE_REMOTE;
				} else if (btnAR.getSelection()) {
					btnFR.setSelection(false);
					btnAL.setSelection(false);
					mode = IntlolaMode.ARCHIVE_REMOTE;
				} else if (btnAL.getSelection()) {
					btnFR.setSelection(false);
					btnAR.setSelection(false);
					mode = IntlolaMode.ARCHIVE_LOCAL;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		};

		btnFR.addSelectionListener(modeListener);
		btnAR.addSelectionListener(modeListener);
		btnAL.addSelectionListener(modeListener);

		switch (mode) {
			case FILE_REMOTE:
				btnFR.setSelection(true);
				break;
			case ARCHIVE_LOCAL:
				btnAL.setSelection(true);
				break;
			case ARCHIVE_REMOTE:
				btnAR.setSelection(true);
				break;
			case ARCHIVE_TEST:
				break;
			default:
				break;

		}
		return comp;
	}

	public String getProject() {
		return project;
	}

	public IntlolaMode getMode() {
		return mode;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public void connError(IntlolaError err) {
		if (err.equals(IntlolaError.CONN)) {
			showError("Connection error", "Could not connect to server on: "
					+ address + ":" + port);
		} else if (err.equals(IntlolaError.LOGIN)) {
			showError("Login error", "Invalid username or password.");
		}
	}

	public int open(IntlolaError err) {
		connError(err);
		return super.open();
	}
}
