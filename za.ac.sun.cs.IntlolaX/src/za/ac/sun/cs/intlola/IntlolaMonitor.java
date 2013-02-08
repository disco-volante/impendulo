package za.ac.sun.cs.intlola;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class IntlolaMonitor implements ILaunchListener {

	public void launchAdded(ILaunch launch) {
		// Intlola.trace("launch added");
	}

	public void launchChanged(ILaunch launch) {
		ILaunchConfiguration config = launch.getLaunchConfiguration();
		String projectName = "";
		try {
			projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		} catch (CoreException e) {
		}
		if (projectName != "") {
			IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if ((p != null) && Intlola.getActiveState(p)) {
				IntlolaVisitor.copyOrTouch(p, Intlola.getStorePath(p), IntlolaVisitor.LAUNCHED);
			}
		}
		// Intlola.trace("launch changed");
	}

	public void launchRemoved(ILaunch launch) {
		// Intlola.trace("launch removed");
	}

}
