package com.f13.crimefinder.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import java.util.ArrayList;

import com.f13.crimefinder.shared.Account;
import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.FlexTable;


/*** Entry point classes define onModuleLoad(). */

public class Crimefinder implements EntryPoint {
	
	ArrayList<Crime> cl = new ArrayList<Crime>();
	MapWidget map;
	

	/**
	 * This is the entry point method.
	 */
	/*** Create a remote service proxy to talk to the server-side Greeting service. */
	private final ServerConnectionAsync serverConnectProxy = GWT.create(ServerConnection.class);
	private String searchString;
	private AsyncCallback<ArrayList<Crime>> getCrimes;
	private AsyncCallback<Account> getUser;
	private AsyncCallback<java.lang.Boolean> addUser;
	private AsyncCallback<java.lang.Boolean> addHistory;
	private AsyncCallback<ArrayList<History>> getHistory;
	private AsyncCallback<java.lang.Boolean> deleteUser;
	public static VerticalPanel loginPanel;
	public static VerticalPanel mapPanel;
	public static VerticalPanel vPanel;
	public static VerticalPanel regPanel;
	public RootPanel rootPanel;
	private TextBox userNameTextBox;
	private TextBox passwordTextBox;
	private TextBox passwordConfirmTextBox;
	private TextBox emailTextBox;
	private TextBox verifyTextBox;
	private Account account = new Account();
	private static InfoWindow info;
	private VerticalPanel verifyPanel;
	

   private double searchDistance;
   private CheckBox listAddressCheckBox;
   private TextBox distanceBox;
   private MultiWordSuggestOracle suggestionOracle = new MultiWordSuggestOracle();
   private final SuggestBox suggestSearchBox = new SuggestBox(suggestionOracle);
  
   final VerticalPanel listAddressPanel =  new VerticalPanel();

    
	/*** This is the entry point method. */
	public void onModuleLoad() {
		
		rootPanel = RootPanel.get();
		
		/**vPanel**/
		initVPanel();
		
		/**LoginPanel**/
		initLoginPanel();
		
		/**MAP**/
		Maps.loadMapsApi("", "2", false, new Runnable() {
		      public void run() {
		    	  map = buildInitialMap();
		    	  vPanel.add(map);
		      }
		});
		
		
		// Put into private class for neatness, but can be reverted back to format like 
		// getUserExample above
		getCrimes=new GetCrimeAsyncCallback();  
		
		//serverConnectProxy.getUser("Newton", getUserExample);;
		//serverConnectProxy.searchCrime("UBC hospital", 3.0 ,getCrimes); // Becomes default
	}
	
