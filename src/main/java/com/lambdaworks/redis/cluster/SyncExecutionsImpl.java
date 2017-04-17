/*
 * Copyright 2011-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lambdaworks.redis.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import com.lambdaworks.redis.cluster.api.sync.Executions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;

/**
 * @author Mark Paluch
 */
class SyncExecutionsImpl<T> implements Executions<T> {

    private Map<RedisClusterNode, T> executions;

    public SyncExecutionsImpl(Map<RedisClusterNode, CompletionStage<T>> executions) throws ExecutionException,
            InterruptedException {

        Map<RedisClusterNode, T> result = new HashMap<>(executions.size(), 1);
        for (Map.Entry<RedisClusterNode, CompletionStage<T>> entry : executions.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toCompletableFuture().get());
        }

        this.executions = result;
    }

    @Override
    public Map<RedisClusterNode, T> asMap() {
        return executions;
    }

    @Override
    public Collection<RedisClusterNode> nodes() {
        return executions.keySet();
    }

    @Override
    public T get(RedisClusterNode redisClusterNode) {
        return executions.get(redisClusterNode);
    }

}