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

import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreEntityConverter;
import org.springframework.cloud.gcp.data.datastore.core.mapping.DatastoreDataException;
import org.springframework.cloud.gcp.data.datastore.core.mapping.DatastoreMappingContext;
import org.springframework.data.annotation.Id;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Chengyuan Zhao
 */
public class DatastoreTemplateTests {

	private final Datastore datastore = mock(Datastore.class);

	private final DatastoreEntityConverter datastoreEntityConverter = mock(
			DatastoreEntityConverter.class);

	private DatastoreTemplate datastoreTemplate;

	private Key createFakeKey() {
		return new KeyFactory("project").setKind("kind").newKey("key");
	}

	@Before
	public void setup() {
		this.datastoreTemplate = new DatastoreTemplate(this.datastore,
				this.datastoreEntityConverter, new DatastoreMappingContext());
	}

	@Test
	public void findByIdTest() {
		Key key1 = createFakeKey();
		Object ob1 = new Object();
		Entity e1 = Entity.newBuilder(createFakeKey()).build();
		when(this.datastore.get(ArgumentMatchers.<Key>any())).thenReturn(e1);
		when(this.datastoreEntityConverter.read(eq(Object.class), any())).thenReturn(ob1);

		assertEquals(ob1, this.datastoreTemplate.findById(key1, Object.class));
	}

	@Test
	public void findAllByIdTest() {
		Key key1 = createFakeKey();
		Key key2 = createFakeKey();
		List<Key> keys = ImmutableList.of(key1, key2);
		Object ob1 = new Object();
		Object ob2 = new Object();
		Entity e1 = Entity.newBuilder(createFakeKey()).build();
		Entity e2 = Entity.newBuilder(createFakeKey()).build();
		when(this.datastoreEntityConverter.read(eq(Object.class), any()))
				.thenAnswer(invocation -> {
					Object ret;
					if (invocation.getArgument(1) == e1) {
						ret = ob1;
					}
					else {
						ret = ob2;
					}
					return ret;
				});

		when(this.datastore.get(ArgumentMatchers.<Key[]>any()))
				.thenReturn(ImmutableList.of(e1, e2).iterator());
		assertThat(this.datastoreTemplate.findAllById(keys, Object.class),
				contains(ob1, ob2));
	}

	@Test
	public void saveTest() {
		DatastoreTemplate spy = spy(this.datastoreTemplate);
		Object object = new Object();
		Entity entity = Entity.newBuilder(createFakeKey()).build();
		Key key = createFakeKey();
		doReturn(key).when(spy).getKey(same(object));
		spy.save(object);
		verify(this.datastore, times(1)).put(eq(entity));
		verify(this.datastoreEntityConverter, times(1)).write(same(object), notNull());
	}

	@Test
	public void findAllTest() {
		Object ob1 = new Object();
		Object ob2 = new Object();
		Entity e1 = Entity.newBuilder(createFakeKey()).build();
		Entity e2 = Entity.newBuilder(createFakeKey()).build();
		this.datastoreTemplate.findAll(TestEntity.class);
		when(this.datastoreEntityConverter.read(eq(TestEntity.class), any()))
				.thenAnswer(invocation -> {
					Object ret;
					if (invocation.getArgument(1) == e1) {
						ret = ob1;
					}
					else {
						ret = ob2;
					}
					return ret;
				});

		QueryResults queryResults = mock(QueryResults.class);
		doAnswer(invocation -> {
			ImmutableList.of(e1, e2).iterator()
					.forEachRemaining(invocation.getArgument(0));
			return null;
		}).when(queryResults).forEachRemaining(any());
		when(this.datastore.run(
				eq(Query.newEntityQueryBuilder().setKind("custom_test_kind").build())))
						.thenReturn(queryResults);

		assertThat(this.datastoreTemplate.findAll(TestEntity.class), contains(ob1, ob2));
	}

	@Test
	public void countTest() {
		DatastoreTemplate spy = spy(this.datastoreTemplate);
		doReturn(ImmutableList.of(new Object(), new Object(), new Object())).when(spy)
				.findAll(eq(Object.class));
		assertEquals(3, spy.count(Object.class));
	}

	@Test
	public void existsByIdTest() {
		DatastoreTemplate spy = spy(this.datastoreTemplate);
		Key key1 = createFakeKey();
		Key key2 = createFakeKey();
		doReturn(new Object()).when(spy).findById(same(key1), eq(Object.class));
		doReturn(null).when(spy).findById(same(key2), eq(Object.class));
		assertTrue(spy.existsById(key1, Object.class));
		assertFalse(spy.existsById(key2, Object.class));
	}

	@Test
	public void deleteByIdTest() {
		Key key1 = createFakeKey();
		this.datastoreTemplate.deleteById(key1, Object.class);
		verify(this.datastore, times(1)).delete(same(key1));
	}

	@Test
	public void deleteObjectTest() {
		DatastoreTemplate spy = spy(this.datastoreTemplate);
		Object object = new Object();
		Key key = createFakeKey();
		doReturn(key).when(spy).getKey(same(object));
		spy.delete(object);
		verify(this.datastore, times(1)).delete(same(key));
	}

	@Test
	public void deleteAllTest() {
		DatastoreTemplate spy = spy(this.datastoreTemplate);
		Object object = new Object();
		Key key = createFakeKey();
		doReturn(key).when(spy).getKey(same(object));
		doReturn(ImmutableList.of(object, object)).when(spy).findAll(eq(Object.class));
		spy.deleteAll(Object.class);
		verify(this.datastore, times(1)).delete(same(key), same(key));
	}

	@Test
	public void getKeyFromIdKeyTest() {
		when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
		Key key = createFakeKey();
		assertSame(key, this.datastoreTemplate.getKeyFromId(key, Object.class));
	}

	@Test
	public void getKeyFromIdStringTest() {
		when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
		assertEquals(new KeyFactory("p").setKind("custom_test_kind").newKey("key"),
				this.datastoreTemplate.getKeyFromId("key", TestEntity.class));
	}

	@Test(expected = DatastoreDataException.class)
	public void getKeyFromIdExceptionTest() {
		when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
		this.datastoreTemplate.getKeyFromId(true, TestEntity.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullIdTest() {
		this.datastoreTemplate.getKey(new TestEntity());
	}

	@Test
	public void getKeyTest() {
		when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
		TestEntity testEntity = new TestEntity();
		testEntity.id = "testkey";
		assertEquals(new KeyFactory("p").setKind("custom_test_kind").newKey("testkey"),
				this.datastoreTemplate.getKey(testEntity));
	}

	@Test(expected = DatastoreDataException.class)
	public void getKeyNoIdTest() {
		this.datastoreTemplate.getKey(new TestEntityNoId());
	}

	@org.springframework.cloud.gcp.data.datastore.core.mapping.Entity(name = "custom_test_kind")
	private static class TestEntity {
		@Id
		String id;
	}

	@org.springframework.cloud.gcp.data.datastore.core.mapping.Entity(name = "custom_test_kind")
	private static class TestEntityNoId {
	}

}
