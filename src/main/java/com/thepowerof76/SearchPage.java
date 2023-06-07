package com.thepowerof76;

import com.thepowerof76.metasearch_backend.util.MetaResult;
import com.thepowerof76.util.ResultGetter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.concurrent.ExecutionException;

public class SearchPage extends WebPage {
    @Override
    protected void onInitialize() {
        super.onInitialize();
        visitChildren((component, visit) -> {
            if(!component.isStateless()) {
                System.out.println("Component " + component.getId() + " is not stateless");
            }
        });
    }
    final ResultGetter r = new ResultGetter();
    public SearchPage(final PageParameters parameters) {
        super(parameters);
        setVersioned(false);
        if(parameters.isEmpty() || String.valueOf(parameters.get("query")).equals("") || String.valueOf(parameters.get("query")).equals(" ")) {
            setResponsePage(HomePage.class);
        }
        TextField<String> query_field = new TextField<>("q_field", Model.of(""));
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
        add(new RepeatingView("results") {
            @Override
            protected void onPopulate() {
                try {
                    for(MetaResult res: r.search(String.valueOf(parameters.get("query")), parameters.get("p").toInt())) {
                        AbstractItem item = new AbstractItem(this.newChildId());

                        this.add(item);
                        item.add(new ExternalLink("link", res.getHref(), res.getTitle()));
                        item.add(new Label("description", res.getDesc()));
                        String engines = "";
                        boolean[] srcList = res.getSources();
                        if(srcList[0]) {
                            engines += "Google ";
                        }
                        if(srcList[1]) {
                            engines += "Bing ";
                        }
                        if(srcList[2]) {
                            engines += "DuckDuckGo ";
                        }
                        if(srcList[3]) {
                            engines += "Archive.org ";
                        }
                        if(srcList[4]) {
                            engines += "BaseSearch ";
                        }
                        if(srcList[5]) {
                            engines += "Wiby ";
                        }
                        item.add(new Label("engines", engines));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Link<Void> prev = new Link<>("prev") {
            @Override
            public void onClick() {
                PageParameters p = new PageParameters();

                p.add("query", parameters.get("query"));
                p.add("p", parameters.get("p").toInt()-1);
                setResponsePage(SearchPage.class, p);
            }

        };
        if(parameters.get("p").toInt() == 0) {
            prev.setEnabled(false);
        }
        add(prev);
        add(new StatelessLink<Void>("next") {
            @Override
            public void onClick() {
                PageParameters p = new PageParameters();

                p.add("query", parameters.get("query"));
                p.add("p", parameters.get("p").toInt()+1);
                setResponsePage(SearchPage.class, p);
            }
        });
    }
}
