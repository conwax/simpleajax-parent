package com.conwax.ajax;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.junit.Test;

public class SimpleAjaxFallbackLinkTest extends ConwaxTestCase {
	@Test(expected = MarkupException.class)
	public void onlyAnchorAreaAndLink() {
		tester.startPage(new OnlyAnchorAreaAndLinkPage());
	}

	@SuppressWarnings("serial")
	private static class OnlyAnchorAreaAndLinkPage extends WebPage implements
			IMarkupResourceStreamProvider {
		private OnlyAnchorAreaAndLinkPage() {
			add(new SimpleAjaxFallbackLink("l") {

				@Override
				public void onClick(SimpleAjaxRequestTarget target) {
					// TODO Auto-generated method stub
				}
			});
		}

		@Override
		public IResourceStream getMarkupResourceStream(
				MarkupContainer container, Class<?> containerClass) {
			return new StringResourceStream(
					"<html><body><bla wicket:id='l'>Ajax fallback link</bla></body></html>");
		}
	}
}