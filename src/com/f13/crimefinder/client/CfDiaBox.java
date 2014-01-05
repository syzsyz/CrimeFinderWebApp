package com.f13.crimefinder.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;


      class CfDiaBox extends DialogBox {


            public CfDiaBox() {
               // Set the dialog box's caption.


               // Enable animation.
               setAnimationEnabled(true);


               // Enable glass background.
               setGlassEnabled(true);


               // DialogBox is a SimplePanel, so you have to set its widget
               // property to whatever you want its contents to be.
               Button ok = new Button("OK");
               ok.addClickHandler(new ClickHandler() {
                  public void onClick(ClickEvent event) {
                	  CfDiaBox.this.hide();
                  }
               });
              


                 VerticalPanel panel = new VerticalPanel();
                 panel.setHeight("100");
                 panel.setWidth("300");
                 panel.setSpacing(10);
                 panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                 panel.add(ok);


                 setWidget(panel);


           
            }}




