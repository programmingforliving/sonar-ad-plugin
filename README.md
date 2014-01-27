Sonar AD Plugin
===============

It is a simple plugin to integrate Sonar with Active Directory for authentication.

How to use? 
----------- 
1. Drop the plugin 'sonar-ad-plugin-0.1.jar' to Sonar plugin directory ('<sonarqube home>\extensions\plugins').
2. Add the following to sonar config ('<sonarqube home>\conf\sonar.properties')
```bash
sonar.security.realm: AD
sonar.authenticator.createUsers: true  
```
3. Restart Sonar
