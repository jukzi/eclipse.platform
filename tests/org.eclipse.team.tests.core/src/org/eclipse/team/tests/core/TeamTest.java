/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial implementation
 ******************************************************************************/
package org.eclipse.team.tests.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.tests.harness.EclipseWorkspaceTest;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.sync.IRemoteResource;
import org.eclipse.team.internal.core.target.IRemoteTargetResource;
import org.eclipse.team.internal.core.target.Site;
import org.eclipse.team.internal.core.target.TargetManager;
import org.eclipse.team.internal.core.target.TargetProvider;

public class TeamTest extends EclipseWorkspaceTest {
	protected static IProgressMonitor DEFAULT_MONITOR = new NullProgressMonitor();
	protected static final IProgressMonitor DEFAULT_PROGRESS_MONITOR = new NullProgressMonitor();

	Properties properties;

	public TeamTest() {
		super();
	}
	public TeamTest(String name) {
		super(name);
	}
	/**
	* @see TestCase#setUp()
	*/
	protected void setUp() throws Exception {
		super.setUp();
		properties = TargetTestSetup.properties;
	}
	protected IProject getNamedTestProject(String name) throws CoreException {
		IProject target = getWorkspace().getRoot().getProject(name);
		if (!target.exists()) {
			target.create(null);
			target.open(null);
		}
		assertExistsInFileSystem(target);
		return target;
	}
	
	protected IProject getUniqueTestProject(String prefix) throws CoreException {
		// manage and share with the default stream create by this class
		return getNamedTestProject(prefix + "-" + Long.toString(System.currentTimeMillis()));
	}
	
	protected IStatus getTeamTestStatus(int severity) {
		return new Status(severity, "org.eclipse.team.tests.core", 0, "team status", null);
	}
		/**
	 * Retrieves the Site object that the TargetProvider is contained in.
	 * @return Site
	 */
	Site getSite() {
		try {
			URL url = new URL(properties.getProperty("location"));
			return TargetManager.getSite(properties.getProperty("target"), url);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	/**
	 * Creates filesystem 'resources' with the given names and fills them with random text.
	 * @param container An object that can hold the newly created resources.
	 * @param hierarchy A list of files & folder names to use as resources
	 * @param includeContainer A flag that controls whether the container is included in the list of resources.
	 * @return IResource[] An array of resources filled with variable amounts of random text
	 * @throws CoreException
	 */
	public IResource[] buildResources(IContainer container, String[] hierarchy, boolean includeContainer) throws CoreException {
		List resources = new ArrayList(hierarchy.length + 1);
		if (includeContainer)
			resources.add(container);
		resources.addAll(Arrays.asList(buildResources(container, hierarchy)));
		IResource[] result = (IResource[]) resources.toArray(new IResource[resources.size()]);
		ensureExistsInWorkspace(result, true);
		for (int i = 0; i < result.length; i++) {
			if (result[i].getType() == IResource.FILE) // 3786 bytes is the average size of Eclipse Java files!
				 ((IFile) result[i]).setContents(getRandomContents(100), true, false, null);
		}
		return result;
	}
	public IResource[] buildEmptyResources(IContainer container, String[] hierarchy, boolean includeContainer) throws CoreException {
		List resources = new ArrayList(hierarchy.length + 1);
		resources.addAll(Arrays.asList(buildResources(container, hierarchy)));
		if (includeContainer)
			resources.add(container);
		IResource[] result = (IResource[]) resources.toArray(new IResource[resources.size()]);
		ensureExistsInWorkspace(result, true);
		return result;
	}
	/**
	 * Creates an InputStream filled with random text in excess of a specified minimum.
	 * @param sizeAtLeast 	The minimum number of chars to fill the input stream with.
	 * @return InputStream The input stream containing random text.
	 */
	protected static InputStream getRandomContents(int sizeAtLeast) {
		StringBuffer randomStuff = new StringBuffer(sizeAtLeast + 100);
		while (randomStuff.length() < sizeAtLeast) {
			randomStuff.append(getRandomSnippet());
		}
		return new ByteArrayInputStream(randomStuff.toString().getBytes());
	}
	/**
	 * Produces a random chunk of text from a finite collection of pre-written phrases.
	 * @return String Some random words.
	 */
	public static String getRandomSnippet() {
		switch ((int) Math.round(Math.random() * 10)) {
			case 0 :
				return "este e' o meu conteudo (portuguese)";
			case 1 :
				return "Dann brauchen wir aber auch einen deutschen Satz!";
			case 2 :
				return "I'll be back";
			case 3 :
				return "don't worry, be happy";
			case 4 :
				return "there is no imagination for more sentences";
			case 5 :
				return "customize yours";
			case 6 :
				return "foo";
			case 7 :
				return "bar";
			case 8 :
				return "foobar";
			case 9 :
				return "case 9";
			default :
				return "these are my contents";
		}
	}
	public TargetProvider createProvider(IProject project) throws TeamException {
		// Ensure the remote folder exists
		IRemoteTargetResource remote = getSite().getRemoteResource().getFolder(
			new Path(properties.getProperty("test_dir")).append(project.getName()).toString());
		if (! remote.exists(null)) {
			remote.mkdirs(null);
		}
		TargetManager.map(project, getSite(), new Path(properties.getProperty("test_dir")).append(project.getName()));
		TargetProvider target = getProvider(project);
		return target;
	}
	
	public TargetProvider getProvider(IProject project) throws TeamException {
		return TargetManager.getProvider(project);
	}
	
	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.err.println("Testing was rudely interrupted.");
		}
	}
	void assertLocalEqualsRemote(IProject project) throws CoreException, TeamException {
		IProject newProject = getNamedTestProject("equals");
		TargetProvider target = TargetManager.getProvider(project);
		IResource[] localResources = project.members();
		for (int i = 0; i < localResources.length; i++) {
			assertEquals(target.getRemoteResourceFor(localResources[i]), localResources[i]);
		}
	}
	// Assert that the two containers have equal contents
	protected void assertEquals(IRemoteResource container1, IResource container2) throws CoreException, TeamException {
		if (container2.getType() == IResource.FILE) {
			// Ignore .project file
			if (container2.getName().equals(".project"))
				return;
			assertTrue(compareContent(container1.getContents(DEFAULT_MONITOR), ((IFile) container2).getContents()));
		} else {
			IRemoteResource[] remoteResources = container1.members(DEFAULT_MONITOR);
			IResource[] localResources = ((IFolder) container2).members();
			for (int i = 0; i < localResources.length; i++) {
				assertEquals(remoteResources[i], localResources[i]);
			}
		}
	}
	protected IProject createAndPut(String projectPrefix, String[] resourceNames) throws CoreException, TeamException {
		IProject project = getUniqueTestProject(projectPrefix);
		IResource[] resources = buildResources(project, resourceNames, false);
		TargetProvider target = createProvider(project);
		target.put(resources, DEFAULT_MONITOR);
		return project;
	}