    private void initLoginPanel(){
		//for login
		loginPanel = new VerticalPanel();
		Label loginLabel = new Label("Please enter your credentials:");
		loginPanel.add(loginLabel);
		//for login
		loginPanel.add(loginLabel);
		rootPanel.add(loginPanel);

		FlexTable flexTable = new FlexTable();
		loginPanel.add(flexTable);
		flexTable.setSize("211px", "88px");
		
		Label lblUserName = new Label("Username :");
		flexTable.setWidget(0, 0, lblUserName);
		
		userNameTextBox = new TextBox();
		flexTable.setWidget(1, 0, userNameTextBox);
		
		Label lblPass = new Label("Password\r\n:");
		flexTable.setWidget(2, 0, lblPass);
		
		passwordTextBox = new TextBox();
		flexTable.setWidget(3, 0, passwordTextBox);
		
		Button btnLogin = new Button("Sign In");
		btnLogin.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getUser=new GetUserCallback();
				serverConnectProxy.getUser(userNameTextBox.getText(), getUser);
				/** Suggestive search */
				getHistory = new GetHistoryCallback();
				serverConnectProxy.getHistory(userNameTextBox.getText(),getHistory);
			}
		});
		flexTable.setWidget(4, 0, btnLogin);

		Button btnRegister = new Button("Register");
		btnRegister.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rootPanel.remove(loginPanel);
				initRegisterPanel();
			}
		});
		flexTable.setWidget(5, 0, btnRegister);
    }

    private static void displayInfoWindow(Crime c){
    	HTML htmlWidget = new HTML("<h2>Type: "+c.getType()+"</h1>" + "Address: "+c.getFormattedAddress()+"</p>");
    	InfoWindowContent content = new InfoWindowContent(htmlWidget);
    	LatLng crimelatlng = LatLng.newInstance(c.getLat(), c.getLong());
    	info.open(crimelatlng, content);        
     }
    
    private void initVPanel(){
		vPanel = new VerticalPanel(); // Declare vertical panel
		Label titleLabel = new Label("Welcome to Crime Search"); // Create loose label for title
		titleLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		titleLabel.setDirectionEstimator(true);
		Label searchLabel = new Label("Please enter search address:"); // search prompt phrase
		titleLabel.setStyleName("titleLabelStyel"); // Set style of the titleLabel in CSS
		
		// Panel for search box, search button, radius selection
		HorizontalPanel searchPanel = new HorizontalPanel(); 
       // this.searchBox = new TextBox(); // Create searchbox
        this.distanceBox = new TextBox(); // Create distanceBox
        distanceBox.setText("0.5"); //Default 0.5 KM.
        listAddressCheckBox = new CheckBox("List all addresses");
        Button searchButton = new Button("Search"); // Create search button
        //searchPanel.add(this.searchBox); // Add search box to search panel
        searchPanel.add(this.distanceBox); // Add search box to search panel
        searchPanel.add(suggestSearchBox); // Add suggestive search box to search panel
        searchPanel.add(this.distanceBox); // Add distance search box to search panel
        Button logoutButton = new  Button("Log Out");
        logoutButton.addClickHandler(new LogoutClickHandler());
        
        searchPanel.add(searchButton); // Add search button next to search box
        searchPanel.add(this.listAddressCheckBox); // Add search box to search panel
        searchPanel.add(logoutButton);
		//Handles the person entering an address and clicking on search
		searchButton.addClickHandler(new SearchClickHandler ());
		
		vPanel.add(titleLabel); // This adds the title label to the vertical panel
		vPanel.setCellHorizontalAlignment(titleLabel, HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(searchLabel); // This adds the search phrase below the title
		vPanel.add(searchPanel); // This adds the searchPanel below the search phrase
    }

	private MapWidget buildInitialMap() {
	    LatLng vanCity = LatLng.newInstance(49.2500, -123.1000);
	    final MapWidget map = new MapWidget(vanCity ,2 );
	    map.setSize("1024px", "768px");
	    map.setZoomLevel(12);
	    map.setDraggable(true);
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());

	    // Add an info window to highlight a point of interest


	    // Add the map to the HTML host page

        info =  map.getInfoWindow();
		return map;
	}
	
	private void displayOkBox(String string) {	
		CfDiaBox box = new CfDiaBox();
        box.setText(string);
        int left = Window.getClientWidth()/ 2;
        int top = Window.getClientHeight()/ 2;
        box.setPopupPosition(left, top);
        box.show();				
	}

    private void initRegisterPanel(){
    	regPanel = new VerticalPanel(); // Declare vertical panel
		Label regLabel = new Label("Please enter your register information:");
		regPanel.add(regLabel);
		rootPanel.add(regPanel);

		FlexTable flexTable = new FlexTable();
		regPanel.add(flexTable);
		flexTable.setSize("211px", "88px");
		
		Label lblUserName = new Label("Username :");
		flexTable.setWidget(0, 0, lblUserName);
		
		userNameTextBox = new TextBox();
		flexTable.setWidget(1, 0, userNameTextBox);
		
		Label lblPass = new Label("Password\r\n:");
		flexTable.setWidget(2, 0, lblPass);
		
		passwordTextBox = new TextBox();
		flexTable.setWidget(3, 0, passwordTextBox);
		
		Label lblPassConf = new Label("Password Confirmation\r\n:");
		flexTable.setWidget(4, 0, lblPassConf);

		passwordConfirmTextBox = new TextBox();
		flexTable.setWidget(5, 0, passwordConfirmTextBox);

		Label lblEmail = new Label("E-mail Address");
		flexTable.setWidget(6, 0, lblEmail);

		emailTextBox = new TextBox();
		flexTable.setWidget(7, 0, emailTextBox);

		Button btnRegister = new Button("Register");
		btnRegister.addClickHandler(new ClickHandler() {
		
			public void onClick(ClickEvent event) {
				if (emailTextBox.getText().isEmpty()||passwordTextBox.getText().isEmpty()||passwordConfirmTextBox.getText().isEmpty()||userNameTextBox.getText().isEmpty())
				{displayOkBox("Please fill in all information.");}

				else if (!passwordTextBox.getText().equals(passwordConfirmTextBox.getText())){
					displayOkBox("Password doesn't match, pleas enter again.");
				}
				else 
				{
				addUser=new AddUserCallback();
				serverConnectProxy.addUser(userNameTextBox.getText(), passwordTextBox.getText(), emailTextBox.getText(), addUser);
				}
			}
		});
		flexTable.setWidget(8, 0, btnRegister);
		
		Button btnBack = new Button("Back");
		btnBack.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rootPanel.remove(regPanel);
				initLoginPanel();
			}
		});
		flexTable.setWidget(9, 0, btnBack);
   }	
    
	private void initVerifyPanel(){
		verifyPanel = new VerticalPanel();
		Label verifyLabel = new Label("Please enter your email verification code:");
		verifyPanel.add(verifyLabel);
		//for login
		verifyPanel.add(verifyLabel);
		rootPanel.add(verifyPanel);
		FlexTable flexTable = new FlexTable();
		verifyPanel.add(flexTable);
		flexTable.setSize("211px", "88px");
		Label vcode = new Label("Verification code :");
		flexTable.setWidget(0, 0, vcode);
		verifyTextBox = new TextBox();
		flexTable.setWidget(1, 0, verifyTextBox);
		Button btnLogin = new Button("Verify");
		flexTable.setWidget(2, 0, btnLogin);
		Button btnBack = new Button("Back");
		flexTable.setWidget(3, 0, btnBack);			
		btnLogin.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				System.out.println(verifyTextBox.getText());
				System.out.println(account.getVerification());
				if (verifyTextBox.getText().equals(account.getVerification())) {
					//deleteUser = new DeleteUserCallback();
					//account.setVerified(true);
					//serverConnectProxy.deleteUser(account.getUserName(), deleteUser);
					addUser=new AddUserCallback();
					serverConnectProxy.addUser(account.getUserName(), account.getPassWord(), account.getEmail(), true, false, addUser);
					VerifyClickBox diabox = new VerifyClickBox();
					diabox.show();
					rootPanel.remove(verifyPanel);
				}
			}
		});				
		btnBack.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rootPanel.remove(verifyPanel);
				rootPanel.add(loginPanel);
			}
		});
	}

    private class LogoutClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			rootPanel.remove(vPanel);
			initLoginPanel();
			// logout: clear the suggestions list before next user logs in
			suggestionOracle.clear();
			suggestSearchBox.setText("");
			map.clearOverlays();
		}	
    }
    
	private class SearchClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			  try{
				  if ( suggestSearchBox.getText().isEmpty()) { 
                       displayOkBox("Empty Search Entry");
				  }
                  else{
                	  searchString = suggestSearchBox.getText();
                	  // Add search to history of user
                	  addHistory= new AddHistoryCallback ();
                	  serverConnectProxy.addHistory(userNameTextBox.getText(), searchString, addHistory);
                	  // Add to suggestive search
                	  suggestionOracle.add(searchString); 
                	  searchDistance = Double.parseDouble(distanceBox.getText());
                	  map.clearOverlays();
                	  serverConnectProxy.searchCrime(searchString,searchDistance,getCrimes);
                  }
			  }
              catch(NumberFormatException e) {
            	  displayOkBox("Invalid Radius!");
              }
			serverConnectProxy.searchCrime(searchString, searchDistance ,getCrimes);
		}
	}	
	
	private class GetCrimeAsyncCallback implements AsyncCallback<ArrayList<Crime>> {
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
        public void onSuccess(ArrayList<Crime> result) {
             listAddressPanel.clear();
             if (result == null){
            	 displayOkBox("Server unable to interpret address");
             }
             else {
	             Crime userInput = result.get(0);
	             LatLng loc = LatLng.newInstance(userInput.getLat(), userInput.getLong());
	             Marker m = new Marker(loc);
	             displayInfoWindow(result.get(0));
	             map.addOverlay(m);
	             for (final Crime c: result){
	                  loc = LatLng.newInstance(c.getLat(), c.getLong());
	                  m = new Marker(loc);
	                  m.addMarkerClickHandler(new MarkerClickHandler() {
	                       @Override
	                       public void onClick(MarkerClickEvent event) {
	                            displayInfoWindow(c);                         
	                       }
	                 });
	                 map.addOverlay(m);
	                 if (listAddressCheckBox.getValue() == true){
	                 final Label cLabel = new Label(c.getFormattedAddress());
	                 listAddressPanel.add(cLabel);
	                 }
	             }
	             vPanel.add(listAddressPanel);
             }
		}
	}
	
	private class GetUserCallback implements AsyncCallback<Account>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Account result) {
			if (result.getUserName()!=null){
				if ((passwordTextBox.getText()).equals(result.getPassWord())){
					if (result.isVerified()){
						account = new Account(result);
						rootPanel.remove(loginPanel);
						if (rootPanel.getWidgetIndex(verifyPanel)!=-1){
							rootPanel.remove(verifyPanel);
						}
						rootPanel.add(vPanel);
					}
					else {
						account = new Account(result);
						rootPanel.remove(loginPanel);
						initVerifyPanel();
					}
				}
				else {
					displayOkBox("Incorrect password.");
				}
			}
			
			else {
				displayOkBox("User not found.");
			}
			// TODO Auto-generated method stub
		}
	};	
	
	private class AddUserCallback implements AsyncCallback<java.lang.Boolean>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Boolean result) {
			if (account.getUserName() == null){
			AsyncCallback<java.lang.Boolean>sendMail=new SendMailCallback();
			serverConnectProxy.sendMail(userNameTextBox.getText() + passwordTextBox.getText(), emailTextBox.getText(), sendMail);
		}}
	};	
	
	private class DeleteUserCallback implements AsyncCallback<java.lang.Boolean>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Boolean result) {
			// TODO Auto-generated method stub	
		}
	};	

	private class GetHistoryCallback implements AsyncCallback<ArrayList<History>> {
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(ArrayList<History> result) {
			// TODO Auto-generated method stub
			if(!result.isEmpty()) {
				for(History h : result) {
					suggestionOracle.add(h.getAddress());
				}
			}
		}
	};	
	
	private class AddHistoryCallback implements AsyncCallback<java.lang.Boolean>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Boolean result) {
			// TODO Auto-generated method stub
		}
	};	
	
	private class DeleteHistoryCallback implements AsyncCallback<java.lang.Boolean>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Boolean result) {
			// TODO Auto-generated method stub	
		}
	};
	
	private class SendMailCallback implements AsyncCallback<java.lang.Boolean>{
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onSuccess(Boolean result) {
			displayOkBox("Register succeed.");
			rootPanel.remove(regPanel);
			initLoginPanel();
		}
	};
	
    private class VerifyClickBox extends DialogBox {
    public VerifyClickBox(){
       setGlassEnabled(true);
       this.setText("Verification in progress, click OK to continue..");
        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
           public void onClick(ClickEvent event) {
        	   serverConnectProxy.getUser( account.getUserName(), getUser);
        	   VerifyClickBox.this.hide();
           }
        });
          VerticalPanel panel = new VerticalPanel();
          panel.setHeight("100");
          panel.setWidth("300");
          panel.setSpacing(10);
          panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
          panel.add(ok);
          setWidget(panel);}
     }




	
	
	
}


