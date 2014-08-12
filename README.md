Sonar AD Plugin
===============

It is a simple plugin to integrate Sonar with Active Directory for authentication.

Features
--------
1. AD Configuration autodiscovery (zero configuration in Sonar)
2. AD Authentication


Installation 
------------ 
1. Drop the plugin **sonar-ad-plugin** jar to Sonar plugin directory (**&lt;Sonarqube Home&gt;\extensions\plugins**).
2. Add the following to sonar config (**&lt;Sonarqube Home&gt;\conf\sonar.properties**) & Restart Sonar
```properties
	sonar.security.realm: AD
	sonar.authenticator.createUsers: true  
```


Future Enhancements
-------------------
1. Groups Retrieval from Active Directory


History
-------
*  0.2  Changes to [bypass AD for Sonar default user 'admin'](https://github.com/programmingforliving/sonar-ad-plugin/issues/1) - Credits to [Frieder Bluemle](https://github.com/friederbluemle)
*  0.1  Initial version

[ ![Codeship Status for programmingforliving/sonar-ad-plugin](https://codeship.io/projects/4126c120-03a3-0132-04bb-1a827ae27d2a/status)](https://codeship.io/projects/30504)
