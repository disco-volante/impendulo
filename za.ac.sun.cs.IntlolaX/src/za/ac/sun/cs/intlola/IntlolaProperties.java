package za.ac.sun.cs.intlola;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class IntlolaProperties extends PropertyPage implements IWorkbenchPropertyPage {

	private static final int TEXT_FIELD_WIDTH = 50;

	private Button activeButton;

	private Text storePath;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public IntlolaProperties() {
		super();
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout g = new GridLayout();
		c.setLayout(g);

		activeButton = new Button(c, SWT.CHECK);
		activeButton.setText("Activate the monitor service");
		activeButton.setSelection(Intlola.getActiveState((IResource) getElement()));
		
		storePath = new Text(c, SWT.SINGLE | SWT.BORDER);
		GridData d = new GridData();
		d.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		storePath.setLayoutData(d);
		storePath.setText(Intlola.getStorePath((IResource) getElement()));

		return c;
	}

	protected void performDefaults() {
		activeButton.setSelection(Intlola.ACTIVE_DEFAULT);
		storePath.setText(Intlola.STOREPATH_DEFAULT);
	}
	
	public boolean performOk() {
		Intlola.setActiveState((IResource) getElement(), activeButton.getSelection());
		Intlola.setStorePath((IResource) getElement(), storePath.getText());
		Intlola.trace("properties updated");
		return super.performOk();
	}

}