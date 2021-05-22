/*
 * Copyright (c) 2021 dzikoysk
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

package org.panda_lang.reposilite.web

import io.javalin.http.Context
import org.panda_lang.reposilite.auth.Session
import org.panda_lang.reposilite.failure.api.ErrorResponse
import org.panda_lang.utilities.commons.function.Result

class ReposiliteContextDsl(
    val ctx: Context,
    val context: ReposiliteContext
) {

    var response: Result<out Any?, ErrorResponse>? = null

    fun authenticated(init: Session.() -> Unit) {
        context.session
            .mapErr { ctx.json(it) }
            .map { init.invoke(it) }
    }

    internal fun handleResult(result: Result<out Any?, ErrorResponse>?) {
        result
            ?.mapErr { ctx.json(it) }
            ?.map { it?.let { ctx.json(it) } }
    }

}

fun context(contextFactory: ReposiliteContextFactory, ctx: Context, init: ReposiliteContextDsl.() -> Unit) {
    contextFactory.create(ctx)
        .mapErr { ctx.json(it) }
        .map { ReposiliteContextDsl(ctx, it) }
        .map {
            init.invoke(it)
            it.handleResult(it.response)
        }
}

fun <V, E, VE> Result<V, E>.mapToError(): Result<VE, E> =
    this.map { null }

fun <V> errorResponse(status: Int, message: String): Result<V, ErrorResponse> =
    Result.error(ErrorResponse(status, message))