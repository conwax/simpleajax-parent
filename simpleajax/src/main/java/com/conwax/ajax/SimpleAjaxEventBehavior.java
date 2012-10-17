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

import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.parser.XmlTag.TagType;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.value.IValueMap;

public abstract class SimpleAjaxEventBehavior extends AbstractAjaxBehavior
{

	private static final long serialVersionUID = 1L;

	private static final String DATA_AJAX_URL = "data-ajax-url";

	private String event;

	/**
	 * @param event
	 * @param simpleAjaxFallbackLink
	 */
	public SimpleAjaxEventBehavior(String event)
	{
		Args.notEmpty(event, "event");

		event = event.toLowerCase();
		if (event.startsWith("on"))
		{
			event = event.substring(2);
		}

		this.event = event;
	}

	/**
	 * 
	 * @return event the event this behavior is attached to
	 */
	public final String getEvent()
	{
		return event;
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		if (tag.getType() != TagType.CLOSE)
		{
			final IValueMap attributes = tag.getAttributes();
			attributes.put(DATA_AJAX_URL, getCallbackUrl());
		}
	}


	@Override
	protected void onComponentRendered()
	{
		getComponent().getResponse().write(
				"<script wicket:id=\"ajaxGet\" type=\"text/javascript\">"
						+ "if(window.XMLHttpRequest){yPos=window.pageYOffset||0;"
						+ "var x,s,l=(s=document.getElementById('"
						+ getComponent().getMarkupId(true)
						+ "'));"
						+ "l.href='#';"
						+ "l."
						+ "on"
						+ event
						+ "=function(){"
						+ "(x=new XMLHttpRequest).open('GET',this.getAttribute('"
						+ DATA_AJAX_URL
						+ "'),false);"
						+ "if(x.overrideMimeType)x.overrideMimeType('text/plain');"
						+ "x.send(null);"
						+ "r=x.responseText;"
						+ "re = / id=\"(\\w+)\"/;"
						+ "rID = re.exec(r)[1];"
						+ "t=document.getElementById(rID);"
						+ "tmpDiv = document.createElement('div');"
						+ "tmpDiv.innerHTML = r;"
						+ "t.parentNode.replaceChild(tmpDiv.firstChild,t);"
						+ "if(window.yPos)window.scrollTo(0,yPos);}" + "}" + "</script>");
	}

	public void onRequest()
	{
		RequestCycle requestCycle = RequestCycle.get();
		SimpleAjaxRequestTarget target = new SimpleAjaxRequestTarget();
		requestCycle.scheduleRequestHandlerAfterCurrent(target);
		onEvent(target);
	}

	/**
	 * Listener method for the ajax event
	 * 
	 * @param target
	 *      the current request handler
	 */
	protected abstract void onEvent(final SimpleAjaxRequestTarget target);
}