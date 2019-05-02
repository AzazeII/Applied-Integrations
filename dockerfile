# @Author Azazell
# This file is used to test mod in dockerized container space
# If you don't want to use dockerized space to run this mod, then just ignore this file

FROM ubuntu:18.04

#-- PRE LOAD --#
# Prepare apt
RUN apt-get -y update

# Install Java
RUN apt-get -y install openjdk-8-jre
RUN apt-get -y install openjdk-8-jdk

# Install wget
RUN apt-get -y install wget

# Install unzip
RUN apt-get -y install unzip

# Install git
RUN apt-get -y install git-core
#-- PRE LOAD --#

# Prepare repository
RUN git clone https://github.com/AzazeII/Applied-Integrations.git

# Change directory
WORKDIR Applied-Integrations

# Install gradle wrapper and dependencies
RUN ./gradlew dependencies
