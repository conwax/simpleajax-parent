/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.conwax.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAjaxRequestTarget implements IRequestHandler
{

	private static final Logger LOG = LoggerFactory.getLogger(SimpleAjaxRequestTarget.class);

	/**
	 * The component instance that will be rendered/replaced.
	 */
	protected Component component;

	public void setComponent(Component component)
	{
		Args.notNull(component, "component");

		if (component.getOutputMarkupId() == false && !(component instanceof Page))
		{
			throw new IllegalArgumentException(
					"cannot update component that does not have setOutputMarkupId property set to true. Component: "
							+ component.toString());
		}
		this.component = component;
	}

	public void respond(IRequestCycle requestCycle)
	{
		final WebResponse response = (WebResponse)requestCycle.getResponse();

		response.reset();
		// Make sure it is not cached by a client
		response.disableCaching();

		if (component.getRenderBodyOnly() == true)
		{
			throw new IllegalStateException(
					"Ajax render cannot be called on component that has setRenderBodyOnly enabled. Component: "
							+ component.toString());
		}

		component.setOutputMarkupId(true);

		// Initialize temporary variables
		final Page page = component.findParent(Page.class);
		if (page == null)
		{
			// dont throw an exception but just ignore this component,
			// somehow
			// it got removed from the page.
			LOG.debug("component: " + component + " with markupid: " + component.getMarkupId()
					+ " not rendered because it was already removed from page");
			return;
		}

		page.startComponentRender(component);

		try
		{
			component.prepareForRender();

			// render any associated headers of the component
			// forget about Header Contribs
			// writeHeaderContribution(response, component);
		}
		catch (RuntimeException e)
		{
			try
			{
				component.afterRender();
			}
			catch (RuntimeException e2)
			{
				// ignore this one could be a result off.
			}
			throw e;
		}

		try
		{
			component.render();
		}
		catch (RuntimeException e)
		{
			response.reset();
			throw e;
		}

		page.endComponentRender(component);

	}

	public void detach(IRequestCycle requestCycle)
	{
		// do nothing
	}
}
