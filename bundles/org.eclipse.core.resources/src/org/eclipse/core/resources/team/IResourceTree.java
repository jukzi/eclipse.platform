/**********************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.core.resources.team;

import org.eclipse.core.internal.localstore.CoreFileSystemLibrary;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import java.io.File;

/**
 * @since 2.0
 */
public interface IResourceTree {

	// FIXME: make this the same as java.io.File#getLastModified
	// when the file doesn't exist.
	long NULL_TIMESTAMP = 0;

/**
 * Adds the state of the given file to the local history.
 * Does nothing if the file does not exist in the local file system
 * or if the file does not exist in the workspace tree. 
 * 
 * @param file the file
 */
public void addToLocalHistory(IFile file);

/**
 * Declares that the given source file has been successfully moved
 * in the local file system to the given destination. The appropriate changes
 * should be made to the workspace tree. The given timestamp is that
 * of the moved file, as computed by <code>computeTimestamp</code>.
 * If the timestamp is <code>NULL_TIMESTAMP</code> then the 
 * destination file is queried for its timestamp.
 * <p>
 * If the source file does not exist in the workspace tree then no action is taken.
 * If the destination file already exists in the workspace tree then this
 * operation fails.
 * </p>
 * 
 * @param source the source file
 * @param destination the destination file
 * @param timestamp the timestamp or <code>NULL_TIMESTAMP</code>
 * @see computeTimestamp
 */
public void movedFile(IFile source, IFile destination, long timestamp);

/**
 * Declares that the given source folder has been successfully moved
 * in the local file system to the given destination. The appropriate changes
 * should be made to the workspace tree. This operation preserves timestamps.
 * <p>
 * If the source folder does not exist in the workspace tree then no action is taken.
 * If the destination folder already exists in the workspace tree then this operation fails. 
 * </p>
 * <p>
 * This is a <code>IResource.DEPTH_INFINITE</code> operation.
 * </p>
 * 
 * @param source the source folder
 * @param destination the destination folder
 */
public void movedFolderSubtree(IFolder source, IFolder destination);

/**
 * Declares that the contents for the given source project have been successfully moved
 * in the local file system to the given destination. The appropriate changes
 * should be made to the workspace tree. This operation preserves timestamps.
 * <p>
 * If the source project does not exist in the workspace tree then no action is taken.
 * If the destination project already exists in the workspace (and is different than 
 * the source) then this operation fails.
 * </p>
 * <p>
 * This is a <code>IResource.DEPTH_INFINITE</code> operation.
 * </p>
 * 
 * @param source the source project
 * @param destination the description for the destination project
 */
public void movedProjectSubtree(IProject source, IProjectDescription destination);

/**
 * Declares that the given file has been successfully deleted from
 * the local file system and the appropriate changes should be made
 * to the workspace tree. If the file does not exist in the workspace
 * tree no action is taken.
 * 
 * @param file the file
 */
public void deletedFile(IFile file);

/**
 * Declares that the given folder has been successfully deleted from
 * the local file system and the appropriate changes should be made
 * to the workspace tree. If the folder does not exist in the workspace
 * then no action is taken. This is a <code>IResource.DEPTH_INFINITE<code>
 * operation.
 * 
 * @param folder the folder
 */
public void deletedFolder(IFolder folder);

/**
 * Declares that the given project has been successfully deleted from
 * the local file system. The appropriate changes should be made
 * to the workspace tree. This is a <code>IResource.DEPTH_INFINITE</code>
 * operation. If the project does not exist in the workspace tree no action is taken.
 * 
 * @param project the project
 */
public void deletedProject(IProject project);

/**
 * Declares that the operation has failed for the specified reason.
 * This method may be called multiple times to report multiple
 * failures. All reasons will be collected and taken into consideration
 * when failing the operation as a whole.
 * 
 * @param reason the reason the operation failed
 */
public void failed(IStatus reason);	
	
/**
 * Declares that the given folder has been successfully created in the file system.
 * The appropriate changes should be made to the workspace tree.
 * <p>
 * This method creates the destination in the workspace tree so the children of the source
 * folder may be moved individually by the hook. (e.g. ensures the destination parent will exist)
 * </p>
 * <p>
 * In the normal case, the moved is completed by calling <code>endMovedFolder</code>.
 * Otherwise if the moved failed then <code>failed</code> should be called.
 *</p>
 * 
 * @param source the source folder
 * @param destination the destination folder
 */
public void beginMoveFolder(IFolder source, IFolder destination);
/**
 * FIXME: clean up javadoc
 * <p>
 * This is a <code>IResource.DEPTH_ZERO</code> operation.
 * Fixes the node_id from the source node in the workspace tree to ensure the
 * node looks like a move in the resulting resource delta. 
 * Nukes the subtree.
 * Moves markers.
 * Moved properties.
 * </p>
 */
public void endMoveFolder(IFolder source, IFolder destination);

/**
 * Declares that the given project has been successfully created in the file system.
 * The appropriate changes should be made to the workspace tree.
 * <p>
 * This method creates the destination in the workspace tree so the children of the source
 * project may be moved individually by the hook. (e.g. ensures the destination parent will exist)
 * </p>
 * <p>
 * In the normal case, the moved is completed by calling <code>endMovedProject</code>.
 * Otherwise if the moved failed then <code>failed</code> should be called.
 *</p>
 * 
 * @param project the project
 * @param description the project description for the destination
 */
public void beginMoveProject(IProject project, IProjectDescription description);
/**
 * FIXME: detail the description usage
 * 
 * @param project the project
 * @param description the project description
 */
public void endMoveProject(IProject project, IProjectDescription description);

/**
 * Returns whether the given resource and its descendants to the given depth are 
 * considered to be in sync with the local file system. Returns <code>true</code>
 * if the resource does not exist in the workspace tree.
 * 
 * @param resource the resource
 * @param depth the depth (one of <code>IResource.DEPTH_ZERO</code>,
 *   <code>DEPTH_ONE</code>, or <code>DEPTH_INFINITE</code>)
 * @return <code>true</code> if the resource is synchronized or does not exist in 
 *   the workspace resource tree, <code>false</code> otherwise
 */
public boolean isSynchronized(IResource resource, int depth);
	
/**
 * Returns the computed timestamp for the given file in the local file system.
 * Returns the <code>NULL_TIMESTAMP</code> if the file does not
 * exist in the workspace or if its location in the local file system cannot be
 * determined.
 * 
 * [FIXME: ISSUE: what happens if the file is busy?]
 * 
 * @param file the file
 * @return the file system timestamp for the file or <code>NULL_TIMESTAMP</code>
 *   if it could not be computed
 */
public long computeTimestamp(IFile file);

/**
 * Deletes all the files and directories from the given root down (inclusive).
 * Returns false if we could not delete some file or an exception occurred
 * at any point in the deletion. Even if an exception occurs, a best effort is 
 * made to continue deleting.
 * 
 * @param root the root file
 * @return <code>true</code> if the delete was successful and
 *   <code>false</code> otherwise
 */
public boolean deleteInFileSystem(java.io.File root);

/**
 * Delete the given file from the file system and the workspace tree.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to delete the file from both the workspace and the
 * local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param file the file
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardDeleteFile(IFile file, int updateFlags, IProgressMonitor monitor);
	
/**
 * Delete the given folder from the file system and the workspace tree.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to delete the folder (and all its children) from both the 
 * workspace and the local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param folder the folder
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardDeleteFolder(IFolder folder, int updateFlags, IProgressMonitor monitor);
	
/**
 * Delete the given project from the file system and the workspace tree.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to delete the project (and all its members) from both the 
 * workspace and the local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param project the project
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardDeleteProject(IProject project, int updateFlags, IProgressMonitor monitor);
	
/**
 * Move the given file from the source to the destination.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to move the file both in the workspace and on the
 * local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param source the source file
 * @param destination the destination file
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardMoveFile(IFile source, IFile destination, int updateFlags, IProgressMonitor monitor);
	
/**
 * Move the given folder from the source to the destination.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to move the folder (and all its children) both in the workspace 
 * and on the local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param source the source folder
 * @param destination the destination folder
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardMoveFolder(IFolder source, IFolder destination, int updateFlags, IProgressMonitor monitor);
	
/**
 * Move the given project from the source to the destination.
 * This contains the standard behaviour of the platform and performs all the
 * necessary calls to move the project (and all its members) both in the workspace 
 * and on the local file system.
 * <p>
 * This operation is long-running; progress and cancellation are provided
 * by the given progress monitor. 
 * </p>
 * 
 * @param source the source project
 * @param description the description for the destination project
 * @param updateFlags bit-wise or of update flag constants
 * @param monitor a progress monitor, or <code>null</code> if progress
 *    reporting and cancellation are not desired
 */
public void standardMoveProject(IProject source, IProjectDescription description, int updateFlags, IProgressMonitor monitor);
/**
 * FIXME: fix up javadoc
 * Move the contents of the specified file from the source location to the destination location.
 * If the source points to a directory then move that directory and all its contents.
 * 
 * <code>IResource.FORCE</code> is the only valid flag.
 * 
 * @param source
 * @param destination
 * @param updateFlags
 */
public void moveInFileSystem(java.io.File source, java.io.File destination, int updateFlags) throws CoreException;
}
