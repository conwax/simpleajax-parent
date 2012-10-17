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

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

public abstract class SimpleAjaxLink<T> extends AbstractLink
		implements
			ISimpleAjaxLink,
			IGenericComponent<T>
{
	private static final long serialVersionUID = 1L;

	public SimpleAjaxLink(String id)
	{
		super(id);
	}

	public SimpleAjaxLink(String id, final IModel<T> model)
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
	 * @param tag
	 *            the component tag
	 */
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		if (isLinkEnabled())
		{
			// disable any href attr in markup
			if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link")
					|| tag.getName().equalsIgnoreCase("area"))
			{
				tag.put("href", "#");
			}
		}
		else
		{
			disableLink(tag);
		}
	}

	/**
	 * 
	 * @param target
	 *            ajax target if this linked was invoked using ajax, null
	 *            otherwise
	 */
	public abstract void onClick(final SimpleAjaxRequestTarget target);

	@Override
	@SuppressWarnings("unchecked")
	public final IModel<T> getModel()
	{
		return (IModel<T>)getDefaultModel();
	}

	@Override
	public final void setModel(IModel<T> model)
	{
		setDefaultModel(model);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final T getModelObject()
	{
		return (T)getDefaultModelObject();
	}

	@Override
	public final void setModelObject(T object)
	{
		setDefaultModelObject(object);
	}


}