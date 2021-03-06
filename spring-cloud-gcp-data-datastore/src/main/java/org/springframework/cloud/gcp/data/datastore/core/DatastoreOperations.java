/*
 *  Copyright 2018 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.cloud.gcp.data.datastore.core;

/**
 * An interface of operations that can be done with Cloud Datastore.
 *
 * @author Chengyuan Zhao
 *
 * @since 1.1
 */
public interface DatastoreOperations {

	/**
	 * Get an entity based on a id.
	 * @param id the id of the entity. If this is actually a {@link com.google.cloud.datastore.Key}
	 * then it will be used. Otherwise it will be attempted to be converted
	 * to an integer or string value and it will be assumed to be a root key value with the
	 * Kind determined by the entityClass.
	 * param.
	 * @param entityClass the type of the entity to get.
	 * @param <T> the class type of the entity.
	 * @return the entity that was found with that id.
	 */
	<T> T findById(Object id, Class<T> entityClass);

	/**
	 * Saves an instance of an object to Cloud Datastore. Behaves as update or insert.
	 * @param instance the instance to save.
	 */
	<T> void save(T instance);

	/**
	 * Delete an entity from Cloud Datastore.
	 * @param id the ID of the entity to delete. If this is actually a
	 * {@link com.google.cloud.datastore.Key}
	 * then it will be used. Otherwise it will be attempted to be converted
	 * to an integer or string value and it will be assumed to be a root key value with the
	 * Kind determined by the entityClass.
	 * @param entityClass the type of the
	 * @param <T> ths entity type
	 */
	<T> void deleteById(Object id, Class<T> entityClass);

	/**
	 * Delete an entity from Cloud Datastore.
	 * @param entity the entity to delete.
	 * @param <T> the entity type
	 */
	<T> void delete(T entity);

	/**
	 * Delete all entities of a given domain type.
	 * @param entityClass the domain type to delete from Cloud Datastore.
	 */
	void deleteAll(Class<?> entityClass);

	/**
	 * Count all occurrences of entities of the given domain type.
	 * @param entityClass the domain type to count.
	 * @return the number of entities of the given type.
	 */
	long count(Class<?> entityClass);

	/**
	 * Find all the entities of the given IDs. If an ID is actually a
	 * {@link com.google.cloud.datastore.Key}
	 * then it will be used. Otherwise it will be attempted to be converted
	 * to an integer or string value and it will be assumed to be a root key value with the
	 * Kind determined by the entityClass.
	 * @param ids the IDs to search.
	 * @param entityClass the domain type of the objects.
	 * @param <T> the type parameter of the domain type.
	 * @return the entities that were found.
	 */
	<T> Iterable<T> findAllById(Iterable<?> ids, Class<T> entityClass);

	/**
	 * Get all the entities of the given domain type.
	 * @param entityClass the domain type to get.
	 * @param <T> the type param of the domain type.
	 * @return the entities that were found.
 	 */
	<T> Iterable<T> findAll(Class<T> entityClass);

	/**
	 * Check if the given ID belongs to an entity in Cloud Datastore. If this is actually a
	 * {@link com.google.cloud.datastore.Key}
	 * then it will be used. Otherwise it will be attempted to be converted
	 * to an integer or string value and it will be assumed to be a root key value with the
	 * Kind determined by the entityClass.
	 * @param id the ID to search for.
	 * @param entityClass the domain type of the entities to search for.
	 * @param <T> the type param of the domain type.
	 * @return true if the given ID refers to an existing entity. False otherwise.
	 */
	<T> boolean existsById(Object id, Class<T> entityClass);
}
