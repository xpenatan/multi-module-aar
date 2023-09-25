/*******************************************************************************
 * Copyright 2023 Natan Barbosa - xpenatan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.artifacts.query.ArtifactResolutionQuery
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.artifacts.transform.TransformSpec
import org.gradle.api.artifacts.type.ArtifactTypeContainer
import org.gradle.api.attributes.AttributesSchema
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.internal.metaobject.MethodAccess
import org.gradle.internal.metaobject.MethodMixIn

class AARDependencyHandler(private val handler: DefaultDependencyHandler, private val invoker: InvokeMethod) : DependencyHandler, MethodMixIn, MethodAccess {

    interface InvokeMethod {
        fun invoke(method: MethodAccess, name: String?, vararg arguments: Any?) : DynamicInvokeResult
    }

    override fun hasMethod(name: String?, vararg arguments: Any?): Boolean {
        return handler.additionalMethods.hasMethod(name)
    }

    override fun tryInvokeMethod(name: String?, vararg arguments: Any?): DynamicInvokeResult {
        return invoker.invoke(handler.additionalMethods, name, arguments)
    }

    override fun getAdditionalMethods(): MethodAccess {
        return this
    }

    override fun getExtensions(): ExtensionContainer {
        return handler.extensions
    }

    override fun add(configurationName: String, dependencyNotation: Any): Dependency? {
        return handler.add(configurationName, dependencyNotation)
    }

    override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>): Dependency? {
        return handler.add(configurationName, dependencyNotation, configureClosure)
    }

    override fun <T : Any?, U : ExternalModuleDependency?> addProvider(configurationName: String, dependencyNotation: Provider<T>, configuration: Action<in U>) {
        handler.addProvider(configurationName, dependencyNotation, configuration)
    }

    override fun <T : Any?> addProvider(configurationName: String, dependencyNotation: Provider<T>) {
        handler.addProvider(configurationName, dependencyNotation)
    }

    override fun <T : Any?, U : ExternalModuleDependency?> addProviderConvertible(configurationName: String, dependencyNotation: ProviderConvertible<T>, configuration: Action<in U>) {
        handler.addProviderConvertible(configurationName, dependencyNotation, configuration)
    }

    override fun <T : Any?> addProviderConvertible(configurationName: String, dependencyNotation: ProviderConvertible<T>) {
        handler.addProviderConvertible(configurationName, dependencyNotation)
    }

    override fun create(dependencyNotation: Any): Dependency {
        return handler.create(dependencyNotation)
    }

    override fun create(dependencyNotation: Any, configureClosure: Closure<*>): Dependency {
        return handler.create(dependencyNotation, configureClosure)
    }

    override fun module(notation: Any): Dependency {
        return handler.module(notation)
    }

    override fun module(notation: Any, configureClosure: Closure<*>): Dependency {
        return handler.module(notation, configureClosure)
    }

    override fun project(notation: MutableMap<String, *>): Dependency {
        return handler.project(notation)
    }

    override fun gradleApi(): Dependency {
        return handler.gradleApi()
    }

    override fun gradleTestKit(): Dependency {
        return handler.gradleTestKit()
    }

    override fun localGroovy(): Dependency {
        return handler.localGroovy()
    }

    override fun getConstraints(): DependencyConstraintHandler {
        return handler.constraints
    }

    override fun constraints(configureAction: Action<in DependencyConstraintHandler>) {
        handler.constraints(configureAction)
    }

    override fun getComponents(): ComponentMetadataHandler {
        return handler.components
    }

    override fun components(configureAction: Action<in ComponentMetadataHandler>) {
        handler.components(configureAction)
    }

    override fun getModules(): ComponentModuleMetadataHandler {
        return handler.modules
    }

    override fun modules(configureAction: Action<in ComponentModuleMetadataHandler>) {
        handler.modules(configureAction)
    }

    override fun createArtifactResolutionQuery(): ArtifactResolutionQuery {
        return handler.createArtifactResolutionQuery()
    }

    override fun attributesSchema(configureAction: Action<in AttributesSchema>): AttributesSchema {
        return handler.attributesSchema(configureAction)
    }

    override fun getAttributesSchema(): AttributesSchema {
        return handler.attributesSchema
    }

    override fun getArtifactTypes(): ArtifactTypeContainer {
        return handler.artifactTypes
    }

    override fun artifactTypes(configureAction: Action<in ArtifactTypeContainer>) {
        return handler.artifactTypes(configureAction)
    }

    override fun <T : TransformParameters?> registerTransform(actionType: Class<out TransformAction<T>>, registrationAction: Action<in TransformSpec<T>>) {
        return handler.registerTransform(actionType, registrationAction)
    }

    override fun platform(notation: Any): Dependency {
        return handler.platform(notation)
    }

    override fun platform(notation: Any, configureAction: Action<in Dependency>): Dependency {
        return handler.platform(notation, configureAction)
    }

    override fun enforcedPlatform(notation: Any): Dependency {
        return handler.enforcedPlatform(notation)
    }

    override fun enforcedPlatform(notation: Any, configureAction: Action<in Dependency>): Dependency {
        return handler.enforcedPlatform(notation, configureAction)
    }

    override fun enforcedPlatform(dependencyProvider: Provider<MinimalExternalModuleDependency>): Provider<MinimalExternalModuleDependency> {
        return handler.enforcedPlatform(dependencyProvider)
    }

    override fun testFixtures(notation: Any): Dependency {
        return handler.testFixtures(notation)
    }

    override fun testFixtures(notation: Any, configureAction: Action<in Dependency>): Dependency {
        return handler.testFixtures(notation, configureAction)
    }

    override fun variantOf(dependencyProvider: Provider<MinimalExternalModuleDependency>, variantSpec: Action<in ExternalModuleDependencyVariantSpec>): Provider<MinimalExternalModuleDependency> {
        return handler.variantOf(dependencyProvider, variantSpec)
    }
}