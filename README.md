Github Login Hook
=================

This application is a Liferay Portal hook that allows users to log in to a Liferay Portal instance with their Github account.

The hook leverages the OAuth 2.0 protocol, which is available by the [Github API](https://developer.github.com/v3/), in order to perform the users' authentication.

The installation and setup of this hook is easy and comprises three simple steps:

  - [A. Hook Installation](#installation)
  - [B. Registration of a Github OAuth Application](#registration)
  - [C. Hook's Secrets Configuration](#configuration)

These steps are thoroughly described below.

<h2 id="installation">A. Hook Installation</h2>

First of all, you have to install (i.e. deploy) the hook to your Liferay Portal instance. 
In order to install the hook, you may follow one of the three recommended ways described in the Liferay Guide, which can be found [here](https://dev.liferay.com/discover/portal/-/knowledge_base/6-2/downloading-and-installing-apps).
In brief, these three alternative ways are presented below:

 1. **Installing through the Liferay Control Panel**
 
    - Download the .lpkg (or .war) file of the hook.
	- Start your Liferay Portal.
	- Log in as an Administrator.
	- Navigate to Control Panel -> Apps -> Install.
	- Click on the *"Choose File"* button and select the .lpkg (or .war) file of the hook.
	- Click *"Install"*.
	
 <p align="center">
	<img align="center" src="/images/github-install-hook.jpg" />
 </p>

	
 2. **Installing through Liferay Hot Deploy**
 
    - Download the .lpkg (or .war) file of the hook.
	- Copy this file.
	- Navigate to the folder named *"deploy"* of your Liferay Portal's home directory.
	- Paste the file into this folder.
	- Start your Liferay Portal.
	
 3. **Installing through Liferay IDE for Eclipse**
 
    - Download or clone the hook's source code.
	- Import the source code into Eclipse (i.e. select the option *"New Liferay Project from Existing Source"*).
	- Deploy the hook to your Liferay Portal instance.
	
Regardless the installation alternative you may have chosen, you should see the Github icon below the "Sign In" button of 
your portal's Welcome Page, if the deployment was successful. 

 <p align="center" width="256" height="147">
	<img align="center" src="/images/github-sign-in-page.jpg" />
 </p>

-----------
**Attention:**

If you click on the Github icon before creating a Github OAuth application and before setting its secrets 
into the hook's *"github_secrets.json"* file (i.e. if you omit the steps [B](#registration) and [C](#configuration) of the overall setup procedure), you will receive a *404 Not Found* response.  	

 <p align="center" width="128" height="73">
	<img align="center" src="/images/github-error-404.jpg" />
 </p>
-------------

<h2 id="registration">B. Registration of a Github OAuth Application</h2>

The next step of the installation process is to create a new Github OAuth application that will authorize
the hook to have access to the users' public information, such as their name and email address. In order to 
register a new Github OAuth application, you should follow the following steps:

 1. Navigate to your Github profile's settings ([link](https://github.com/settings/profile)).
 
 2. In the left column, under the *"Developer settings"* section, click on the *"OAuth applications"* option.
 
 3. On the web page that shows up, click on the *"Register a new application"* button. The following web page appears on your screen:
 
  <p align="center">
	<img align="center" src="/images/github-register-app.jpg" />
  </p>

 4. Add a descriptive name for your application (here MyGithubLoginApp) and define:
     - the homepage of your portal (e.g. http://localhost:8080).
	 - the redirect URI that matches your portal settings (e.g. http://localhost:8080/c/portal/github_login?cmd=token).
	 
 5. Click on the *"Register application"* button in order to create your desired application and generate its secrets (i.e. *"Client ID"* and *"Client Secret"* values). Upon the selection of this button you will be redirected to the application's page:
 
 <p align="center">
	<img align="center" src="/images/github-app-info.jpg" />
 </p> 
 
 6. Copy the values of the *"Client ID"* and *"Client Secret"* fields, as they are necessary for the last step of the hook's set up.
 
 
<h2 id="configuration">C. Hook's Secrets Configuration</h2>

Now that you have created a Github OAuth application and you have its *"Client ID"* and *"Client Secret"* values at your disposal,
you have to set them into the Github Login Hook. The process is simple:

 1. Deploy the Github Login Hook to your Liferay Portal instance following one of the three alternatives described in section A (i.e. [A. Hook Installation](#installation)).
 
 2. Navigate to your *Liferay portal's home folder* and then to the *tomcat directory*. Inside the tomcat directory open the *"webapps"* folder and then the *"github-login-hook"* folder, which is created after the hook's deployment. The exact path should look like: 
   
   ```
     path/to/liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42/webapps/github-login-hook
    ```
	
 3. Inside the hook's folder you will find a JSON file named *"github_secrets.json"*.
 
 4. Open this file with your preferable text editor. You will find two empty entries named *client_id* and *client_secret* as shown below:
 
   ```
   {
	"client_id":"",
	"client_secret":""
	}
   ```
 
 5. Copy the *Client ID* and *Client Secret* values of your Github OAuth application, which were generated in the previous [step](#registration) and paste them to the appropriate fields of the *"github_secrets.json"* file (i.e. to the fields with the same name). The final form of the *"github_secrets.json"* file should be similar to the following example:
 
   ```
   {
	"client_id":"a40cf0aa01be103a",
	"client_secret":"329bc53b032e65b6282e8b34dc9d5088"
    }
   ```
   
 **Attention:** Make sure that you don't delete any of the quotation marks during the copy paste process.
 
 6. Save the file and close the editor.
 
Now the hook should be able to allow the users log in to your Liferay Portal instance with their Github account.

## Attention for deployment through Eclipse Liferay IDE

If you try to deploy the Github Login Hook through Eclipse Liferay IDE do not use the *"github_secrets.json"* file found inside the docroot folder. Instead navigate to your Eclipse home directory and create the folder path "webapps/github-login-hook". 
Inside this folder place the *"github_secrets.json"* file containing your Github OAuth application's secrets as 
presented in [Section C](#configuration).

## Important Points

 - The authentication is based on the users' email address in order to ensure interoperability with other similar hooks. 
 
