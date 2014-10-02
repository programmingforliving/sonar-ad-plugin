---
title: Configuration
permalink: /config/
---

Active Directory will work without any configuration as the plugin discovers the Active Directory domain and associated authentication servers for connecting to Active Directory using the FQN of the host where the Sonar application is running. Plugin also allow the user to configure few parameters.  

### Active Directory Domain Configuration

The auto-discovery process of the plugin identifies the Active Directory authentication domain from the FQN of the Sonar server host. If your host domain is different from the authentication domain, then you can configure the FQN of the autentication domain in `sonar.properties` with property `sonar.ad.domain`

~~~ 
sonar.ad.domain: ad.mycompany.com
~~~

