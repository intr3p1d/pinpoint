/*
 * Copyright 2021 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.jdk.http;

import com.navercorp.pinpoint.bootstrap.plugin.response.ResponseAdaptor;
import com.navercorp.pinpoint.common.util.MapUtils;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yjqg6666
 */
public class JdkHttpClientResponseAdaptor implements ResponseAdaptor<HttpURLConnection> {
    @Override
    public boolean containsHeader(HttpURLConnection response, String name) {
        return response.getHeaderField(name) != null;
    }

    @Override
    public void setHeader(HttpURLConnection response, String name, String value) {
    }

    @Override
    public void addHeader(HttpURLConnection response, String name, String value) {
    }

    @Override
    public String getHeader(HttpURLConnection response, String name) {
        return response.getHeaderField(name);
    }

    /**
     * return the last header value if set
     */
    @Override
    public Collection<String> getHeaders(HttpURLConnection response, String name) {
        final Map<String, List<String>> headerFields = response.getHeaderFields();
        if (MapUtils.isEmpty(headerFields)) {
            return Collections.emptyList();
        }
        return headerFields.get(name);
    }

    @Override
    public Collection<String> getHeaderNames(HttpURLConnection response) {
        final Map<String, List<String>> headerFields = response.getHeaderFields();
        if (MapUtils.isEmpty(headerFields)) {
            return Collections.emptyList();
        }
        return headerFields.keySet();
    }
}
