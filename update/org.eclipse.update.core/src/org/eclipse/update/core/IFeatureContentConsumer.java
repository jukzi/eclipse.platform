package org.eclipse.update.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Feature content consumer.
 * A feature content consumer is an abstraction of each feature internal
 * packaging mechanism. It allows content to be stored into a feature in
 * a standard way regardless of the packaging mechanism used. Only concrete
 * features that support storing need to implement a content consumer. 
 * The platform implements at least one feature type supporting content
 * consumer. This is the feature type representing a locally-installed
 * feature.
 * <p>
 * A feature content consumer delegates the storage of plug-in and 
 * non-plug-in files to a generic content consumer.
 * </p>
 * <p>
 * Clients may implement this interface. However, in most cases clients 
 * will only use the feature content consumer provided by the feature type(s)
 * implemented by the platform.
 * </p>
 * @see org.eclipse.update.core.IContentConsumer
 * @since 2.0
 */
public interface IFeatureContentConsumer {

	/**
	 * Store a feature file.
	 * Note that only the feature definition files should be stored using
	 * this method. Plug-in files and non-plug-in data files should be
	 * stored using the content consumers corresponding to their respective
	 * entries.
	 * 
	 * @see #open(IPluginEntry)
	 * @see #open(INonPluginEntry)	 
	 * @param ContentReference content reference to feature file
	 * @param IProgressMonitor progress monitor, can be <code>null</code>
	 * @exception CoreException
	 * @since 2.0 
	 */
	public void store(ContentReference contentReference, IProgressMonitor monitor)
		throws CoreException;

	/**
	 * Opens a generic content consumer for the specified plug-in entry.
	 * Plug-in files corresponding to this entry should be stored
	 * using this content consumer.
	 * 
	 * @param nonPluginEntry non-plug-in entry
	 * @return generic content consumer for the entry
	 * @exception CoreException
	 * @since 2.0 
	 */

	public IContentConsumer open(IPluginEntry pluginEntry) throws CoreException;

	/**
	 * Opens a generic content consumer for the specified non-plug-in entry.
	 * Non-plug-in files corresponding to this entry should be stored
	 * using this content consumer.
	 * 
	 * @param nonPluginEntry non-plug-in entry
	 * @return generic content consumer for the entry
	 * @exception CoreException
	 * @since 2.0 
	 */
	public IContentConsumer open(INonPluginEntry nonPluginEntry)
		throws CoreException;

	/**
	 * Opens a generic content consumer for the specified feature reference.
	 * Feature reference files corresponding to this entry should be stored
	 * using this feature content consumer.
	 * 
	 * @param featureReference feature reference.
	 * @return feature content consumer for the feature reference
	 * @exception CoreException
	 * @since 2.0 
	 */
	public IFeatureContentConsumer open(IFeatureReference featureReference)
		throws CoreException;

	/**
	 * Closes this content consumer. This indicates a successful completion
	 * of the store operations. The content consumer commits any changes
	 * made by this consumer.
	 * 
	 * @return reference to the newly populated feature
	 * @exception CoreException
	 * @since 2.0 
	 */
	public IFeatureReference close() throws CoreException;

	/**
	 * Closes this content consumer, indicating the store operations should
	 * be aborted. The content consumer attempts to back out any changes
	 * made by this content consumer.
	 * 
	 * @exception CoreException
	 * @since 2.0 
	 */

	public void abort() throws CoreException;

	/**
	 * Sets the feature for this content consumer.
	 * In general, this method should only be called as part of
	 * feature creation. Once set, the feature should not be reset.
	 * 
	 * @param feature feature for this content consumer
	 * @since 2.0
	 */
	public void setFeature(IFeature feature);

	/**
	 * Returns the feature for this content consumer.
	 *
	 * @return the feature for this content consumer
	 * @since 2.0
	 */
	public IFeature getFeature();

	/**
	 * Returns the feature content consumer that opened
	 * this feature content consumer, or <code>null</code>
	 * if this feature content consumer is a root feature
	 * content consumer.
	 *
	 * @return the parent feature content consumer, or null.
	 * @since 2.0
	 */
	public IFeatureContentConsumer getParent();

	/**
	 * Returns the feature content consumers that
	 * this feature content consumer opened
	 *
	 * @return an array of feature content consumer, or en empty array.
	 * @since 2.0
	 */
	public IFeatureContentConsumer[] getChildren();
}