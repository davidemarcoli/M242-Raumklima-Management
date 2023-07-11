# TBZ School Project: Temperature and Humidity Measurement Service

This GitHub repository contains the code and resources for the TBZ school project focused on microcontrollers (M5 Stack) and building a temperature and humidity measurement service using MQTT communication.

## Project Overview
The goal of this project was to create a service that measures temperature and humidity using M5 controllers and communicates with them using MQTT. The project consists of three main components:

1. **environment-meter**: This folder contains the C++ code for the M5 controller. It includes the necessary code to read data from sensors and display the current temperature and humidity on the device. Additionally, it  displays the average values of temperature and humidity for all devices connected to the same service.

2. **environment-server**: The `environment-server` folder contains the Java Spring Boot backend code. This component calculates the average temperature and humidity values and stores the data in InfluxDB. It acts as the central server for receiving and processing data from the M5 controllers.

3. **discord_bot**: In this folder, you can find the implementation of a Discord bot that allows users to access the temperature and humidity data. The bot responds to commands, such as requesting the current temperature and humidity or applying filters for specific time intervals (e.g., `/temperature in last 48 hours`).

## Data Storage and Dashboards
We utilized InfluxDB to store our collected data within a feasible timeframe. The data can be accessed through the InfluxDB dashboard at the following URL: [InfluxDB Dashboard](http://5.182.206.197:8086/orgs/e8cc2f215d487289/dashboards/0b7bb0f35b099000?lower=now%28%29+-+1h).

In addition, we incorporated Grafana as a third-party dashboard service for visualizing the data. You can access our Grafana dashboard via the following link: [Grafana Dashboard](https://server.davidemarcoli.dev/d/a5c0b3cc-81cd-4d5d-98f6-d3f6f4c2dff7/env-iii?orgId=1).

## Getting Started
### environment-meter

The `environment-meter` folder contains the C++ code for the M5 controller. To get started with this component, follow the instructions below:

1. Install [PlatformIO](https://platformio.org/platformio-ide) on your development machine.

2. Connect your M5 controller to your computer.

3. Open the `environment-meter` folder in PlatformIO.

4. Build and upload the code to your M5 controller using the PlatformIO IDE.

5. Once uploaded, the M5 controller will start measuring temperature and humidity data and display it on the device. It will also display the average values for all devices connected to the same service.

### environment-server

The `environment-server` folder contains the backend code developed with Maven and Spring Boot. To set up and run the environment server, follow the steps below:

1. Make sure you have Java Development Kit (JDK) installed on your machine. You can download it from the [Oracle website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

2. Open the `environment-server` folder in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).

3. Build the project using Maven to resolve dependencies and compile the code.

4. Configure the database connection settings in the `application.properties` file.

5. Run the Spring Boot application, which will start the environment server.

### discord_bot

The `discord_bot` folder contains the Node.js implementation of the Discord bot. To set up and run the bot, follow these instructions:

1. Make sure you have [Node.js](https://nodejs.org) installed on your machine.

2. Open a terminal and navigate to the `discord_bot` folder.

3. Install the required dependencies by running the following command:
   ```
   npm install
   ```

4. Create a new Discord bot application and obtain the bot token. You can follow the official Discord developer documentation to create a bot and get the token.

5. Rename the `config.json.template` file to `config.json` and update the variables.

6. Start the Discord bot by running the following command:
   ```
   node ./index.js
   ```

7. The bot will now be online and connected to your Discord server. You can interact with it by sending commands in the configured channels.

Once the respective components are set up and running, you can start using the temperature and humidity measurement service with your M5 controllers, access the data through the environment server, and interact with the Discord bot for temperature and humidity information.

## License
The project is available under the [MIT License](LICENSE).

## Contributors
- [Lazar Petrovic](https://github.com/lazarpe)
- [Manuel Andres](https://github.com/EnderDark1010)
- [Davide Marcoli](https://github.com/davidemarcoli)

We appreciate any contributions and feedback from the community. Feel free to create issues or submit pull requests.
