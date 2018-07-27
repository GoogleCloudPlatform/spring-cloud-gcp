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

package org.springframework.cloud.gcp.data.datastore.repository.support;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.cloud.gcp.data.datastore.core.mapping.DatastoreDataException;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

/**
 * Implementation of {@link DatastoreRepository}.
 *
 * @author Chengyuan Zhao
 *
 * @since 1.1
 */
public class SimpleDatastoreRepository<T, ID> implements DatastoreRepository<T, ID> {

	private final DatastoreTemplate datastoreTemplate;

	private final Class<T> entityType;

	public SimpleDatastoreRepository(DatastoreTemplate datastoreTemplate,
			Class<T> entityType) {
		Assert.notNull(datastoreTemplate, "A non-null DatastoreTemplate is required.");
		Assert.notNull(entityType, "A non-null entity type is required.");
		this.datastoreTemplate = datastoreTemplate;
		this.entityType = entityType;
	}

	@Override
	public <A> A performTransaction(Function<DatastoreRepository<T, ID>, A> operations) {
		throw new DatastoreDataException(
				"Running a function as a transaction is not yet supported.");
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		throw new DatastoreDataException("Sorting findAll is not yet supported.");
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		throw new DatastoreDataException("Pageable findAll is not yet supported.");
	}

	@Override
	public <S extends T> S save(S entity) {
		return this.datastoreTemplate.save(entity);
	}

	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		return this.datastoreTemplate.saveAll(entities);
	}

	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(this.datastoreTemplate.findById(id, this.entityType));
	}

	@Override
	public boolean existsById(ID id) {
		return this.datastoreTemplate.existsById(id, this.entityType);
	}

	@Override
	public Iterable<T> findAll() {
		return this.datastoreTemplate.findAll(this.entityType);
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		return this.datastoreTemplate.findAllById(ids, this.entityType);
	}

	@Override
	public long count() {
		return this.datastoreTemplate.count(this.entityType);
	}

	@Override
	public void deleteById(ID id) {
		this.datastoreTemplate.deleteById(id, this.entityType);
	}

	@Override
	public void delete(T entity) {
		this.datastoreTemplate.delete(entity);
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		this.datastoreTemplate.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		this.datastoreTemplate.deleteAll(this.entityType);
	}
}
