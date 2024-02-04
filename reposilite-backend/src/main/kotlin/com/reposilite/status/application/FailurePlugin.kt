/*
 * Copyright (c) 2023 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reposilite.status.application

import com.reposilite.plugin.api.Plugin
import com.reposilite.plugin.api.ReposilitePlugin
import com.reposilite.plugin.event
import com.reposilite.status.FailureFacade
import com.reposilite.status.infrastructure.FailureHandler
import com.reposilite.web.api.HttpServerInitializationEvent

@Plugin(name = "failure")
internal class FailurePlugin : ReposilitePlugin() {

    override fun initialize(): FailureFacade {
        val failureFacade = FailureComponents(this).failureFacade()

        event { event: HttpServerInitializationEvent ->
            event.config.router.mount {
                it.exception(Exception::class.java, FailureHandler(failureFacade))
            }
        }

        return failureFacade
    }

}
