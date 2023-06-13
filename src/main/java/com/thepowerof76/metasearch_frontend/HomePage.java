package com.thepowerof76.metasearch_frontend;

import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	@Override
	protected void onInitialize() {
		super.onInitialize();
		visitChildren((component, visit) -> {
			if(!component.isStateless()) {
				System.out.println("Component " + component.getId() + " is not stateless");
			}
		});
	}
	public HomePage(final PageParameters parameters) {
		super(parameters);
		setVersioned(false);
		System.out.println(isStateless());

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));
		TextField<String> query_field = new TextField<>("q_field", Model.of(""));
		query_field.setRequired(true);
		StatelessForm<Void> query_form = new StatelessForm<>("query_form") {
			@Override
			protected void onSubmit() {
				PageParameters p = new PageParameters();

				p.add("query", query_field.getValue());
				p.add("p", 0);
				setResponsePage(SearchPage.class, p);
			}
		};

		query_form.add(query_field);
		add(query_form);

	}
}
