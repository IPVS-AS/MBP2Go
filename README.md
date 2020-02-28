# MBP2Go
Mobile client application for the [Multi-purpose Binding and Provisioning Platform (MBP)](https://github.com/IPVS-AS/MBP).

<img src="resources/mbp-app-ui.jpg" alt="drawing" width="300"/>

## Installation

For Developers:
1: Install Android Studio https://developer.android.com/studio/

2: Download the project MBP2Go and open the directory MBP2Go in Android Studio.


For Users:
1: Download the project MBP2Go

2: Transfer the file MBP2Go/MBP2Go/app/build/outputs/apkapp-debug.apk
to your smartphone. 

3: Open this file in your smartphone - and have fun!

## QR-Code Template Example

```json
{  
   "type":"RaspberryPi",
   "name":"RaspberryPi",
   "macAddress":"1234",
   "ipAddress":"192.0.2.1",
   "user":"username",
   "password":"password",
   "sensors":[  
      {  
         "sensorname":"TemperatureLivingRoom",
         "pinset":"123",
         "sensoradapter":"TemperatureAdapter",
         "sensortype":"Temperature"
      },
      {  
         "sensorname":"LightSensor",
         "pinset":"123",
         "sensoradapter":"LightAdapter",
         "sensortype":"Light Flicker"
      }
   ],
"actuators":[  
      {  
         "actuatorname":"Speaker",
         "pinset":"123",
         "actuatoradapter":"Speaker-MQTT",
         "actuatortype":"Speaker"
      }
   ]}
```

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.