	public void appendText(IResource resource, String text, boolean prepend) throws CoreException, IOException {
		IFile file = (IFile) resource;
		InputStream in = file.getContents();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			if (prepend) {
				bos.write(text.getBytes());
			}
			int i;
			while ((i = in.read()) != -1) {
				bos.write(i);
			}
			if (!prepend) {
				bos.write(text.getBytes());
			}
		} finally {
			in.close();
		}
		file.setContents(new ByteArrayInputStream(bos.toByteArray()), false, false, DEFAULT_MONITOR);
	}
	/*
	 * Get the resources for the given resource names
	 */
	public IResource[] getResources(IContainer container, String[] hierarchy) throws CoreException {
		IResource[] resources = new IResource[hierarchy.length];
		for (int i=0;i<resources.length;i++) {
			resources[i] = container.findMember(hierarchy[i]);
			if (resources[i] == null) {
				resources[i] = buildResources(container, new String[] {hierarchy[i]})[0];
			}
		}
		return resources;
	}
	
	// Assert that the two containers have equal contents
	protected void assertEquals(IContainer container1, IContainer container2) throws CoreException {
		assertEquals(container1.getName(), container2.getName());
		List members1 = new ArrayList();
		members1.addAll(Arrays.asList(container1.members()));
		
		List members2 = new ArrayList();
		members2.addAll(Arrays.asList(container2.members()));
		
		assertTrue(members1.size() == members2.size());
		for (int i=0;i<members1.size();i++) {
			IResource member1 = (IResource)members1.get(i);
			IResource member2 = container2.findMember(member1.getName());
			assertNotNull(member2);
			assertEquals(member1, member2);
		}
	}
	
	// Assert that the two files have equal contents
	protected void assertEquals(IFile file1, IFile file2) throws CoreException {
		assertEquals(file1.getName(), file2.getName());
		assertTrue(compareContent(file1.getContents(), file2.getContents()));
	}
	
	// Assert that the two projects have equal contents ignoreing the project name
	// and the .vcm_meta file
	protected void assertEquals(IProject container1, IProject container2) throws CoreException {
		List members1 = new ArrayList();
		members1.addAll(Arrays.asList(container1.members()));
		members1.remove(container1.findMember(".project"));
		
		List members2 = new ArrayList();
		members2.addAll(Arrays.asList(container2.members()));
		members2.remove(container2.findMember(".project"));
		
		assertTrue("Number of children differs for " + container1.getFullPath(), members1.size() == members2.size());
		for (int i=0;i<members1.size();i++) {
			IResource member1 = (IResource)members1.get(i);
			IResource member2 = container2.findMember(member1.getName());
			assertNotNull(member2);
			assertEquals(member1, member2);
		}
	}
	protected void assertEquals(IResource resource1, IResource resource2) throws CoreException {
		assertEquals(resource1.getType(), resource2.getType());
		if (resource1.getType() == IResource.FILE)
			assertEquals((IFile)resource1, (IFile)resource2);
		else 
			assertEquals((IContainer)resource1, (IContainer)resource2);
	}
}
