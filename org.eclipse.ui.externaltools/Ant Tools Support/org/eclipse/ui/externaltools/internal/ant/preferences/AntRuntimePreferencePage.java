package org.eclipse.ui.externaltools.internal.ant.preferences;

/**********************************************************************
Copyright (c) 2002 IBM Corp. and others. All rights reserved.
This file is made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
 
Contributors:
**********************************************************************/

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.Property;
import org.eclipse.ant.core.Task;
import org.eclipse.ant.core.Type;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;
import org.eclipse.ui.externaltools.internal.model.IHelpContextIds;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * Ant preference page to set the classpath, tasks, and types and properties.
 */
public class AntRuntimePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private AntClasspathPage classpathPage;
	private AntTasksPage tasksPage;
	private AntTypesPage typesPage;
	private AntPropertiesPage propertiesPage;
	
	/**
	 * Creates the preference page
	 */
	public AntRuntimePreferencePage() {
		setDescription(AntPreferencesMessages.getString("AntPreferencePage.description")); //$NON-NLS-1$
		setPreferenceStore(ExternalToolsPlugin.getDefault().getPreferenceStore());
	}
	
	/* (non-Javadoc)
	 * Method declared on IWorkbenchPreferencePage.
	 */
	public void init(IWorkbench workbench) {
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	protected Control createContents(Composite parent) {
		WorkbenchHelp.setHelp(parent, IHelpContextIds.ANT_PREFERENCE_PAGE);

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new TabFolderLayout());	
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.setFont(parent.getFont());

		classpathPage = new AntClasspathPage(this);
		classpathPage.createTabItem(folder);
		tasksPage = new AntTasksPage(this);
		tasksPage.createTabItem(folder);
		typesPage = new AntTypesPage(this);
		typesPage.createTabItem(folder);

		propertiesPage= new AntPropertiesPage(this);
		propertiesPage.createTabItem(folder);
	
		tasksPage.initialize();
		typesPage.initialize();
		classpathPage.initialize();
		propertiesPage.initialize();

		return folder;
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	protected void performDefaults() {
		super.performDefaults();
		
		AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
		tasksPage.setInput(Arrays.asList(prefs.getCustomTasks()));
		typesPage.setInput(Arrays.asList(prefs.getCustomTypes()));
		classpathPage.performDefaults();
		propertiesPage.performDefaults();
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	public boolean performOk() {
		AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
		
		List contents = classpathPage.getContents();
		if (contents != null && !contents.isEmpty()) {
			URL[] urls = (URL[]) contents.toArray(new URL[contents.size()]);
			prefs.setAntURLs(urls);
		}
		
		contents = classpathPage.getUserURLs();
		if (contents != null && !contents.isEmpty()) {
			URL[] urls = (URL[]) contents.toArray(new URL[contents.size()]);
			prefs.setCustomURLs(urls);
		}
		
		String antHome= classpathPage.getAntHome();
		prefs.setAntHome(antHome);
		
		contents = tasksPage.getContents();
		if (contents != null && !contents.isEmpty()) {
			Task[] tasks = (Task[]) contents.toArray(new Task[contents.size()]);
			prefs.setCustomTasks(tasks);
		}
		
		contents = typesPage.getContents();
		if (contents != null && !contents.isEmpty()) {
			Type[] types = (Type[]) contents.toArray(new Type[contents.size()]);
			prefs.setCustomTypes(types);
		}
		
		contents = propertiesPage.getContents();
		if (contents != null && !contents.isEmpty()) {
			Property[] properties = (Property[]) contents.toArray(new Property[contents.size()]);
			prefs.setCustomProperties(properties);
		}
		
		String[] files = propertiesPage.getPropertyFiles();
		prefs.setCustomPropertyFiles(files);
		
		prefs.updatePluginPreferences();
		return super.performOk();
	}
	
	/**
	 * Sets the <code>GridData</code> on the specified button to
	 * be one that is spaced for the current dialog page units.
	 * 
	 * @param button the button to set the <code>GridData</code>
	 * @return the <code>GridData</code> set on the specified button
	 */
	/*package*/ GridData setButtonGridData(Button button, int style) {
		GridData data = new GridData(style);
		data.heightHint = convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return data;
	}
	
	protected List getLibraryURLs() {
		List urls= new ArrayList();
		urls.addAll(classpathPage.getContents());
		urls.addAll(classpathPage.getUserURLs());
		return urls;
	}
}