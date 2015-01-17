/**
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
package io.macgyver.chat.hipchat;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceRegistry;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ning.http.client.AsyncHttpClient;

public class HipChatServiceFactory extends
		io.macgyver.core.service.ServiceFactory<HipChat> {

	@Autowired
	@Qualifier("macAsyncHttpClient")
	AsyncHttpClient client;

	public HipChatServiceFactory() {
		super("hipchat");

	}

	@Override
	protected HipChat doCreateInstance(ServiceDefinition def) {
		HipChat c = new HipChat(client);
		c.setApiKey(def.getProperties().getProperty("apiKey"));
		return c;
	}


}
