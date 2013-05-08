package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
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
		for (Object element : selection.toList()) {
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
}
