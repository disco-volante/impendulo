package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Intlola extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "za.ac.sun.cs.goanna";

	public static String STOREPATH_DEFAULT = "store directory";

	public static boolean ACTIVE_DEFAULT = false;

	public static QualifiedName ACTIVE_PROPERTY_KEY = new QualifiedName("cs.goannaPlugin", "active");

	public static QualifiedName STOREPATH_PROPERTY_KEY = new QualifiedName("cs.goannaPlugin", "storepath");

	private static Intlola plugin;

	public static Socket requestSocket;
	public static BufferedOutputStream out;

	private IResourceChangeListener changeListener = null;

	private ILaunchListener launchListener = null;
	
	private boolean listenersAdded = false;

	private static boolean isTracing = false;

	public Intlola() {
		plugin = this;
		String traceFilter = Platform.getDebugOption("za.ac.sun.cs.Intlola/debug");
		if ((traceFilter != null) && traceFilter.equals("true")) {
			isTracing = true;
		}
	}

	public static void trace(String message) {
		if (isTracing) {
			System.out.println("INTLOLA: " + message);
		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		trace("plugin started");
		if (!listenersAdded) {
			try{
				requestSocket = new Socket("196.168.1.151", 2010);
				trace("Connected to server in port 2010");
				out = new BufferedOutputStream(requestSocket.getOutputStream());
				out.flush();
				changeListener = new IntlolaListener();
				getWorkspace().addResourceChangeListener(changeListener, IResourceChangeEvent.POST_CHANGE);
				launchListener = new IntlolaMonitor();
				DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
				listenersAdded = true;
				trace("listeners started");
			}
			catch(UnknownHostException unknownHost){
				System.err.println("You are trying to connect to an unknown host!");
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		trace("plugin stopped");
	}

	public static Intlola getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	public static boolean getActiveState(IResource resource) {
		try {
			String v = resource.getPersistentProperty(ACTIVE_PROPERTY_KEY);
			if (v != null) {
				return v.equals("True");
			}
		} catch (CoreException e) {
		}
		return ACTIVE_DEFAULT;
	}

	public static void setActiveState(IResource resource, boolean value) {
		try {
			if (value) {
				resource.setPersistentProperty(ACTIVE_PROPERTY_KEY, "True");
			} else {
				resource.setPersistentProperty(ACTIVE_PROPERTY_KEY, "False");
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static String getStorePath(IResource resource) {
		try {
			String v = resource.getPersistentProperty(STOREPATH_PROPERTY_KEY);
			if (v != null) {
				return v;
			}
		} catch (CoreException e) {
		}
		return STOREPATH_DEFAULT;
	}

	public static void setStorePath(IResource resource, String value) {
		try {
			resource.setPersistentProperty(STOREPATH_PROPERTY_KEY, value);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void earlyStartup() {
	}
	
}
