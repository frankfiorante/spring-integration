/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.integration.channel;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.dispatcher.BroadcastingDispatcher;
import org.springframework.integration.util.ErrorHandler;
import org.springframework.integration.util.ErrorHandlingTaskExecutor;

/**
 * A channel that sends Messages to each of its subscribers. 
 * 
 * @author Mark Fisher
 */
public class PublishSubscribeChannel extends AbstractSubscribableChannel<BroadcastingDispatcher> implements BeanFactoryAware {

	private volatile TaskExecutor taskExecutor;

	private volatile ErrorHandler errorHandler;


	/**
	 * Create a PublishSubscribeChannel that will use a {@link TaskExecutor}
	 * to publish its Messages. 
	 */
	public PublishSubscribeChannel(TaskExecutor taskExecutor) {
		super(new BroadcastingDispatcher());
		this.taskExecutor = taskExecutor;
	}

	public PublishSubscribeChannel() {
		this(null);
	}


	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setApplySequence(boolean applySequence) {
		this.getDispatcher().setApplySequence(applySequence);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (this.taskExecutor != null) {
			if (!(this.taskExecutor instanceof ErrorHandlingTaskExecutor)) {
				if (this.errorHandler == null) {
					this.errorHandler = new MessagePublishingErrorHandler(new BeanFactoryChannelResolver(beanFactory));
				}
				this.taskExecutor = new ErrorHandlingTaskExecutor(this.taskExecutor, this.errorHandler);
			}
			this.getDispatcher().setTaskExecutor(this.taskExecutor);
		}
	}

}
