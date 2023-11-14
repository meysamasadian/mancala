# Mancala Game

![Mancala]

## Table of Contents
- [About](#about)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)

## About

Mancala is a classic board game that has been implemented in this project. In this project, I have tired to develop very simple online version of game where 2 online competitors who have already signed up in the app, after requesting to start a game, will randomly be inivited to a competition.

## Features

- Online Game!
  - As a  first version it's an online game, which two player through the web can be invited to copmetite together.
- Monolothic, but modular!
  - Since it's the first version of this project, I develop it as a monolith service. But the modules of the game have been split from each other(either through event-driven design or Facade design pattern). This matter will let us to break it down into some microservices in the future.
- Almost Stateless
  - I have tried to design the project stateless as much as possible. The player module and Game module are full stateless. This feature lets us scale up project smoothly. There's only module which called "timing" which is designed based Actor-system which is in charge of time management. This part is also designed so that we can either scale it up or replace it wuth the other stateless time management mechanism.
- Distributed lock:
  - To manage the race conditions and game turn management, I have used a distributed lock based on redis. This matter lets us easily scale up the project.
- Time management:
  - To make the game a bit existing, I designed a timing limitation which forces each player to move the stones within 30 seconds, otherwise, their turn will be over and switched to their competitor.  

## Getting Started

To deploy the game, please follow the steps below:

### Prerequisites

- First make sure you're using the 21 version of java. If you don't have it installed on your system, follow this link(https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html#GUID-8677A77F-231A-40F7-98B9-1FD0B48C346A)
- Second, ensure you have a docker dashboard on your system, otherwise follow this link(https://docs.docker.com/engine/install/)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/meysamasadian/mancala.git

2. Run these two command inside of the main directory of project:

  ```bash
  chmod +x ./backend/mancala/mvnw

  ./backend/mancala/mvnw clean install


With these command you will create the backend JAR file.

3. Run the below command to create/downlaod the required docker images(run it in the main directory as well)

  ```bash
  docker-compose build

This step may take few minutes.

4. As final round, run the below command to deploy and run whole project and its 3rd-party tools.

  ```bash
  docker-compose up -d

When this step finished, make sure all containers are up by checking the docker dashboard in your system or by run:

  ```bash  
  docker ps

like the below images:
{image}

In order to enter the game, open your browser, and look for the below  address:
  
http://localhost:3050

If all steps are successfully completed, you will see the below image:
{image}

Enter your user/pass if you already have an account, otherwise, follow the highlighted link to create an account.
{image}

Choose start gane and wait for competitor. (To test it, open another browser and login by another account to join the current game)
{image}
{image}





