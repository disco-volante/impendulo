package za.ac.sun.cs.intlola;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import za.ac.sun.cs.intlola.file.FileUtils;

public class IntlolaMonitor implements ILaunchListener {

	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(final ILaunch launch) {
		final ILaunchConfiguration config = launch.getLaunchConfiguration();
		String projectName = "";
		try {
			projectName = config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		} catch (final CoreException e) {
			Intlola.log(e);
		}
		if (projectName != "") {
			final IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (project != null && Intlola.getRecordStatus(project)) {
				final String path = project.getLocation().toString();
				boolean isFile = project.getType() == IResource.FILE;
				try {
					Intlola.getActive().getProcessor()
							.processChanges(path, isFile, FileUtils.LAUNCHED);
				} catch (IOException e) {
					Intlola.log(e, "Could not process launch");
				}
			}
		}
	}

	@Override
	public void launchRemoved(final ILaunch launch) {
	}

}
