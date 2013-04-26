package za.ac.sun.cs.intlola.gui;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LoginDialog extends Dialog {

	private Text usernameField;

	private Text passwordField;

	private String username;

	private String password;

	public LoginDialog(final Shell parentShell, final String username) {
		super(parentShell);
		this.username = username;
	}

	@Override
	public boolean close() {
		username = usernameField.getText().trim();
		password = passwordField.getText().trim();
		return super.close();
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
		usernameLabel.setText("Username: ");

		usernameField = new Text(comp, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		usernameField.setLayoutData(data);
		usernameField.setText(username);

		final Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password: ");

		passwordField = new Text(comp, SWT.SINGLE | SWT.PASSWORD);
		data = new GridData(GridData.FILL_HORIZONTAL);
		passwordField.setLayoutData(data);
		passwordField.setFocus();

		return comp;
	}
}
