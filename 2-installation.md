---
title: Installation
permalink: /installation/
---

Installation of the plugin is straight forward. Please follow the below instructions

1.  Download the plugin jar from the [Downloads]({{ site.baseurl }}/download)
2.  Drop the plugin **`sonar-ad-plugin-x.x.jar`** to Sonar plugin directory (**`$SONARQUBE_INSTALL\extensions\plugins`**).
3.  Add the following to sonar config (**`$SONARQUBE_INSTALL\conf\sonar.properties`**)
        
    ~~~
        sonar.security.realm: AD
        sonar.authenticator.createUsers: true  
    ~~~

4.  Restart Sonar application

**`$SONARQUBE_INSTALL`** points to the directory where you have installed your Sonar Application.

These steps will enable Active Directory configuration in Sonar and you would be able to login to Sonar with your Active Directory credentials.

The current implementation of the plugin by pass the Active Directory authentication for the default Sonar Administrator user `admin`. So, you would be able to login to Sonar using `admin` with its default password even after the Active Directory plugin is installed.
