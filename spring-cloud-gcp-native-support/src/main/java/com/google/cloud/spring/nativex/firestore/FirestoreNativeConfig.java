/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.nativex.firestore;

import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreEmulatorAutoConfiguration;
import com.google.cloud.spring.autoconfigure.firestore.FirestoreRepositoriesAutoConfiguration;
import com.google.cloud.spring.data.firestore.SimpleFirestoreReactiveRepository;
import com.google.cloud.spring.data.firestore.repository.support.FirestoreRepositoryFactoryBean;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * Native hints for {@link FirestoreRepositoriesAutoConfiguration}. Inspired by <code>
 * org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesHints</code>
 *
 * @see <a
 *     href="https://github.com/spring-projects-experimental/spring-native/blob/e659ad5488418b77492a04e028142daf02f7f3ef/spring-native-configuration/src/main/java/org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesHints.java">JpaRepositoriesHints</a>
 */
@NativeHint(
    trigger = FirestoreRepositoriesAutoConfiguration.class,
    types =
        @TypeHint(
            types = {
              FirestoreRepositoryFactoryBean.class,
              SimpleFirestoreReactiveRepository.class,
              GcpDatastoreEmulatorAutoConfiguration.class
            },
            typeNames = {"com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext"},
            access =
                AccessBits.CLASS
                    | AccessBits.DECLARED_METHODS
                    | AccessBits.DECLARED_CONSTRUCTORS
                    | AccessBits.RESOURCE),
    jdkProxies =
        @JdkProxyHint(
            typeNames = {
              "com.google.cloud.spring.data.firestore.FirestoreReactiveRepository",
              "org.springframework.aop.SpringProxy",
              "org.springframework.aop.framework.Advised",
              "org.springframework.core.DecoratingProxy"
            }))
public class FirestoreNativeConfig implements NativeConfiguration {}
