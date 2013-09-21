//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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

/**
 * PluginUtils provides some utility methods associated with Eclipse plugin
 * development.
 * 
 * @author godfried
 * 
 */
public class PluginUtils {
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Intlola.PLUGIN_ID,
				path);
	}

	/**
	 * This method retrieves the project selected by the user in the package
	 * explorer.
	 * 
	 * @param event
	 * @return
	 */
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

	/**
	 * This method is used to retrieve the user's current workspace.
	 * 
	 * @return
	 */
	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	/**
	 * Recursively marks an {@link IContainer}'s contents as changed.
	 * 
	 * @param container
	 */
	public static void touchAll(IContainer container) {
		try {
			for (IResource resource : container.members()) {
				if (resource.getFullPath().lastSegment().contains("test")) {
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
