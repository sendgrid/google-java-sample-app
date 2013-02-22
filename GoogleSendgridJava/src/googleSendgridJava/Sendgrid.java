package googleSendgridJava;

import java.net.HttpURLConnection;
import java.util.*;
import java.io.IOException;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONArray;


public class Sendgrid {
    
    private String from,
                   subject,
                   text,
                   html;
    public String message = "";
    private ArrayList<String> to_list;

    protected String domain = "http://sendgrid.com/",
                     endpoint= "api/mail.send.json",
                     username,
                     password;
    
    Sendgrid(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * getTos - Return the list of recipients
     * 
     * @return List of recipients
     */
    public ArrayList<String> getTos() {
        return this.to_list;
    }
    
    /**
     * setTo - Initialize a single email for the recipient 'to' field
     * Destroy previous recipient 'to' data.
     * 
     * @param    email   A list of email addresses
     * @return           The SendGrid object.
     */
    public Sendgrid setTo(String email) {
        this.to_list = new ArrayList<String>();
        this.to_list.add(email);
        
        return this;
    }
    
    /**
     * addTo - Append an email address to the existing list of addresses
     * Preserve previous recipient 'to' data.
     * 
     * @param    email   Recipient email address
     * @param    name    Recipient name
     * @return           The SendGrid object.
     */
    public Sendgrid addTo(String email, String name) {
        String toAddress = (name.length() > 0) ? name + "<" + email + ">" : email;
        this.to_list.add(toAddress);
     
        return this;
    }
    
    /**
     * Make the second parameter("name") of "addTo" method optional
     * 
     * @param   email   A single email address 
     * @return          The SendGrid object.  
     */
    public Sendgrid addTo(String email) {
        return addTo(email, "");       
    }
    
    /**
     * getFrom - Get the from email address
     * 
     * @return  The from email address
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * setFrom - Set the from email
     * 
     * @param    email   An email address
     * @return           The SendGrid object.
     */
    public Sendgrid setFrom(String email) {
        this.from = email;
      
        return this;
    }

    /** 
     * getSubject - Get the email subject
     * 
     * @return  The email subject
     */
    public String getSubject() {
        return this.subject;
    }

    /** 
     * setSubject - Set the email subject
     * 
     * @param    subject  The email subject
     * @return            The SendGrid object
     */
    public Sendgrid setSubject(String subject) {
        this.subject = subject;
      
        return this;
    }

    /** 
     * getText - Get the plain text part of the email
     * 
     * @return   the plain text part of the email
     */
    public String getText() {
        return this.text;
    }

    /** 
     * setText - Set the plain text part of the email
     * 
     * @param   text  The plain text of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setText(String text) {
        this.text = text;
      
        return this;
    }
    
    /** 
     * getHtml - Get the HTML part of the email
     * 
     * @return   The HTML part of the email.
     */
    public String getHtml() {
        return this.html;
    }

    /** 
     * setHTML - Set the HTML part of the email
     * 
     * @param   html   The HTML part of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setHtml(String html) {
        this.html = html;
        
        return this;
    }

    /**
     * _prepMessageData - Takes the mail message and returns a url friendly querystring
     * 
     * @return String - the data query string to be posted
     * @throws JSONException 
     */
    protected Map<String, String> _prepMessageData() throws JSONException {
        Map<String,String> params = new HashMap<String, String>();
      
        params.put("api_user", this.username);
        params.put("api_key", this.password);
        params.put("subject", this.getSubject());
        params.put("html", this.getHtml());
        params.put("text",this.getText());
        params.put("from", this.getFrom());

        if(this.getTos().size() > 1) {
            Map<String, ArrayList<String>> smtpapi = new HashMap<String, ArrayList<String>>();
            smtpapi.put("to", this.getTos());
            JSONObject smtpJson = new JSONObject(smtpapi);
            params.put("x-smtpapi", smtpJson.toString());
            params.put("to", this.getFrom());
        } else {
            params.put("to", this.getTos().get(0));
        }
        
        return params;
    }

    /**
     * send - Send an email
     * 
     * @throws IOException 
     * @throws JSONException 
     */
    public void send() throws IOException, JSONException {
        Map<String,String> data = new HashMap<String, String>();
      
        data = this._prepMessageData();
        StringBuffer requestParams = new StringBuffer();
        Iterator<String> paramIterator = data.keySet().iterator();
        while (paramIterator.hasNext()) {
            String key = paramIterator.next();
            String value = data.get(key);
            requestParams.append(URLEncoder.encode(key, "UTF-8"));
            requestParams.append("=");
            requestParams.append(URLEncoder.encode(value, "UTF-8"));
            requestParams.append("&");
        }
        String request = this.domain + this.endpoint;

        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestParams.toString());
          
            // Get the response
            writer.flush(); 
           
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
            String line, response = "";
          
            while ((line = reader.readLine()) != null) { 
                // Process line...
                response += line;
            }
            reader.close();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                message = "success";
            } else {
                // Server returned HTTP error code.
                JSONObject apiResponse = new JSONObject(response);
                JSONArray errorsObj = (JSONArray) apiResponse.get("errors");
                for (int i = 0; i < errorsObj.length(); i++) {
                	if (i != 0) {
                		message += ", ";
                	}
                	message += errorsObj.get(i);
                }
            }
        } catch (MalformedURLException e) {
            message = "MalformedURLException - " + e.getMessage();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            message = "IOException - " + e.getMessage();
            System.out.println(e.getMessage());
        }
    }
}
