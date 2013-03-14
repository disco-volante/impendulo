package za.ac.sun.cs.intlola;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class IntlolaListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		Intlola.log(null, "resource changed",event);
		try {
			event.getDelta().accept(new IntlolaVisitor());
		} catch (CoreException e) {
			Intlola.log(e);
		}
	}

}
