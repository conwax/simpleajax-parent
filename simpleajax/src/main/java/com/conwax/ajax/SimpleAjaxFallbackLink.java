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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public abstract class SimpleAjaxFallbackLink<T> extends Link<T> implements ISimpleAjaxLink
{
	private static final long serialVersionUID = 1L;

	public SimpleAjaxFallbackLink(String id)
	{
		super(id);
	}

	public SimpleAjaxFallbackLink(String id, final IModel<T> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();
		add(newSimpleAjaxEventBehavior("click"));
	}

	@SuppressWarnings("serial")
	private SimpleAjaxEventBehavior newSimpleAjaxEventBehavior(String string)
	{
		return new SimpleAjaxEventBehavior(string)
		{
			@Override
			protected void onEvent(SimpleAjaxRequestTarget target)
			{
				onClick(target);
			}
			
			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				// only render handler if link is enabled
				if (isLinkEnabled())
				{
					super.onComponentTag(tag);
				}
			}

			@Override
			protected void onComponentRendered()
			{
				// only render handler if link is enabled
				if (isLinkEnabled())
				{
					super.onComponentRendered();
				}
			}
		};
	}

	/**
	 * 
	 * @see org.apache.wicket.markup.html.link.Link#onClick()
	 */
	@Override
	public final void onClick()
	{
		onClick(null);
	}

	/**
	 * Callback for the onClick event. If ajax failed and this event was
	 * generated via a normal link the target argument will be null
	 * 
	 * @param target
	 *            ajax target if this linked was invoked using ajax, null
	 *            otherwise
	 */
	public abstract void onClick(final SimpleAjaxRequestTarget target);

	/**
	 * Removes any inline 'onclick' attributes set by
	 * Link#onComponentTag(ComponentTag).
	 * 
	 * @param tag
	 *            the component tag
	 */
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		// Ajax links work with JavaScript Event registration
		tag.remove("onclick");

		String tagName = tag.getName();
		if (isLinkEnabled()
				&& !("a".equalsIgnoreCase(tagName) || "area".equalsIgnoreCase(tagName) || "link"
						.equalsIgnoreCase(tagName)))
		{
			String msg = String.format(
					"%s must be used only with <a>, <area> or <link> markup elements. "
							+ "The fallback functionality doesn't work for other markup elements. "
							+ "Component path: %s, markup element: <%s>.",
					SimpleAjaxFallbackLink.class.getSimpleName(), getClassRelativePath(), tagName);
			findMarkupStream().throwMarkupException(msg);
		}
	}
}