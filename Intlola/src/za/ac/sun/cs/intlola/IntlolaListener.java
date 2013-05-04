package za.ac.sun.cs.intlola;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class IntlolaListener implements IResourceChangeListener {

	public void resourceChanged(final IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IntlolaVisitor());
		} catch (final CoreException e) {
			Intlola.log(e);
		}
	}

}
