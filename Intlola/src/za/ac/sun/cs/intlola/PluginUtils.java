package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class PluginUtils {
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Intlola.PLUGIN_ID,
				path);
	}

	public static IProject getSelectedProject(final ExecutionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		for (final Object element : selection.toList()) {
			if (element instanceof IProject) {
				return (IProject) element;
			}
			if (element instanceof IAdaptable) {
				final IAdaptable adaptable = (IAdaptable) element;
				final Object adapter = adaptable.getAdapter(IProject.class);
				return (IProject) adapter;
			}
		}
		return null;
	}

	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	/**
	 * Recursively marks an {@link IContainer}'s contents as changed.
	 * @param container
	 */
	public static void touchAll(IContainer container) {
		try {
			for (IResource resource : container.members()) {
				if(resource.getFullPath().lastSegment().contains("test")){
					continue;
				}
				resource.touch(null);
				if (resource instanceof IContainer) {
					touchAll((IContainer) resource);
				} 
			}
		} catch (CoreException e) {
			Intlola.log(e);
		}
	}
}
