/*
 * Copyright 2023 NAVER Corp.
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
package com.navercorp.pinpoint.channel;

import java.util.Objects;

/**
 * @author youngjin.kim2
 *
 * A pair for registration of ChannelProvider.
 *
 * @see ChannelProviderRepository
 */
public class ChannelProviderRegistry {

    private final String scheme;
    private final ChannelProvider provider;

    private ChannelProviderRegistry(String scheme, ChannelProvider provider) {
        this.scheme = Objects.requireNonNull(scheme, "scheme");
        this.provider = Objects.requireNonNull(provider, "provider");
    }

    public static ChannelProviderRegistry of(String scheme, ChannelProvider provider) {
        return new ChannelProviderRegistry(scheme, provider);
    }

    public String getScheme() {
        return scheme;
    }

    public ChannelProvider getProvider() {
        return provider;
    }

}
